package com.example.lab2_p2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button buttonToast = findViewById(R.id.buttonToast);
        Button buttonCount = findViewById(R.id.buttonCount);
        TextView textViewCounter = findViewById(R.id.textViewCounter);

        buttonToast.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Mesaj Toast", Toast.LENGTH_SHORT).show()
        );

        buttonCount.setOnClickListener(v ->
        { count++;
            textViewCounter.setText(String.valueOf(count));
        });
        
    }
}