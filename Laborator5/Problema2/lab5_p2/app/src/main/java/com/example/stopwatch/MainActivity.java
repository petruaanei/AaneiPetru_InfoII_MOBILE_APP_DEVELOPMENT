package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView timerText;
    private Button startButton, pauseButton, resetButton;
    private Handler handler = new Handler();
    private long startTime = 0L, timeInMillis = 0L;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - timeInMillis;
            handler.postDelayed(updateTimeRunnable, 0);
            isRunning = true;
        }
    }
    private void pauseTimer() {
        if (isRunning) {
            handler.removeCallbacks(updateTimeRunnable);
            timeInMillis = System.currentTimeMillis() - startTime;
            isRunning = false;
        }
    }

    private void resetTimer() {
        handler.removeCallbacks(updateTimeRunnable);
        timeInMillis = 0L;
        timerText.setText("00:00:00");
        isRunning = false;
    }

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            timeInMillis = System.currentTimeMillis() - startTime;
            int seconds = (int) (timeInMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (timeInMillis % 1000 / 10);
            timerText.setText(String.format("%02d:%02d:%02d", minutes, seconds, milliseconds));
            handler.postDelayed(this, 10);
        }
    };
}