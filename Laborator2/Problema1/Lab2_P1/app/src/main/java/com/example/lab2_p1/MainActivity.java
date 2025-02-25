package com.example.lab2_p1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView textViewMessage;
    private Button buttonClickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMessage = findViewById(R.id.textViewMessage);
        buttonClickMe = findViewById(R.id.buttonClickMe);

        buttonClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMessage.setText("Mesaj schimbat");
            }
        });
    }
}