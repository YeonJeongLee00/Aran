package com.hackathon.aran2.Solution;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LovesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public class LovesHolder extends RecyclerView.ViewHolder {
        ImageView iv_solution;
        TextView tv_title, tv_num;
        CheckBox cb_heart;

        LovesHolder(@NonNull View itemView) {
            super(itemView);
            iv_solution = itemView.findViewById(R.id.solution_image);
            tv_title = itemView.findViewById(R.id.solution_title);
            cb_heart = itemView.findViewById(R.id.solution_heart);
            tv_num = itemView.findViewById(R.id.solution_num);
        }
    }

    ArrayList<SolutionItem> solutionItems;
    ArrayList<HeartItem> heartItems;
    Context context;
    View v;

    LovesAdapter(ArrayList<SolutionItem> SolutionItems, ArrayList<HeartItem> heartItems) {
        this.solutionItems = SolutionItems;
        this.heartItems = heartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item, parent, false);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(parent, typeface);
        return new LovesHolder(v);
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
        final LovesHolder lovesHolder = (LovesHolder) holder;
        lovesHolder.cb_heart.setChecked(true);
        lovesHolder.tv_title.setText(solutionItems.get(position).contentTitle);
        lovesHolder.tv_num.setText(String.valueOf(solutionItems.get(position).loveCount));
        final String title = solutionItems.get(position).contentTitle;

        final HeartItem heartItem = heartItems.get(position);
        lovesHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = heartItem.contentPosi.split("/")[1] +"/" + heartItem.second;
                Intent intent = new Intent(context, SolutionShowActivity.class);
                intent.putExtra("key", temp);
                context.startActivity(intent);
            }
        });
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(solutionItems.get(position).contentUri);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(lovesHolder.iv_solution);
            }
        });
        final int posi = position;
        lovesHolder.cb_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(heartItem.contentPosi + "/" + heartItem.second);
                databaseReference.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        SolutionItem s = mutableData.getValue(SolutionItem.class);
                        if (s == null) {
                            return Transaction.success(mutableData);
                        }
                        s.isLove.remove(getUid());
                        s.loveCount--;
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getUid() + "/" + "loves/" + heartItem.key);
                        databaseReference.removeValue();
                        Message msg = Message.obtain();
//                        msg.arg1 = posi;
//                        msg.arg2 = s.loveCount;
//                        msg.obj = lovesHolder;
//                        handler.sendMessage(msg);
                        mutableData.setValue(s);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });

            }
        });
    }

    public String getUid() {
        String uid = "id";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return uid;
    }

    @Override
    public int getItemCount() {
        return solutionItems.size();
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            notifyItemRemoved(msg.arg1);
            solutionItems.remove(msg.arg1);
            heartItems.remove(msg.arg1);
        }
    };
}
