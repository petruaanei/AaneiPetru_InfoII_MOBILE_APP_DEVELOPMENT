package com.example.lab3_p2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ChapterFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_IMAGE = "image";

    public static ChapterFragment newInstance(String title, String content, int imageRes) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putInt(ARG_IMAGE, imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);

        TextView txtTitle = view.findViewById(R.id.txtChapterTitle);
        TextView txtContent = view.findViewById(R.id.txtChapterContent);
        ImageView imgChapter = view.findViewById(R.id.imgChapter);

        if (getArguments() != null) {
            txtTitle.setText(getArguments().getString(ARG_TITLE, "Capitol"));
            txtContent.setText(getArguments().getString(ARG_CONTENT, "Conținut indisponibil."));
            imgChapter.setImageResource(getArguments().getInt(ARG_IMAGE, R.drawable.formula1));
        }

        return view;
    }
}
