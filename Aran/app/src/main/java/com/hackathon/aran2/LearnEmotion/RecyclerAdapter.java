package com.hackathon.aran2.LearnEmotion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.aran2.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<Emotion_ListActivity.Content> contents;
    private Context context;
    String gender;
    int age;
    public RecyclerAdapter(ArrayList<Emotion_ListActivity.Content> contents, String gender, int age){
        this.contents = contents;
        this.gender = gender;
        this.age = age;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emotion_list, parent, false);
        context = parent.getContext();

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(parent, typeface);
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.

        return new ItemViewHolder(view);
    }
    void setGlobalFont(ViewGroup root, Typeface typeface) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child,typeface);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.imageView.setImageResource(contents.get(position).id);
        itemViewHolder.textView1.setText(contents.get(position).emotion);
        final int i =position;
        itemViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String age = intent.getExtras().getString("age");
//                String[] year = age.split("-");
//                int a = Integer.valueOf(year[0]);
//                Date date = new Date();
//                SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
//                String signUpDate = day.format(date);
//                String[] d = signUpDate.split("-");
//                int b = Integer.valueOf((d[0]));
//                int age2 = b - a + 1;
                if(age<6) {
                    Intent noIntent = new Intent(context, YoungEmotionActivity.class);
                    noIntent.putExtra("emotion", contents.get(i).emotion);
                    noIntent.putExtra("gender", gender);
                    noIntent.putExtra("age",age);
                    ((Activity)context).startActivityForResult(noIntent,5000);
                } else {
                    //6 세 이상이면
                    Intent noIntent = new Intent(context, OldEmotionActivity.class);
                    noIntent.putExtra("emotion", contents.get(i).emotion);
                    noIntent.putExtra("gender", gender);
                    noIntent.putExtra("age",age);
                    ((Activity)context).startActivityForResult(noIntent,5000);
                }
//                Intent intent = new Intent(context, YoungEmotionActivity.class);
//                intent.putExtra("emotion", contents.get(i).emotion);
//                context.startActivity(intent);
//                Intent intent = new Intent(context, OldEmotionActivity.class);
//                intent.putExtra("emotion", contents.get(i).emotion);
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return contents.size();
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private ImageView imageView;
        private LinearLayout linearLayout;
        ItemViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear);
            textView1 = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.imageView);
        }


    }
}