package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    private TextView chatHistory;
    private EditText messageInput;
    private Button sendButton, switchToUser2, clearChatButton;
    private final String FILE_NAME = "conversatie.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatHistory = findViewById(R.id.chatHistory);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        switchToUser2 = findViewById(R.id.switchToUser2);
        clearChatButton = findViewById(R.id.clearChatButton);

        loadChatHistory();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        switchToUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        clearChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearChatHistory();
            }
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatHistory.append("\nPersoana 1: " + message);
            saveMessageToFile("Persoana 1: " + message + "\n");
            messageInput.setText("");
        }
    }

    private void saveMessageToFile(String message) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND)) {
            fos.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder history = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                history.append(line).append("\n");
            }
            chatHistory.setText(history.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearChatHistory() {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
            fos.write("".getBytes());
            chatHistory.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
