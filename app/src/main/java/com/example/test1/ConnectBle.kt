package com.example.test1

import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.test1.databinding.ActivityMainBinding
import java.util.*


class ConnectBle(activityContext: AppCompatActivity, deviceaddress:String,binding: ActivityMainBinding) {
    private var mBluetoothAdapter: BluetoothAdapter
    val carac: UUID = UUID.fromString("ca73b3ba-39f6-4ab3-91ae-186dc9577d99")
    var CLIENT_CHARACTERISTIC_CONFIG:UUID =UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb")
    private var activityContext: AppCompatActivity
    private var device:BluetoothDevice
    private var deviceaddress:String
    private var mBluetoothGatt: BluetoothGatt? = null
    private var descriptor: BluetoothGattDescriptor? = null
    private var binding:ActivityMainBinding
    init {
        this.binding = binding
        this.activityContext = activityContext
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.deviceaddress = deviceaddress
        this.device = mBluetoothAdapter.getRemoteDevice(deviceaddress);

    }
    fun connect():Boolean
    {
        try{
            checkPermission()
            //connect to the given deviceaddress
            mBluetoothGatt = device.connectGatt(activityContext, false, mGattCallback);
            return true
        }catch (exception: IllegalArgumentException) {
            Log.w(TAG, "Device not found with provided address.  Unable to connect.")
            return false
    }

    }
    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.w(TAG, "connected sucessfully!")
                binding.connectStatus.text = "Connected to $deviceaddress"
                checkPermission();
                gatt.discoverServices();

            } else {
                Log.w(TAG, "Disconnected!")
                binding.connectStatus.text = "Disconnected"
                binding.data.text = "No data"
                binding.rssi.text = ""
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.e("BluetoothLeService", "onServiceDiscovered()")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "**ACTION_SERVICE_DISCOVERED** $status")
                var gattServices: List<BluetoothGattService> = mBluetoothGatt!!.services
                Log.e("onServiceDiscovered", "Services count: ${gattServices.size}")
                for (gattService in gattServices) {
                    var serviceUUID = gattService.uuid.toString()
                    Log.e("OnServicesDiscovered", "Service uuid: $serviceUUID")
                    readCharacteristic(gattService)

                }
                Log.e("OnServicesDiscovered", "---------------------------")

                //broadcastUpdate( "com.np.lekotlin.ACTION_GATT_SERVICES_DISCOVERED")

            } else {
                //Service discovery failed so log a warning
                Log.i(TAG, "onServicesDiscovered received: $status")
            }
        }

        fun readCharacteristic(service: BluetoothGattService) {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized")
                return
            }
            var gattCharacteristic: List<BluetoothGattCharacteristic> = service.characteristics
            Log.i(TAG, "**LEO LAS ${gattCharacteristic.count()} CARACTERISTICAS DE $service**")


            for (characteristic in gattCharacteristic) {
                for (descriptor in characteristic.descriptors) {
                    Log.e(TAG, "BluetoothGattDescriptor: " + descriptor.uuid.toString())
                }
                Log.e("OnServicesDiscovered", "Service characteristic: ${characteristic.uuid}")
                checkPermission()
                if (characteristic.uuid == carac) {
                    mBluetoothGatt!!.setCharacteristicNotification(characteristic, true)
                    descriptor =
                        characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                    descriptor!!.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    mBluetoothGatt!!.writeDescriptor(descriptor)
                    mBluetoothGatt!!.readCharacteristic(characteristic)
                    Log.e("Carac", "-----------------------------")

                }

            }
            Log.e("OnServicesDiscovered", "-----------------------------")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.d(TAG, "rssi is : $rssi")
            binding.rssi.text = rssi.toString()

        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //we are still connected to the service
            Log.i(TAG, "Read")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //See if the read was successful
                Log.i(TAG, "**ACTION_DATA_READ** ${characteristic.uuid}")
                //broadcastUpdate("com.np.lekotlin.ACTION_DATA_AVAILABLE"/*, characteristic*/)                 //Go broadcast an intent with the characteristic data
                Log.e("onCharacteristicRead", "Datos:")
                Log.e("onCharacteristicRead", "$characteristic.value")

            } else {
                Log.i(TAG, "ACTION_DATA_READ: Error$status")
            }

        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            Log.e("Entered here", "onCharacteristicChanged")
            Log.e("onCharacteristicRead", String(characteristic.value))
            binding.data.text = String(characteristic.value)
            checkPermission()
            gatt.readRemoteRssi()


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
