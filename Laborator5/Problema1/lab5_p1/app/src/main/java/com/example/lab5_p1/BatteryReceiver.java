package com.example.lab5_p1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (level != -1) {
            Toast.makeText(context, "Battery Level: " + level + "%", Toast.LENGTH_SHORT).show();
        }

        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING && level <= 15) {
            Toast.makeText(context, "Warning: Battery Low!", Toast.LENGTH_LONG).show();
        }
    }
}
