package com.example.safealert2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class EmergencyCallHelper {
    private static final int CALL_PERMISSION_REQUEST = 200;
    private final Context context;
    private String emergencyNumber;
    private boolean isCancelled = false;
    public EmergencyCallHelper(Context context, String emergencyNumber) {
        this.context = context;
        this.emergencyNumber = emergencyNumber;
    }
    public void scheduleEmergencyCall(Activity activity, int delayMillis) {

        isCancelled = false;
        new Handler().postDelayed(() -> {
            if (!isCancelled) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    initiateCall();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]
                            {Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST);
                }
            }
        }, delayMillis);
    }
    public void cancelCall() {

        isCancelled = true;
    }
    private void initiateCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(context, "Call permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}
