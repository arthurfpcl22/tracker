package com.example.test1

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class BleState(activityContext: Context):BroadcastReceiver() {
    var activityContext: Context
    init {
        this.activityContext = activityContext
    }
    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> Toast.makeText(activityContext,
                    "bluetooth is off", Toast.LENGTH_SHORT).show()
                BluetoothAdapter.STATE_TURNING_OFF -> Toast.makeText(activityContext,
                    "Bluetooth is turning off...", Toast.LENGTH_SHORT).show()
                BluetoothAdapter.STATE_ON -> Toast.makeText(activityContext,
                    "Bluetooth is on", Toast.LENGTH_SHORT).show()
                BluetoothAdapter.STATE_TURNING_ON -> Toast.makeText(activityContext,
                    "Bluetooth is turning on...", Toast.LENGTH_SHORT).show()

            }
        }
    }
}
