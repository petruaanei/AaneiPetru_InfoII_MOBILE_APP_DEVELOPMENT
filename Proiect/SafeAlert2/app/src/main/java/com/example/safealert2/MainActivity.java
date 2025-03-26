package com.example.safealert2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int CALL_PERMISSION_REQUEST = 200;
    private static final int READ_CONTACTS_PERMISSION_REQUEST = 300;
    private static final long MAX_INTERVAL = 2000;
    private static final int SOS_DELAY = 10000;
    private Button sosButton, emergencyButton,cancelEmergencyButton;
    private Button scheduleMessageButton, cancelMessageButton;
    private EditText phoneNumberInput, messageInput, delayInput;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private BroadcastReceiver batteryLevelReceiver;
    private String emergencyNumber = "0755919836";
    private long lastPressTime = 0;
    private int pressCount = 0;
    private boolean isCallCancelled = false;
    private Handler handler;

    private ScheduledMessageSender scheduledMessageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cancelEmergencyButton = findViewById(R.id.cancelEmergencyButton);
        cancelEmergencyButton.setVisibility(View.GONE);
        sosButton = findViewById(R.id.sosButton);
        emergencyButton = findViewById(R.id.emergencyButton);
        scheduleMessageButton = findViewById(R.id.scheduleMessageButton);
        cancelMessageButton = findViewById(R.id.cancelMessageButton);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        messageInput = findViewById(R.id.messageInput);
        delayInput = findViewById(R.id.delayInput);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler();
        scheduledMessageSender = new ScheduledMessageSender(this);

        checkAndRequestPermissions();

        sosButton.setOnClickListener(view -> triggerSOS());
        emergencyButton.setOnClickListener(view -> initiateEmergencyCall());
        cancelEmergencyButton.setOnClickListener(view -> {
            isCallCancelled = true;
            Toast.makeText(MainActivity.this, "Emergency call canceled!",
                    Toast.LENGTH_SHORT).show();
            cancelEmergencyButton.setVisibility(View.GONE);
        });

        scheduleMessageButton.setOnClickListener(view -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();
            String delayStr = delayInput.getText().toString().trim();
            if (!phoneNumber.isEmpty() && !message.isEmpty() && !delayStr.isEmpty()) {
                try {
                    long delayMillis = Long.parseLong(delayStr) * 1000;
                    scheduledMessageSender.scheduleMessage(phoneNumber, message, delayMillis);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please enter a valid number for the time!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Fill in the number, message and waiting time!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cancelMessageButton.setOnClickListener(view -> scheduledMessageSender.cancelScheduledMessage());
        batteryLevelReceiver = new BatteryLevelReceiver();
        IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS
        };
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE ||
                requestCode == CALL_PERMISSION_REQUEST ||
                requestCode == READ_CONTACTS_PERMISSION_REQUEST) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Not all necessary permissions have been granted!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
    private void triggerSOS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Required permissions are missing!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    sendSOSMessage(0, 0);
                    return;
                }
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                sendSOSMessage(latitude, longitude);
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void sendSOSMessage(double lat, double lon) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = (lat == 0 && lon == 0)
                ? "SOS! My location could not be obtained at the moment."
                : "SOS! My location: https://maps.google.com/?q=" + lat + "," + lon;

        ArrayList<String> favoriteContacts = getFavoriteContacts();
        if (favoriteContacts.isEmpty()) {
            Toast.makeText(this, "No favorite contacts found!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        for (String contact : favoriteContacts) {
            smsManager.sendTextMessage(contact, null, message, null, null);
        }
        Toast.makeText(this, "SOS message sent to favorite contacts!",
                Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> getFavoriteContacts() {
        ArrayList<String> favorites = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSION_REQUEST);
            return favorites;
        }
        Cursor cursor = getContentResolver().query(
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
                    Cursor phoneCursor = getContentResolver().query(
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
    private void initiateEmergencyCall() {
        isCallCancelled = false;
        cancelEmergencyButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Emergency call in 10 seconds... Press cancel to stop!",
                Toast.LENGTH_LONG).show();
        handler.postDelayed(() -> {
            if (!isCallCancelled) {
                makeEmergencyCall();
            }
            cancelEmergencyButton.setVisibility(View.GONE);
        }, SOS_DELAY);
    }
    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PERMISSION_REQUEST);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));
        startActivity(callIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPressTime < MAX_INTERVAL) {
                pressCount++;
                if (pressCount == 3) {
                    triggerSOS();
                    pressCount = 0;
                }
            } else {
                pressCount = 1;
            }
            lastPressTime = currentTime;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
