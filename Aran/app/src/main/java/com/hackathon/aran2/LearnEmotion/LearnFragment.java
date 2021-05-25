package com.hackathon.aran2.LearnEmotion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hackathon.aran2.InfoBean;
import com.hackathon.aran2.R;
import com.hackathon.aran2.Solution.SolutionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LearnFragment extends Fragment {
    View v;
    Button b,b2;
    String uid;

    private Typeface typeface;
    String gender;
    int age;
    boolean isLoad;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.learn_emotion_frag, container, false);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(),
                    "BinggraeMelona.ttf");
        }
        setGlobalFont((ViewGroup) v);
        b = v.findViewById(R.id.learnEmotion);
        b2 = v.findViewById(R.id.learnEmotion_emotionList);
        isLoad = false;
        getString();
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //감정종류
                Intent intent = new Intent(getContext(), EmotionCollectionActivity.class);
                startActivity(intent);

            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //감정학습
                if(isLoad){
                     Intent intent = new Intent(getContext(),YesOrNoActivity.class);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age",age);
                    startActivity(intent);
                }
            }
        });
        Button b3= v.findViewById(R.id.learnEmotion_solution);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SolutionActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
    public void getString() {
        uid = "id";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid + "/userInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InfoBean infoBean = dataSnapshot.getValue(InfoBean.class);

                gender = infoBean.getGender();
                String[] year = infoBean.getBirth().split("-");
                int a = Integer.valueOf(year[0]);
                Date date = new Date();
                SimpleDateFormat day2 = new SimpleDateFormat("yyyy-MM-dd");
                String signUpDate = day2.format(date);
                String[] d = signUpDate.split("-");
                int b = Integer.valueOf((d[0]));
                int age2 = b - a + 1;
                age = age2;
                isLoad = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}