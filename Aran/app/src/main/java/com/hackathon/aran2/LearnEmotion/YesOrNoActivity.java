package com.hackathon.aran2.LearnEmotion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.aran2.InfoBean;
import com.hackathon.aran2.R;
import com.google.firebase.database.DatabaseReference;

public class YesOrNoActivity extends AppCompatActivity {
    Button backBtn;
    private final int END_GAME =1;
    Button yesBtn, noBtn;
    private DatabaseReference mDatabase;
    InfoBean infoBean;
    int age;
    String gender;

    private Typeface mTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_or_no_select);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        ImageView iv_photo = findViewById(R.id.yesOrNo_image);
        int width = (int)(getApplicationContext().getResources().getDisplayMetrics().widthPixels*0.8);
        iv_photo.getLayoutParams().width = width;
        iv_photo.getLayoutParams().height = width;
        yesBtn = findViewById(R.id.yes);
        age = getIntent().getIntExtra("age", 0);
        gender = getIntent().getStringExtra("gender");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent yesIntent = new Intent(getApplicationContext(), Emotion_ListActivity.class);
                yesIntent.putExtra("선택", -2);
                yesIntent.putExtra("gender", gender);
                yesIntent.putExtra("age", age);
                startActivityForResult(yesIntent,5000);
            }
        });
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("gender"), Toast.LENGTH_LONG).show();
        noBtn = findViewById(R.id.no);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(age<6) {
                    Intent noIntent = new Intent(getApplicationContext(), YoungEmotionActivity.class);
                    noIntent.putExtra("emotion", "random");
                    noIntent.putExtra("gender", gender);
                    noIntent.putExtra("age", age);
                    startActivityForResult(noIntent,5000);
                } else {
                    //6 세 이상이면
                    Intent noIntent = new Intent(getApplicationContext(), OldEmotionActivity.class);
                    noIntent.putExtra("emotion", "random");
                    noIntent.putExtra("gender",gender);
                    noIntent.putExtra("age", age);
                    startActivityForResult(noIntent,5000);
                }
            }

        });
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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