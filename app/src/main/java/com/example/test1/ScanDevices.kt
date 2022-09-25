package com.example.test1


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.test1.databinding.ActivityMainBinding


class ScanDevices(activityContext: AppCompatActivity, binding: ActivityMainBinding) {
    private var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothLeScanner: BluetoothLeScanner
    private var scanSettings:ScanSettings
    lateinit var device:BluetoothDevice
    private val handler = Handler(Looper.getMainLooper())
    private val filter = ScanFilter.Builder().setDeviceName("ESP-32").build()
    private var devfilters: MutableList<ScanFilter> = ArrayList()
    private var mScanning = false
    private val ScanPeriod: Long = 5000
    var activityContext: AppCompatActivity
    lateinit var binding:ActivityMainBinding
    var names: List<String?> = listOf()
    init {
        this.binding = binding
        this.activityContext = activityContext
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        this.mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
        this.scanSettings = ScanSettings.Builder().build()
        devfilters.add(filter)
    }

     fun scanLeDevice() {
        if (!mScanning) { // Stops scanning after a pre-defined scan period.
            checkPermission()
            handler.postDelayed({
                mScanning = false
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                    activityContext, android.R.layout.simple_list_item_1,names
                )
                binding.devicesListView.adapter=arrayAdapter
                names= listOf()
                mBluetoothLeScanner.stopScan(leScanCallback)
            }, ScanPeriod)
            mScanning = true
            mBluetoothLeScanner.startScan(devfilters,scanSettings,leScanCallback)
        } else {
            checkPermission()
            mScanning = false
            mBluetoothLeScanner.stopScan(leScanCallback)
        }


    }
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            checkPermission()
            device = result.getDevice();
            if (device.name !in names)
                names +=device.name
            //val rssi: String? = device.name
            //Toast.makeText(activityContext, result.device.address, Toast.LENGTH_SHORT).show()
        }


    }
    private val PermissionsLocation = arrayOf(
        "android.permission.BLUETOOTH_SCAN",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.BLUETOOTH_PRIVILEGED",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"

    )
    private fun checkPermission() {
        val permission1 = ActivityCompat.checkSelfPermission(
            (activityContext),
            "android.permission.BLUETOOTH_SCAN"
        )
        val permission2 = ActivityCompat.checkSelfPermission(
            (activityContext),
            "android.permission.BLUETOOTH_CONNECT"
        )

        val permission3 = ActivityCompat.checkSelfPermission(
            (activityContext),
            "android.permission.ACCESS_COARSE_LOCATION"
        )
        val permission4 = ActivityCompat.checkSelfPermission(
            (activityContext),
            "android.permission.ACCESS_FINE_LOCATION"
        )

        if (permission1 != PackageManager.PERMISSION_GRANTED
            && permission2 != PackageManager.PERMISSION_GRANTED
            && permission3 != PackageManager.PERMISSION_GRANTED
            && permission4 != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activityContext, PermissionsLocation, 1)
        }
    }

}