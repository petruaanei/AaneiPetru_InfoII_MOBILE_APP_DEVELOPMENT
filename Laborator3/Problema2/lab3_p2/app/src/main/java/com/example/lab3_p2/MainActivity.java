package com.example.lab3_p2;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer,new IntroFragment())
                    .commit();
        }

        int[] buttonIds = {R.id.btnChapter1, R.id.btnChapter2, R.id.btnChapter3, R.id.btnChapter4};

        for (int i = 0; i < buttonIds.length; i++) {
            int index = i;
            findViewById(buttonIds[i]).setOnClickListener(view ->
                    openChapter(ChapterContent.titles[index],
                            ChapterContent.contents[index],
                            ChapterContent.imageRes[index]));
        }

    }
    private void openChapter(String title, String content, int imageRes) {
        Fragment chapterFragment = ChapterFragment.newInstance(title, content, imageRes);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, chapterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
