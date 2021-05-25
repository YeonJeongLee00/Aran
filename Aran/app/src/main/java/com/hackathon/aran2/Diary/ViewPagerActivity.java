package com.hackathon.aran2.Diary;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hackathon.aran2.R;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity {
    ViewPager vp_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        vp_photo = findViewById(R.id.ViewPagerAct_photoPager);
        ArrayList<String> uris = getIntent().getStringArrayListExtra("uris");
        DiaryViewpageAdapter viewPageAdapter = new DiaryViewpageAdapter(getApplicationContext(),uris, false);
        int position =getIntent().getIntExtra("clickPosition",0);

        vp_photo.setAdapter(viewPageAdapter);
        vp_photo.setCurrentItem(position);
    }
}
