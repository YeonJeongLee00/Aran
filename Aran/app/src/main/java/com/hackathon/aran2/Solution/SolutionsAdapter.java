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
import java.util.HashMap;


public class SolutionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public class SolutionHolder extends RecyclerView.ViewHolder {
        ImageView iv_solution;
        TextView tv_title, tv_num;
        CheckBox cb_heart;

        SolutionHolder(@NonNull View itemView) {
            super(itemView);
            iv_solution = itemView.findViewById(R.id.solution_image);
            tv_title = itemView.findViewById(R.id.solution_title);
            cb_heart = itemView.findViewById(R.id.solution_heart);
            tv_num = itemView.findViewById(R.id.solution_num);
        }
    }

    ArrayList<SolutionItem> solutionItems;
    Context context;
    View v;
    String caseString;

    SolutionsAdapter(String caseString, ArrayList<SolutionItem> solutionItems) {
        this.solutionItems = solutionItems;
        this.caseString = caseString;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item, parent, false);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(parent, typeface);
        return new SolutionHolder(v);
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
        final SolutionHolder solutionHolder = (SolutionHolder) holder;

        final int tempPosi = position + 1;
        solutionHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SolutionShowActivity.class);
                intent.putExtra("key",caseString + "/contents" +tempPosi);
                context.startActivity(intent);
            }
        });
        solutionHolder.cb_heart.setChecked(false);
        solutionHolder.tv_title.setText(solutionItems.get(position).contentTitle);
        solutionHolder.tv_num.setText(String.valueOf(solutionItems.get(position).isLove.size()));
        if (solutionItems.get(position).isLove != null) {
            if (solutionItems.get(position).isLove.containsKey(getUid())) {
                solutionHolder.cb_heart.setChecked(true);
            }
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(solutionItems.get(position).contentUri);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(solutionHolder.iv_solution);
            }
        });

        solutionHolder.cb_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("solution/" + caseString + "/contents" + tempPosi);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("solution/" + caseString + "/contents" + tempPosi);
                databaseReference.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        SolutionItem s = mutableData.getValue(SolutionItem.class);
                        if (s == null) {
                            return Transaction.success(mutableData);
                        }
                        if (s.isLove.containsKey(getUid())) {
                            s.loveCount--;
                            s.isLove.remove(getUid());
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(getUid() + "/loves/"+s.contentTitle);
                            databaseReference1.removeValue();
                            solutionItems.get(position).isLove.remove(getUid());
                        } else {
                            s.loveCount++;
                            s.isLove.put(getUid(), true);
                            solutionItems.get(position).isLove.put(getUid(), true);
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(getUid() + "/loves/"+s.contentTitle);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("key", s.contentTitle);

                            hashMap.put("date", System.currentTimeMillis());
                            hashMap.put("second", "contents" + tempPosi);
                            hashMap.put("contentPosi","solution/" + caseString);
                            databaseReference1.setValue(hashMap);
                        }
                        solutionItems.get(position).loveCount = s.loveCount;
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.arg2 = s.loveCount;
                        msg.obj = solutionHolder;
                        handler.sendMessage(msg);
//                        solutionHolder.tv_num.setText(String.valueOf(s.loveCount));
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
            ((SolutionHolder) msg.obj).tv_num.setText(String.valueOf(msg.arg2));
        }
    };

}
