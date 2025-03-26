package com.example.safealert2;

import android.content.Context;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

public class ScheduledMessageSender {
    private final Context context;
    private final Handler handler;
    private Runnable messageRunnable;

    public ScheduledMessageSender(Context context) {
        this.context = context;
        this.handler = new Handler();
    }
    public void scheduleMessage(String phoneNumber, String message, long delayMillis) {
        cancelScheduledMessage();

        messageRunnable = () -> {
            sendMessage(phoneNumber, message);
            messageRunnable = null;
        };
        handler.postDelayed(messageRunnable, delayMillis);

        Toast.makeText(context, "Scheduled message. Will be sent automatically unless canceled.",
                Toast.LENGTH_SHORT).show();
    }

    public void cancelScheduledMessage() {
        if (messageRunnable != null) {
            handler.removeCallbacks(messageRunnable);
            messageRunnable = null;
            Toast.makeText(context, "Scheduled message canceled!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(context, "Sent message: " + message,
                Toast.LENGTH_SHORT).show();
    }
}
