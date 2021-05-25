package com.hackathon.aran2.LearnEmotion;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hackathon.aran2.R;

import java.util.ArrayList;

public class EmotionCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class EmotionHolder extends RecyclerView.ViewHolder {
        ImageView emotion_Image;
        TextView emotion_explain, emotion_header;
        EmotionHolder(View view) {
            super(view);
            emotion_Image = view.findViewById(R.id.emotions_image);
            emotion_explain = view.findViewById(R.id.emotions_explain);
            emotion_header = view.findViewById(R.id.emotions_header);
        }
    }

    ArrayList<String> emotionHeaders, emotionExplains;
    ArrayList<Integer> drawableResources;
    public EmotionCollectionAdapter(ArrayList<Integer> drawableResources, ArrayList<String> emotionHeaders, ArrayList<String> emotionExplains){
        this.drawableResources = drawableResources;
        this.emotionExplains = emotionExplains;
        this.emotionHeaders = emotionHeaders;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.emotions, parent, false);
        Context context = parent.getContext();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(parent, typeface);
        return new EmotionHolder(v);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EmotionHolder emotionHolder = (EmotionHolder)holder;
        emotionHolder.emotion_header.setText(this.emotionHeaders.get(position));
        emotionHolder.emotion_explain.setText(this.emotionExplains.get(position));
        emotionHolder.emotion_Image.setImageResource(this.drawableResources.get(position));
    }

    @Override
    public int getItemCount() {
        return emotionExplains.size();
    }

}

