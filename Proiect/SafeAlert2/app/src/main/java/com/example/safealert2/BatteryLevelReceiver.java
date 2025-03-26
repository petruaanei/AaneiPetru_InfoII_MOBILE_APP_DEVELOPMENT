package com.example.safealert2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;

public class BatteryLevelReceiver extends BroadcastReceiver {
    private static final String TAG = "BatteryLevelReceiver";
    private static final int Low_Level = 10;
    private static final int Critical_Level = 3;
    private boolean smsSentLow = false;
    private boolean smsSentCritical = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) ((level / (float) scale) * 100);

        if (batteryPct <= Low_Level && !smsSentLow) {
            smsSentLow = true;
            String toastMsg = "Low Battery (" + batteryPct + "%)! Please enable Power Saving Mode.";
            showToast(context, toastMsg);
            sendLocationSMS(context, batteryPct, false);
        } else if (batteryPct == Critical_Level && !smsSentCritical) {
            smsSentCritical = true;
            String toastMsg = "Critical battery (" + batteryPct + "%)! Sending final location...";
            showToast(context, toastMsg);
            sendLocationSMS(context, batteryPct, true);
        } else if (batteryPct > Low_Level && smsSentLow) {
            smsSentLow = false;
        }
    }
    private void sendLocationSMS(Context context, int batteryPct, boolean isCritical) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                String message;
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    if (isCritical) {
                        message = "Critical: Battery " + batteryPct + "%! " +
                                "Final location: https://maps.google.com/?q=" + lat + "," + lon;
                    } else {
                        message = "Alert: Battery " + batteryPct + "%! " +
                                "My location: https://maps.google.com/?q=" + lat + "," + lon;
                    }
                } else {
                    if (isCritical) {
                        message = "Critical: Battery " + batteryPct +
                                "%! Could not get final location!";
                    } else {
                        message = "Alert: Battery " + batteryPct +
                                "%! Location not found!";
                    }
                }
                sendSMS(context, message);
            }).addOnFailureListener(e -> {
                showToast(context, "Error getting location!");
                String message;
                if (isCritical) {
                    message = "Critical Alert: Battery " + batteryPct +
                            "%! Could not get final location!";
                } else {
                    message = "Alert: Battery " + batteryPct +
                            "%! Location not found!";
                }
                sendSMS(context, message);
            });
        } else {
            showToast(context, "Permission to send SMS is not granted!");
        }
    }
    private void sendSMS(Context context, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            ArrayList<String> favoriteContacts = getFavoriteContacts(context);
            if (favoriteContacts.isEmpty()) {
                showToast(context, "No favorite contacts found in phone book!");
                return;
            }
            for (String contact : favoriteContacts) {
                smsManager.sendMultipartTextMessage(contact, null, parts, null, null);
            }
            showToast(context, "SMS sent to favorite contacts!");
        } catch (SecurityException e) {
            showToast(context, "SMS permission denied!");
            e.printStackTrace();
        }
    }
    private ArrayList<String> getFavoriteContacts(Context context) {
        ArrayList<String> favorites = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return favorites;
        }
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.STARRED + "=?",
                new String[]{"1"},
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String hasPhone =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone != null && hasPhone.equals("1")) {
                    Cursor phoneCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            @SuppressLint("Range") String phoneNumber =
                                    phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            favorites.add(phoneNumber);
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }
        return favorites;
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
