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
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ideabus.mylibrary.code.bean.*
import com.ideabus.mylibrary.code.callback.*
import com.ideabus.mylibrary.code.connect.ContecSdk
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


class MainActivity2 : AppCompatActivity() {

    companion object {
        var DEVICE_ADDRESS: String = "DEVICE_ADDRESS"
        var DEVICE_NAME: String = "DEVICE_NAME"
    }
    
    private var finish_timer: Timer? = null
    
    private lateinit var device_name: String
    private lateinit var device_address: String

    private var connected: Boolean = false

    private var bluetooth: BluetoothAdapter? = null
    private var bluetoothOK: Boolean = false
    private lateinit var sdk: ContecSdk

    private lateinit var connectBtn: Button
    private lateinit var disconnectBtn: Button
    private lateinit var communicateBtn: Button
    private lateinit var startRealtimeBtn: Button
    private lateinit var startSpo2RealtimeBtn: Button
    private lateinit var stopRealtimeBtn: Button
    private lateinit var getStorageModeBtn: Button
    private lateinit var setDataStorageInfoBtn: Button
    private lateinit var setDeviceStorageModeBtn: Button
    private lateinit var setCalorieBtn: Button
    private lateinit var setWeightBtn: Button
    private lateinit var setStepsTimeBtn: Button
    private lateinit var deleteDataBtn: Button

    private var locationmanager: LocationManager? = null

    fun check_multiple_perms(vararg perms: String): Boolean {
        val perms_array: ArrayList<String> = ArrayList()
        for (perm in perms) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity2,
                    perm,
                ) != PackageManager.PERMISSION_GRANTED) {
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

        device_name = intent.getStringExtra(DEVICE_NAME)!!
        device_address = intent.getStringExtra(DEVICE_ADDRESS)!!

        title = device_name

        sdk = ContecSdk(this)
        sdk.init(true)

        val sys_service = getSystemService(Context.BLUETOOTH_SERVICE)
        bluetooth = (if (sys_service is BluetoothManager) sys_service else null)?.adapter

        initLayout()
        initButtons()
        init_bluetooth()
    }

    private fun initLayout() {
        setContentView(R.layout.activity_main2)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initButtons() {
        connectBtn = findViewById(R.id.connect)
        disconnectBtn = findViewById(R.id.disconnect)
        communicateBtn = findViewById(R.id.communicate)
        startRealtimeBtn = findViewById(R.id.start_realtime)
        startSpo2RealtimeBtn = findViewById(R.id.start_spo2_realtime)
        stopRealtimeBtn = findViewById(R.id.stop_realtime)
        getStorageModeBtn = findViewById(R.id.get_storage_mode)
        setDataStorageInfoBtn = findViewById(R.id.set_data_storage_info)
        setDeviceStorageModeBtn = findViewById(R.id.set_device_storage_mode)
        setCalorieBtn = findViewById(R.id.set_calorie)
        setWeightBtn = findViewById(R.id.set_weight)
        setStepsTimeBtn = findViewById(R.id.set_steps_time)
        deleteDataBtn = findViewById(R.id.delete_data)

        connectBtn.setOnClickListener {
            if (bluetoothOK)
                sdk.connect(device_address, object : ConnectCallback {
                    override fun onConnectStatus(p0: Int) {
                        runOnUiThread {
                            when (p0) {
                                SdkConstants.CONNECT_UNSUPPORT_DEVICETYPE -> {
                                    Log.e("onConnectStatus", "CONNECT_UNSUPPORT_DEVICETYPE!")
                                }
                                SdkConstants.CONNECT_UNSUPPORT_BLUETOOTHTYPE -> {
                                    Log.e("onConnectStatus", "CONNECT_UNSUPPORT_BLUETOOTHTYPE!")
                                }
                                SdkConstants.CONNECT_CONNECTING -> {
                                    Log.e("onConnectStatus", "CONNECT_CONNECTING...")
                                }
                                SdkConstants.CONNECT_CONNECTED -> {
                                    Log.e("onConnectStatus", "CONNECT_CONNECTED!")
                                    connected = true
                                }
                                SdkConstants.CONNECT_DISCONNECTED -> {
                                    Log.e("onConnectStatus", "Disconnecting")
                                    connected = false
                                }
                                SdkConstants.CONNECT_DISCONNECT_SERVICE_UNFOUND -> {
                                    Log.e("onConnectStatus", "No service found, Disconnecting")
                                    connected = false
                                }
                                SdkConstants.CONNECT_DISCONNECT_NOTIFY_FAIL -> {
                                    Log.e("onConnectStatus", "Monitoring failed, Disconnecting")
                                    connected = false
                                }
                                SdkConstants.CONNECT_DISCONNECT_EXCEPTION -> {
                                    Log.e("onConnectStatus", "Abnormal disconnection")
                                    connected = false
                                }
                            }
                        }
                    }
                    override fun onOpenStatus(p0: Int) {
                        when (p0) {
                            SdkConstants.OPEN_SUCCESS -> {
                                Log.e("onOpenStatus", "OPEN_SUCCESS")
                            }
                            SdkConstants.OPENED -> {
                                Log.e("onOpenStatus", "OPENED")
                            }
                            SdkConstants.OPEN_FAIL -> {
                                Log.e("onOpenStatus", "OPEN_FAIL")
                            }
                        }
                    }
                })
        }
        disconnectBtn.setOnClickListener {
            if (connected)
                sdk.disconnect()
        }
        communicateBtn.setOnClickListener {
            if (connected)
                sdk.communicate(object : CommunicateCallback {
                    override fun onFail(errorCode: Int) {
                        Log.e("onFail", errorCode.toString())
                    }

                    override fun onPointSpO2DataResult(spO2PointData: java.util.ArrayList<SpO2PointData>?) {
                        for (r in spO2PointData!!) {
                            val m: HashMap<String, Any> = HashMap()
                            m["date"] = r.date
                            m["prData"] = r.prData
                            m["spo2Data"] = r.spo2Data
                            Log.e("onPointSpO2DataResult", m.toString())
                        }
                    }

                    override fun onDayStepsDataResult(dayStepsData: java.util.ArrayList<DayStepsData>?) {
                        for (r in dayStepsData!!) {
                            val m: HashMap<String, Any> = HashMap()
                            r.calorie
                            r.stepCount
                            r.targetCalories
                            m["date"] = "${r.day}.${r.month}.${r.year}"
                            m["calorie"] = r.calorie
                            m["stepCount"] = r.stepCount
                            m["targetCalories"] = r.targetCalories
                            Log.e("onDayStepsDataResult", m.toString())
                        }
                    }

                    override fun onFiveMinStepsDataResult(fiveMinStepsData: java.util.ArrayList<FiveMinStepsData>?) {
                        for (r in fiveMinStepsData!!) {
                            val m: HashMap<String, Any> = HashMap()
                            m["date"] = "${r.day}.${r.month}.${r.year}"
                            m["length"] = r.length
                            m["stepFiveDataBean"] = r.stepFiveDataBean
                            Log.e("onDayStepsDataResult", m.toString())
                        }
                    }

                    override fun onEachPieceDataResult(pieceData: PieceData?) {
                        if (pieceData != null) {
                            val m: HashMap<String, Any> = HashMap()
                            m["dataType"] = pieceData.dataType
                            m["totalNumber"] = pieceData.totalNumber
                            m["caseCount"] = pieceData.caseCount
                            m["supportPI"] = pieceData.supportPI
                            m["length"] = pieceData.length
                            m["startTime"] = pieceData.startTime
                            m["spo2Data"] = pieceData.spo2Data
                            m["prData"] = pieceData.prData
                            m["piData"] = pieceData.piData
                            Log.e("onDayStepsDataResult", m.toString())
                        }
                    }

                    override fun onEachEcgDataResult(ecgData: EcgData?) {
                        if (ecgData != null) {
                            val m: HashMap<String, Any> = HashMap()
                            m["uploadCount"] = ecgData.uploadCount
                            m["currentCount"] = ecgData.currentCount
                            m["date"] = "${ecgData.day}.${ecgData.month}.${ecgData.year} ${ecgData.hour}:${ecgData.min}:${ecgData.sec}"
                            m["pr"] = ecgData.pr
                            m["chineseResult"] = ecgData.chineseResult
                            m["englishResult"] = ecgData.englishResult
                            m["size"] = ecgData.size
                            m["ecgData"] = ecgData.ecgData
                            Log.e("onDayStepsDataResult", m.toString())
                        }
                    }

                    override fun onDataResultEmpty() {
                        Log.e("onDataResultEmpty", "")
                    }

                    override fun onDataResultEnd() {
                        Log.e("onDataResultEnd", "")
                    }

                    override fun onDeleteSuccess() {
                        Log.e("onDeleteSuccess", "")
                    }

                    override fun onDeleteFail() {
                        Log.e("onDeleteFail", "")
                    }

                })
        }
        startRealtimeBtn.setOnClickListener {
            if (connected)
                sdk.startRealtime(object : RealtimeCallback {
                    override fun onFail(errorCode: Int) {
                        Log.e("onFail", errorCode.toString())
                    }

                    override fun onRealtimeWaveData(signal: Int, prSound: Int, waveData: Int, barData: Int, fingerOut: Int) {
                        Log.e("onRealtimeWaveData", "$signal $prSound $waveData $barData $fingerOut")
                    }

                    override fun onSpo2Data(piError: Int, spo2: Int, pr: Int, pi: Int) {
                        Log.e("onSpo2Data", "$piError $spo2 $pr $pi")
                    }

                    override fun onRealtimeEnd() {
                        Log.e("onRealtimeEnd", "")
                    }

                })
        }
        startSpo2RealtimeBtn.setOnClickListener {
            if (connected)
                sdk.startRealtimeSpO2(object : RealtimeSpO2Callback {
                    override fun onFail(errorCode: Int) {
                        Log.e("onFail", errorCode.toString())
                    }

                    override fun onRealtimeSpo2Data(pr: Int, spo2: Int, pi: Int) {
                        Log.e("onSpo2Data", "$pr $spo2 $pi")
                    }

                    override fun onRealtimeSpo2End() {
                        Log.e("onRealtimeSpo2End", "")
                    }

                })
        }
        stopRealtimeBtn.setOnClickListener {
            if (connected)
                sdk.stopRealtime()
        }
        getStorageModeBtn.setOnClickListener {
            if (connected)
                sdk.getDeviceStorageMode(object : GetStorageModeCallback {
                    override fun onFail(errorCode: Int) {
                        Log.e("onFail", errorCode.toString())
                    }

                    override fun onSuccess(storageMode: Int) {
                        Log.e("onSuccess", storageMode.toString())
                    }

                })
        }
        setDataStorageInfoBtn.setOnClickListener {
            if (connected)
                makeTextInputDialog("0: POINTDATAINFO, 1: DAYSTEPSINFO, 2: DAYFIVEMINUTESSTEPSINFO, 3: ECGDATAINFO, 4: PULSEWAVEDATAINFO, 5: WITHSTORAGEINFO, 6: PIECESPO2DATAINFO") { text ->
                    sdk.setDataStorageInfo(
                        when (text) {
                            "0" -> SystemParameter.DataStorageInfo.POINTDATAINFO
                            "1" -> SystemParameter.DataStorageInfo.DAYSTEPSINFO
                            "2" -> SystemParameter.DataStorageInfo.DAYFIVEMINUTESSTEPSINFO
                            "3" -> SystemParameter.DataStorageInfo.ECGDATAINFO
                            "4" -> SystemParameter.DataStorageInfo.PULSEWAVEDATAINFO
                            "5" -> SystemParameter.DataStorageInfo.WITHSTORAGEINFO
                            "6" -> SystemParameter.DataStorageInfo.PIECESPO2DATAINFO
                            else -> null
                        },
                        object : DataStorageInfoCallback {
                            override fun onFail(errorCode: Int) {
                                Log.e("onFail", errorCode.toString())
                            }

                            override fun onSuccess(
                                storageInfo: SystemParameter.DataStorageInfo?,
                                totalNumber: Int
                            ) {
                                Log.e("onSuccess", "$storageInfo $totalNumber")
                            }

                        }
                    )
                }
        }
        setDeviceStorageModeBtn.setOnClickListener {
            if (connected)
                makeTextInputDialog("0: MANUAL, 1: AUTOMATIC") { text ->
                    sdk.setDeviceStorageMode(
                        when (text) {
                            "0" -> SystemParameter.StorageMode.MANUAL
                            "1" -> SystemParameter.StorageMode.AUTOMATIC
                            else -> null
                        },
                        object : StorageModeCallback {
                            override fun onFail(errorCode: Int) {
                                Log.e("onFail", errorCode.toString())
                            }

                            override fun onSuccess() {
                                Log.e("onSuccess", "")
                            }

                        })
                }
        }
        setCalorieBtn.setOnClickListener {
            if (connected)
                makeTextInputDialog("Calorie") { text ->
                    sdk.setCalorie(
                        text.toInt(),
                        text.toInt(),
                        SystemParameter.StepsSensitivity.MIDDLE,
                        object : SetCalorieCallback {
                            override fun onFail(errorCode: Int) {
                                Log.e("onFail", errorCode.toString())
                            }

                            override fun onSuccess() {
                                Log.e("onSuccess", "")
                            }

                        })
                }
        }
        setWeightBtn.setOnClickListener {
            if (connected)
                makeTextInputDialog("Weight") { text ->
                    sdk.setWeight(text.toInt(), object : SetWeightCallback {
                        override fun onFail(errorCode: Int) {
                            Log.e("onFail", errorCode.toString())
                        }

                        override fun onSuccess() {
                            Log.e("onSuccess", "")
                        }

                    })
                }
        }
        setStepsTimeBtn.setOnClickListener {
            if (connected)
                makeTextInputDialog("StepsTime") { text ->
                    sdk.setStepsTime(text.toInt(), text.toInt(), object : SetStepsTimeCallback {
                        override fun onFail(errorCode: Int) {
                            Log.e("onFail", errorCode.toString())
                        }

                        override fun onSuccess() {
                            Log.e("onSuccess", "")
                        }

                    })
                }
        }
        deleteDataBtn.setOnClickListener {
            if (connected)
                sdk.deleteData(object : DeleteDataCallback {
                    override fun onFail(errorCode: Int) {
                        Log.e("onFail", errorCode.toString())
                    }

                    override fun onSuccess() {
                        Log.e("onSuccess", "")
                    }
                })
        }
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
        }
    }

    override fun onBackPressed() {
        if (connected) {
            sdk.disconnect()
        }
        super.finish()
    }

    override fun finish() {
        if (connected) {
            sdk.disconnect()
        }
        if (finish_timer == null) {
            finish_timer = Timer("FinishDelay", false)
            finish_timer!!.schedule(3500) {
                super.finish()
            }
        }
    }

    fun makeTextInputDialog(title: String, callback: (text: String) -> Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ -> callback(input.text.toString()) }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
    
}