package com.hackathon.aran2.Diary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DiaryViewpageAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> images;
    boolean isSmall;
    public  DiaryViewpageAdapter(Context context, ArrayList<String> images, boolean isSmall){
        super();
        this.isSmall = isSmall;
        this.context = context;
        this.images = images;

    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(images.get(position));
        final ImageView imageView = new ImageView(context);
        final int posi = position;
        ((ViewPager)container).addView(imageView);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        if(isSmall){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewPagerActivity.class);
                    intent.putStringArrayListExtra("uris", images);
                    intent.putExtra("clickPosition",posi);
                    context.startActivity(intent);


                }
            });
        }
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object o){
        container.removeView((View)o);
        container.invalidate();
    }
}
