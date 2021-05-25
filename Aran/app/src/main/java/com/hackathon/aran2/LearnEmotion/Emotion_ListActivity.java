package com.hackathon.aran2.LearnEmotion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.aran2.R;


public class Emotion_ListActivity extends AppCompatActivity {
    private RecyclerAdapter adapter;
    private LinearLayout li;
    Button back;

    private Typeface mTypeface;
    class Content{
        int id;
        String emotion;
        public Content(int id, String emotion){
            this.id = id;
            this.emotion = emotion;
        }
    }
    ArrayList<Content> contents;
    int age;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_list_);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gender = getIntent().getStringExtra("gender");
        age = getIntent().getIntExtra("age",0);
        contents = new ArrayList<>();
        contents.add(new Content(R.drawable.happy, "고마움"));
        contents.add(new Content(R.drawable.upset, "화남"));
        contents.add(new Content(R.drawable.happy, "부끄러움"));
        contents.add(new Content(R.drawable.sad, "슬픔"));
        contents.add(new Content(R.drawable.scary, "무서움"));
        contents.add(new Content(R.drawable.surprise, "뿌듯함"));
        contents.add(new Content(R.drawable.envy, "부러움"));
        contents.add(new Content(R.drawable.surprise, "놀람"));
        contents.add(new Content(R.drawable.hurt, "서운함"));
        contents.add(new Content(R.drawable.happy, "행복"));
        contents.add(new Content(R.drawable.sorry, "미안"));
        contents.add(new Content(R.drawable.love, "사랑"));
        init();

    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter(contents, gender, age);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case  5000:
                    Intent outintent = new Intent();
                    setResult(RESULT_OK, outintent);
                    finish();
                    break;
            }
        }
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
}