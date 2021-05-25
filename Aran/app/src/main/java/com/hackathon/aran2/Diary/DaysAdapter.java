package com.hackathon.aran2.Diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hackathon.aran2.R;

import java.util.ArrayList;

public class DaysAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class DaysHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_tags, tv_contents,tv_todayBaby;
        ImageButton btn_delete;
        ImageButton btn_left, btn_right;
        ViewPager vp_images;
        Button btn_modi;
        ConstraintLayout constraintLayout;
//        ImageView iv_tempV;
         DaysHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.todayInfo);
            tv_tags = view.findViewById(R.id.diary_tagEmotion);
            tv_contents = view.findViewById(R.id.contents);
            tv_todayBaby = view.findViewById(R.id.diary_todayBaby);
            btn_delete = view.findViewById(R.id.trashCan);

            vp_images = view.findViewById(R.id.diary_images);
            btn_left = view.findViewById(R.id.Diary_leftBtn);
            btn_right = view.findViewById(R.id.Diary_rightBtn);
            constraintLayout = view.findViewById(R.id.diary_viewPagerContainer);
            btn_modi = view.findViewById(R.id.diary_modify);

        }

    }
    private ArrayList<DiaryContent> diaryContents;
    private Context context;
    private Activity activity;
    DaysAdapter(Context context, ArrayList<DiaryContent> diaryContents, Activity activity) {
        this.diaryContents = diaryContents;
        this.activity = activity;
    }


    private View v;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context =  viewGroup.getContext();
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.diary, viewGroup, false);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        setGlobalFont(viewGroup, typeface);
        return new DaysHolder(v);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        final DaysHolder daysHolder = (DaysHolder) viewHolder;
        final int position = i;
        daysHolder.tv_title.setText(diaryContents.get(i).day);
        String emotion = "#"+diaryContents.get(i).emotion +", #" + diaryContents.get(i).state;
        daysHolder.tv_tags.setText(emotion);
        daysHolder.tv_todayBaby.setText(diaryContents.get(i).baby);
        daysHolder.tv_contents.setText(diaryContents.get(i).content);
        daysHolder.btn_modi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DaysAddActivity.class);
                intent.putExtra("isModi", true);
                intent.putExtra("emotionId", diaryContents.get(position).emotionId);
                intent.putExtra("emotion", diaryContents.get(position).emotion);
                intent.putExtra("state", diaryContents.get(position).state);
                intent.putExtra("stateId", diaryContents.get(position).stateId);
                intent.putExtra("baby", diaryContents.get(position).baby);
                intent.putExtra("id", diaryContents.get(position).id);
                intent.putExtra("day", diaryContents.get(position).day);
                intent.putExtra("imageUri", diaryContents.get(position).imageUri);
                intent.putExtra("content", diaryContents.get(position).content);

                context.startActivity(intent);

                activity.overridePendingTransition(R.anim.slide_left_show, R.anim.slide_left_remove);
            }
        });
        daysHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까").setCancelable(true).setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uid = "id";
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        }
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid+"/diary");
                        databaseReference.child(diaryContents.get(position).id).removeValue();
                        if(diaryContents.get(position).imageUri!=null) {
                            for (int i = 0; i < diaryContents.get(position).imageUri.size(); i++) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(diaryContents.get(position).imageUri.get(i));
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("삭제에 실패하였습니다.!").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        if(diaryContents.get(position).imageUri!=null){
            daysHolder.constraintLayout.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams temp = daysHolder.vp_images.getLayoutParams();
            temp.height = context.getResources().getDisplayMetrics().widthPixels;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            DiaryViewpageAdapter diaryViewpageAdapter = new DiaryViewpageAdapter(context,diaryContents.get(position).imageUri, true);
            for(int j = 0; j< diaryContents.get(position).imageUri.size();j++){
                System.out.println("링크 " + j +":" + diaryContents.get(position).imageUri.get(j));
            }
            if(diaryContents.get(position).imageUri.size()>1) {
                daysHolder.btn_left.setVisibility(View.VISIBLE);
                daysHolder.btn_right.setVisibility(View.VISIBLE);

                daysHolder.btn_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentItem = daysHolder.vp_images.getCurrentItem();
                        if (currentItem + 1 < diaryContents.get(position).imageUri.size()) {
                            daysHolder.vp_images.setCurrentItem(currentItem + 1);
                        }
                    }
                });
                daysHolder.btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentItem = daysHolder.vp_images.getCurrentItem();
                        if (currentItem - 1 >= 0) {
                            daysHolder.vp_images.setCurrentItem(currentItem - 1);
                        }
                    }
                });
            }else{
                daysHolder.btn_left.setVisibility(View.GONE);
                daysHolder.btn_right.setVisibility(View.GONE);
            }
            daysHolder.vp_images.setAdapter(diaryViewpageAdapter);
            //     daysHolder.iv_tempV.setImageDrawable(null);
//            StorageReference storageReference = storage.getReference().child(diaryContents.get(position).imageUri);
//            System.out.print(diaryContents.get(position).imageUri);
//            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//
//                    Glide.with(daysHolder.itemView.getContext()).load(uri.toString()).into(daysHolder.iv_tempV);
//                    daysHolder.iv_tempV.getLayoutParams().height = (int)(context.getResources().getDisplayMetrics().widthPixels*0.7);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                }
//            });

//            fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                }
//            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            });

        }else{
            //  Glide.with(daysHolder.itemView.getContext()).clear(daysHolder.iv_tempV);
//            ViewGroup.LayoutParams temp = daysHolder.vp_images.getLayoutParams();
//            temp.height = 100;
//            daysHolder.vp_images.setLayoutParams(temp);

            daysHolder.constraintLayout.setVisibility(View.GONE);


        }
    }

    @Override
    public int getItemCount() {
        return diaryContents.size();
    }

}
