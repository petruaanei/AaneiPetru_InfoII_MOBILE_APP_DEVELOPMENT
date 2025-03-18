package com.example.lab5_p1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReciever extends BroadcastReceiver {
    private int lastBatteryLevel = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            if (lastBatteryLevel != -1 && lastBatteryLevel != batteryPct) {
                Toast.makeText(context, "Battery Level: " + batteryPct + "%", Toast.LENGTH_SHORT).show();
            }
            lastBatteryLevel = batteryPct;
        }
        else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            Toast.makeText(context, "Low Battery!", Toast.LENGTH_LONG).show();
        }
    }
}
