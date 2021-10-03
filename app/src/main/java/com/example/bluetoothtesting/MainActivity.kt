package com.example.bluetoothtesting

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbutton.presentation.State
import com.ideabus.mylibrary.code.bean.SdkConstants
import com.ideabus.mylibrary.code.callback.BluetoothSearchCallback
import com.ideabus.mylibrary.code.connect.ContecSdk
import com.example.bluetoothtesting.BluetoothList.BluetoothListAdapter
import com.example.bluetoothtesting.BluetoothList.Diffutil


class MainActivity : AppCompatActivity() {

    private val SEARCH_TIMEOUT: Int = 5000

    private var bluetooth: BluetoothAdapter? = null
    private var bluetoothOK: Boolean = false
    private var sdk: ContecSdk? = null

    private var locationmanager: LocationManager? = null

    private var Recyclerview: RecyclerView? = null
    private var btn: CircularProgressButton? = null
    private var stp_btn: AppCompatButton? = null

    private var DeviceAdapter: BluetoothListAdapter? = null
    private var DeviceList: ArrayList<BluetoothDevice> = ArrayList(mutableListOf())

    fun check_multiple_perms(vararg perms: String): Boolean {
        val perms_array: ArrayList<String> = ArrayList()
        for (perm in perms) {
             if (ActivityCompat.checkSelfPermission(
                     this@MainActivity,
                     perm,) != PackageManager.PERMISSION_GRANTED) {
                         Log.e("debug", perm)
                 perms_array.add(perm)
             }
        }
        return if (perms_array.size > 0) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                perms_array.toTypedArray(),
                100
            )
            false
        } else
            true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sdk = ContecSdk(this)
        sdk?.init(false)

        val sys_service = getSystemService(Context.BLUETOOTH_SERVICE)
        bluetooth = (if (sys_service is BluetoothManager) sys_service else null)?.adapter
        
        init_layout()
        init_bluetooth()

    }

    fun init_layout() {
        setContentView(R.layout.activity_main)
        this.window.setBackgroundDrawableResource(R.drawable.run_screen)

        Recyclerview = findViewById(R.id.DeviceList)

        Recyclerview?.layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(
            Recyclerview?.context,
            RecyclerView.VERTICAL
        )

        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(baseContext, R.drawable.list_divider)!!)
        Recyclerview?.addItemDecoration(dividerItemDecoration)

        DeviceAdapter = BluetoothListAdapter(DeviceList)
        DeviceAdapter?.setOnItemClick(object : BluetoothListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                sdk?.stopBluetoothSearch()

                btn!!.revertAnimation()
                stp_btn!!.visibility = INVISIBLE

                val intent = Intent(this@MainActivity, MainActivity2::class.java)

                intent.putExtra(MainActivity2.DEVICE_NAME, DeviceList[position].name)
                intent.putExtra(MainActivity2.DEVICE_ADDRESS, DeviceList[position].address)

                startActivity(intent)
            }
        })
        Recyclerview?.adapter = DeviceAdapter

        btn = findViewById(R.id.main_button)
        stp_btn = findViewById(R.id.stop_button)

        btn!!.setOnClickListener {
            if (bluetoothOK) {
                val calc = DiffUtil.calculateDiff(Diffutil(DeviceList, ArrayList()))
                DeviceList.clear()
                calc.dispatchUpdatesTo(DeviceAdapter!!)
                sdk?.startBluetoothSearch(searchCallback, SEARCH_TIMEOUT)

                btn!!.startAnimation()
                stp_btn!!.visibility = VISIBLE
            }
            else
                init_bluetooth()
        }

        stp_btn!!.setOnClickListener {
            sdk?.stopBluetoothSearch()

            btn!!.revertAnimation()
            stp_btn!!.visibility = INVISIBLE
        }
    }

    fun init_bluetooth() {
        bluetoothOK = false
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(
                applicationContext,
                "Ваше устройство не поддерживает BLE",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (check_multiple_perms(
            Manifest.permission.ACCESS_COARSE_LOCATION
            )) {
            locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            enable_BLE()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            for (r in grantResults.indices)
                if (grantResults[r] == PackageManager.PERMISSION_DENIED) {
                    Log.e("debug", permissions[r])
                    Toast.makeText(
                        applicationContext,
                        "Не выданы все необходимые разрешения, попробуйте ещё раз, нажав на кнопку",
                        Toast.LENGTH_LONG
                    ).show()
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    return
                }
            locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            enable_BLE()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val bluetooth_callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bluetoothOK = true
            if (btn!!.getState() == State.PROGRESS)
                sdk!!.startBluetoothSearch(searchCallback, 5000)
        }
        else {
            Toast.makeText(
                applicationContext,
                "Без включения BLE приложение не будет работать",
                Toast.LENGTH_LONG
            ).show()
            if (btn!!.getState() == State.PROGRESS) {
                btn!!.revertAnimation()
                stp_btn!!.visibility = INVISIBLE
            }
        }
    }

    val location_callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (locationmanager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!)
            enable_BLE()
        else {
            Toast.makeText(
                applicationContext,
                "Без включения местоположения приложение не будет работать",
                Toast.LENGTH_LONG
            ).show()
            if (btn!!.getState() == State.PROGRESS) {
                btn!!.revertAnimation()
                stp_btn!!.visibility = INVISIBLE
            }
        }

    }

    private fun enable_BLE() {
        if (!locationmanager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
            location_callback.launch(
                Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
            )
            return
        }
        if (bluetooth == null) {
            Toast.makeText(
                this, "Не удалось создать Bluetooth адаптер", Toast.LENGTH_LONG
            ).show()
        } else if (!bluetooth?.isEnabled()!!) {
            bluetooth_callback.launch(Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE
            ))
        } else {
            bluetoothOK = true
        }
    }

    var searchCallback: BluetoothSearchCallback = object : BluetoothSearchCallback {
        override fun onDeviceFound(bluetoothDevice: BluetoothDevice, rssi: Int, record: ByteArray) {
            runOnUiThread {
                Log.e("debug", bluetoothDevice.name)
                if (!DeviceList.contains(bluetoothDevice)) {
                    val oldList: ArrayList<BluetoothDevice> = DeviceList.clone() as ArrayList<BluetoothDevice>
                    DeviceList.add(bluetoothDevice)
                    val calc = DiffUtil.calculateDiff(Diffutil(oldList, DeviceList))
                    calc.dispatchUpdatesTo(DeviceAdapter!!)
                }
            }
        }

        override fun onSearchError(errorCode: Int) {
            if (errorCode == SdkConstants.SCAN_FAIL_BT_UNSUPPORT) {
                Toast.makeText(applicationContext, "Это устройство не поддерживает BLE!", Toast.LENGTH_LONG).show()
                btn!!.revertAnimation()
                stp_btn!!.visibility = INVISIBLE
            } else if (errorCode == SdkConstants.SCAN_FAIL_BT_DISABLE) {
                init_bluetooth()
            }
        }

        override fun onSearchComplete() {
            runOnUiThread {
                btn!!.revertAnimation()
                stp_btn!!.visibility = INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        sdk?.stopBluetoothSearch()
        super.onDestroy()
    }
}