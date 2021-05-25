package com.hackathon.aran2.LearnEmotion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hackathon.aran2.R;
import java.util.ArrayList;

public class EmotionCollectionActivity extends AppCompatActivity {
    ArrayList<Integer> emotionDrawable= new ArrayList<>();
    ArrayList<String> emotionTitle = new ArrayList<>();
    ArrayList<String> emotionText = new ArrayList<>();
    Button back;
    private Typeface mTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_list);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView EmotionRecyclerView = findViewById(R.id.emotionList_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        EmotionRecyclerView.setLayoutManager(layoutManager);

        emotionDrawable.add(R.drawable.happy);
        emotionTitle.add("즐거움");
        emotionText.add(" 즐거운 느낌이나 마음");

        emotionDrawable.add(R.drawable.happy);
        emotionTitle.add("행복함");
        emotionText.add(" 생활에서 충분한 만족과 기쁨을 느끼어 흐뭇하다.");

        emotionDrawable.add(R.drawable.happy);
        emotionTitle.add("책임감");
        emotionText.add(" 맡아서 해야 할 임무나 의무를 중히 여기는 마음");
        emotionDrawable.add(R.drawable.happy);
        emotionTitle.add("욕망");
        emotionText.add("부족을 느껴 무엇을 가지거나 누리고자 함");

        emotionDrawable.add(R.drawable.happy);
        emotionTitle.add("부러움 ");
        emotionText.add("남의 좋은 일이나 물건을 보고 자기도 그런 일을 이루거나 그런 물건을 가졌으면 하고 바라는 마음이 있다.");

        emotionDrawable.add(R.drawable.satisfy);
        emotionTitle.add("기대감");
        emotionText.add(" 어떤 일이 원하는 대로 이루어지기를 바라면서 기다리다");

        emotionDrawable.add(R.drawable.love);
        emotionTitle.add("고마움");
        emotionText.add(" 남이 베풀어 준 호의나 도움 따위에 대하여 마음이 흐뭇하고 즐겁다.");

        emotionDrawable.add(R.drawable.love);
        emotionTitle.add("감동");
        emotionText.add(" 깊이 느껴 마음이 움직임");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("우울함");
        emotionText.add(" 근심스럽거나 답답하여 활기가 없다.");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("속상함");
        emotionText.add(" 화가 나거나 걱정이 되는 따위로 인하여 마음이 불편하다.");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("미안함");
        emotionText.add("남에게 대하여 마음이 편치 못하고 부끄럽다.");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("억울함");
        emotionText.add("아무 잘못없이 꾸중을 듣거나 벌을 받거나 하여 분하고 답답하다.");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("자괴감");
        emotionText.add("스스로 부끄러워하는 마음");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("수치심");
        emotionText.add("부끄러움을 느끼는 마음");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("죄책감");
        emotionText.add("저지른 잘못에 대하여 책임을 느끼는 마음");

        emotionDrawable.add(R.drawable.sad);
        emotionTitle.add("후회");
        emotionText.add("이전의 잘못을 깨닫고 뉘우침");

        emotionDrawable.add(R.drawable.scary);
        emotionTitle.add("불안");
        emotionText.add("마음이 편하지 않고 조마조마함.");

        emotionDrawable.add(R.drawable.scary);
        emotionTitle.add("절망");
        emotionText.add("바라볼 것이 없게 되어 모든 희망을 끊어 버림");

        emotionDrawable.add(R.drawable.love);
        emotionTitle.add("그리움");
        emotionText.add("사랑하여 몹시 보고 싶어 하다.");

        emotionDrawable.add(R.drawable.upset);
        emotionTitle.add("샘(질투)");
        emotionText.add(" 남의 상황이나 물건을 탐내거나, 자신보다 나은 처지에 있는 사람을 미워함");
        EmotionCollectionAdapter emotionListAdapter = new EmotionCollectionAdapter(emotionDrawable, emotionTitle, emotionText);
        EmotionRecyclerView.setAdapter(emotionListAdapter);
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
