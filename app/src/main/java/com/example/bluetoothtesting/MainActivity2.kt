package com.example.bluetoothtesting

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.contec.spo2.code.bean.*
import com.contec.spo2.code.callback.ConnectCallback
import com.contec.spo2.code.callback.RealtimeCallback
import com.contec.spo2.code.connect.ContecSdk
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity2 : AppCompatActivity() {

    companion object {
        var DEVICE_ADDRESS: String = "DEVICE_ADDRESS"
        var DEVICE_NAME: String = "DEVICE_NAME"
    }
    
    private var finish_timer: Timer? = null
    
    private var device_name: String? = null
    private var device_address: String? = null

    private var text1: TextView? = null
    private var text2: TextView? = null
    private var loading: ProgressBar? = null

    private var connected: Boolean = false

    private var bluetooth: BluetoothAdapter? = null
    private var bluetoothOK: Boolean = false
    private var sdk: ContecSdk? = null

    private var locationmanager: LocationManager? = null

    fun check_multiple_perms(vararg perms: String): Boolean {
        val perms_array: ArrayList<String> = ArrayList()
        for (perm in perms) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity2,
                    perm,) != PackageManager.PERMISSION_GRANTED) {
                Log.e("debug", perm)
                perms_array.add(perm)
            }
        }
        return if (perms_array.size > 0) {
            ActivityCompat.requestPermissions(
                this@MainActivity2,
                perms_array.toTypedArray(),
                100
            )
            false
        } else
            true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        device_name = intent.getStringExtra(DEVICE_NAME)
        device_address = intent.getStringExtra(DEVICE_ADDRESS)

        title = device_name

        sdk = ContecSdk(this)
        sdk?.init(false)

        val sys_service = getSystemService(Context.BLUETOOTH_SERVICE)
        bluetooth = (if (sys_service is BluetoothManager) sys_service else null)?.adapter

        init_layout()
        init_bluetooth()
    }

    private fun init_layout() {
        setContentView(R.layout.activity_main2)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        text1 = findViewById(R.id.textView)
        text2 = findViewById(R.id.textView2)

        loading = findViewById(R.id.loading)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
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
                        "Не выданы все необходимые разрешения",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
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
        }
        else {
            Toast.makeText(
                applicationContext,
                "Без включения BLE, приложение не сможет работать",
                Toast.LENGTH_LONG
            ).show()
            finish()
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
            finish()
        }

    }

    private fun enable_BLE() {
        if (!locationmanager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
            location_callback.launch(
                Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
            )
            return
        }
        if (bluetooth == null) {
            Toast.makeText(
                this, "Ошибка получения адаптера Bluetooth устройства", Toast.LENGTH_LONG
            ).show()
            finish()
        } else if (!bluetooth?.isEnabled()!!) {
            bluetooth_callback.launch(
                Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE
            )
            )
        } else {
            bluetoothOK = true
            set_communication()
        }
    }

    fun set_communication() {
        if (
            (device_name?.startsWith("SpO201")!! || device_name?.startsWith("SpO202")!!
                    || device_name?.startsWith("SpO206")!! || device_name
                ?.startsWith("SpO208")!!
                    || device_name?.startsWith("SpO209")!! || device_name
                ?.startsWith("SpO210")!!)
        ) {
            start_communication()
        }
        else {
            Toast.makeText(applicationContext, "Bluetooth device not supported", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun start_communication() {
        sdk?.connect(device_address, object : ConnectCallback {
            override fun onConnectStatus(p0: Int) {
                runOnUiThread {
                    when (p0) {
                        SdkConstants.CONNECT_UNSUPPORT_DEVICETYPE -> {
                            Log.e("debug", "CONNECT_CONNECTED!")
                        }
                        SdkConstants.CONNECT_UNSUPPORT_BLUETOOTHTYPE -> {
                            Log.e("debug", "CONNECT_CONNECTED!")
                        }
                        SdkConstants.CONNECT_CONNECTING -> {
                            Log.e("debug", "CONNECT_CONNECTING...")
                        }
                        SdkConstants.CONNECT_CONNECTED -> {
                            Log.e("debug", "CONNECT_CONNECTED!")
                            connected = true
                            text1?.visibility = View.VISIBLE
                            text2?.visibility = View.VISIBLE
                            loading?.visibility = View.INVISIBLE
                            sdk?.startRealtime(object : RealtimeCallback {
                                override fun onFail(p0: Int) {
                                    runOnUiThread {
                                        Log.e("debug", p0.toString())
                                        Toast.makeText(applicationContext, "Передача данных завершена", Toast.LENGTH_LONG).show()
                                        finish()
                                    }
                                }

                                override fun onRealtimeWaveData(
                                    p0: Int,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int,
                                    p4: Int
                                ) {

                                }

                                override fun onSpo2Data(p0: Int, p1: Int, p2: Int, p3: Int) {
                                    Log.e("spo2Data", "$p0 $p1 $p2 $p3")
                                    runOnUiThread {
                                        text1?.text = ("$p1%")
                                        text2?.text = ("$p2 pm")
                                    }
                                }

                                override fun onRealtimeEnd() {}

                            })
                        }
                        SdkConstants.CONNECT_DISCONNECTED -> {
                            Toast.makeText(
                                applicationContext,
                                "Disconnecting...",
                                Toast.LENGTH_LONG
                            ).show()
                            connected = false
                            finish()
                        }
                        SdkConstants.CONNECT_DISCONNECT_SERVICE_UNFOUND -> {
                            Toast.makeText(
                                applicationContext,
                                "No sevice found, Disconnecting...",
                                Toast.LENGTH_LONG
                            ).show()
                            connected = false
                            finish()
                        }
                        SdkConstants.CONNECT_DISCONNECT_NOTIFY_FAIL -> {
                            Toast.makeText(
                                applicationContext,
                                "Monitoring failed, Disconnecting...",
                                Toast.LENGTH_LONG
                            ).show()
                            connected = false
                            finish()
                        }
                        SdkConstants.CONNECT_DISCONNECT_EXCEPTION -> {
                            Toast.makeText(
                                applicationContext,
                                "Abnormal disconnection...",
                                Toast.LENGTH_LONG
                            ).show()
                            connected = false
                            finish()
                        }
                    }
                }
            }
            override fun onOpenStatus(p0: Int) {}
        })
    }

    override fun onBackPressed() {
        if (connected) {
            sdk?.disconnect()
        }
        super.finish()
    }

    override fun finish() {
        if (connected) {
            sdk?.disconnect()
        }
        if (finish_timer == null) {
            finish_timer = Timer("FinishDelay", false)
            finish_timer!!.schedule(3500) {
                super.finish()
            }
        }
    }

    
}