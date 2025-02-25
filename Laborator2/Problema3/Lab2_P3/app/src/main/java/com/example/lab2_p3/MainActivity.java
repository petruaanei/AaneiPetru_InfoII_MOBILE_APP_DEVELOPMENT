package com.example.lab2_p3;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private Button btnControlLumina;
    private Button btnSchimbaCuloarea;
    private Switch switchAutoMode;
    private boolean luminaAprinsa = false;
    private boolean modAutomat = false;
    private int culoareCnt = 0;

    private final int[] colors = {
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#00BFFF"),
            Color.parseColor("#DC143C"),
            Color.parseColor("#32CD32")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        btnControlLumina = findViewById(R.id.btnControl_Lumina);
        btnSchimbaCuloarea = findViewById(R.id.btnSchimbaCuloarea);
        switchAutoMode = findViewById(R.id.switchAutoMode);

        btnControlLumina.setOnClickListener(v -> {
            if (!modAutomat) {
                controlLumina();
            }
        });

        btnSchimbaCuloarea.setOnClickListener(v -> {
            if (!modAutomat) {
                schimbaCuloarea();
            }
        });

        switchAutoMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modAutomat = isChecked;
            if (modAutomat) {
                updateLumina();
            }
        });
    }

    private void controlLumina() {
        luminaAprinsa = !luminaAprinsa;
        if (luminaAprinsa) {
            layout.setBackgroundColor(Color.parseColor("#F0E68C"));
            btnControlLumina.setText("Oprește Lumina");
            Toast.makeText(this, "Buna dimineata!", Toast.LENGTH_SHORT).show();
        } else {
            layout.setBackgroundColor(Color.parseColor("#696969"));
            btnControlLumina.setText("Aprinde Lumina");
            Toast.makeText(this, "Noapte buna!", Toast.LENGTH_SHORT).show();
        }
    }

    private void schimbaCuloarea() {
        layout.setBackgroundColor(colors[culoareCnt]);
        switch (culoareCnt) {
            case 0:
                Toast.makeText(this, "Alba:Optimism", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "Albastru: Relaxare", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Roșu: Alerta", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "Verde: Energie", Toast.LENGTH_SHORT).show();
                break;
        }
        culoareCnt = (culoareCnt + 1) % colors.length;
    }
    private void updateLumina() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 7 && hour < 18) {
            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Toast.makeText(this, "Buna dimineata!", Toast.LENGTH_SHORT).show();
        } else if (hour >= 18 && hour < 22) {
            layout.setBackgroundColor(Color.parseColor("#FFA500"));
        } else {
            layout.setBackgroundColor(Color.parseColor("#696969"));
            Toast.makeText(this, "Noapte buna!", Toast.LENGTH_SHORT).show();
        }
    }
}
