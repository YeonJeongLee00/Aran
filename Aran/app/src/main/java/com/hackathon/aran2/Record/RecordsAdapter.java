package com.hackathon.aran2.Record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public static class RecordHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_recordHolder;
        TextView tv_date, tv_emotion;
        ImageView iv_drawing;
        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            tv_date= itemView.findViewById(R.id.record_date);
            tv_emotion = itemView.findViewById(R.id.record_emotion);
            iv_drawing = itemView.findViewById(R.id.record_image);
            cl_recordHolder = itemView.findViewById(R.id.record_holder);
        }
    }
    View v;
    private ArrayList<RecordContent> recordContents;
    private Context context;
    RecordsAdapter( ArrayList<RecordContent> recordContents) {
        this.recordContents = recordContents;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context =  parent.getContext();
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record, parent, false);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(parent, typeface);
        return new RecordHolder(v);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final  RecordHolder recordHolder = (RecordHolder)holder;
        int itemViewHeight =  (int)(context.getResources().getDisplayMetrics().widthPixels/3.2);

        recordHolder.iv_drawing.getLayoutParams().width =(int)(itemViewHeight*0.8);
        recordHolder.iv_drawing.getLayoutParams().height=(int)(itemViewHeight*0.8);
        recordHolder.tv_emotion.setText(recordContents.get(position).emotion);
        String[] split = recordContents.get(position).date.split("_");
        recordHolder.tv_date.setText(split[0]);
        final int i = position;
        String uid = "id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid+"/learnEmotion/"+recordContents.get(position).date+"image");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(recordHolder.iv_drawing);

            }
        });

        recordHolder.cl_recordHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowRecordActivity.class);
                intent.putExtra("isRecorded", recordContents.get(position).isRecorded);
                System.out.println("녹음 : " +  recordContents.get(position).isRecorded);
                intent.putExtra("date", recordContents.get(position).date);
                intent.putExtra("emotion",recordContents.get(position).emotion);
                intent.putExtra("question",recordContents.get(position).question);
                intent.putExtra("answer", recordContents.get(position).stringRecord);
                context.startActivity(intent);
            }
        });
        //화남 고마움 행복 부끄러움 슬픔
        //놀람 부러움 서운함 무서움 미안 사랑 뿌듯함
        //부러움
        switch (recordContents.get(position).emotion){
            case "행복"://ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_red);
                break;
            case "슬픔"://ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_pink);
                break;
            case "사랑"://ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_orange);
                break;
            case "무서움": // 무서움ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_yellow_two);
                break;
            case "미안": // 미안함
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_deepyellow);
                break;
            case "부끄러움": // 부끄러움ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_yellow);
                break;
            case "화남": // 화남ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_lightgreen);
                break;
            case "뿌듯함": // ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_green);
                break;
            case "놀람": // 놀람ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_deepgreen);
                break;
            case "서운함": // 서운함ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_skyblue);
                break;
            case "부러움": // 외로움ㅇ
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_blue);
                break;
            case "고마움":
                recordHolder.cl_recordHolder.setBackgroundResource(R.drawable.border_purple);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return recordContents.size();
    }
}
