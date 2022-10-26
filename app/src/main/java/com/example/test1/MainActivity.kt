package com.example.test1

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.test1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mBTStateUpdateReceiver: BleState
    private lateinit var mScanDevices: ScanDevices
    private lateinit var mConnectBle: ConnectBle
    private var address:String = "7C:9E:BD:F6:3C:26"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        initState()
        initScanDevices(binding)
        checkPermission()
        checkButtuns()



    }



    private fun initState() {
        mBTStateUpdateReceiver = BleState(this);
    }

    private fun initScanDevices(binding: ActivityMainBinding) {
        mScanDevices = ScanDevices(this,binding)

    }

    private fun checkButtuns() {
        binding.scan.setOnClickListener { view ->
            mScanDevices.scanLeDevice()
        }
        binding.devicesListView.setOnItemClickListener {adapterView,view,i,l->
            mConnectBle = ConnectBle(this,address,binding)
            mConnectBle.connect()

        }
    }

    private fun isSupported(context: Context):Boolean{
        return BluetoothAdapter.getDefaultAdapter() != null
                && context.packageManager.hasSystemFeature(FEATURE_BLUETOOTH_LE)

    }
    override fun onStart() {
        super.onStart()
        registerReceiver(
            mBTStateUpdateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    // Required for smartphone
    private val PermissionsLocation = arrayOf(
        "android.permission.BLUETOOTH_SCAN",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.BLUETOOTH_PRIVILEGED"

    )

    private fun checkPermission() {
        val permission = ActivityCompat.checkSelfPermission(
            (this as Context),
            "android.permission.BLUETOOTH_SCAN"
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((this as Activity), PermissionsLocation, 1)
        }
    }
}