package com.hackathon.aran2.Solution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SolutionShowActivity extends AppCompatActivity {
    private Typeface mTypeface;
    TextView tv_title, tv_date, tv_contents;
    ImageView iv_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_show);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String posi = getIntent().getStringExtra("key");
        System.out.println(posi);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("realContents/"+ posi);
        tv_contents = findViewById(R.id.solution_show_content);
        tv_date = findViewById(R.id.solution_show_date);
        tv_title = findViewById(R.id.solution_show_title);
        iv_image = findViewById(R.id.solution_show_image);
        iv_image.getLayoutParams().height = (int)((getApplicationContext().getResources().getDisplayMetrics().widthPixels)*0.6);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RealSolutionItem realSolutionItem = dataSnapshot.getValue(RealSolutionItem.class);
                tv_contents.setText(realSolutionItem.contents);
                tv_date.setText(realSolutionItem.date);
                tv_title.setText(realSolutionItem.title);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(realSolutionItem.imageUri);
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(SolutionShowActivity.this).load(uri.toString()).into(iv_image);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
