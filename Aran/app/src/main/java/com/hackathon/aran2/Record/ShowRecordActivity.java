package com.hackathon.aran2.Record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class ShowRecordActivity extends AppCompatActivity {
    TextView tv_question;
    ImageButton btn_start, btn_pause;
    ImageView imageView;
    TextView tv_answer;
    ImageButton backBtn;
    String uid;
    SeekBar seekbar;
    int playingPosition;
    MediaPlayer mediaPlayer;
    SeekBarThread sbt;
    boolean isRecorded;
    Uri downloaded_uri;
    boolean isDownloaded;
    boolean isPlaying;
    boolean temp;
    private Typeface mTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        sbt = new SeekBarThread();
        btn_start=findViewById(R.id.showRecord_start);
        btn_pause =findViewById(R.id.showRecord_pause);
        seekbar = findViewById(R.id.showRecord_progress);
        seekbar.setClickable(false);
        tv_question = findViewById(R.id.showRecord_question);
        String q =getIntent().getStringExtra("question");
        tv_question.setText(q);
        tv_answer = findViewById(R.id.showRecord_answer);
        String answer = getIntent().getStringExtra("answer");
        if(answer!=null){
            tv_answer.setText(answer);
            tv_answer.setVisibility(View.VISIBLE);
        }else{
            tv_answer.setVisibility(View.GONE);
        }
        uid="id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        backBtn = findViewById(R.id.showRecord_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView = findViewById(R.id.showRecord_image);
        int Swidth = (int)(getApplicationContext().getResources().getDisplayMetrics().widthPixels*0.9);
        imageView.getLayoutParams().width = Swidth;
        imageView.getLayoutParams().height = Swidth;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid+"/learnEmotion/"+getIntent().getStringExtra("date")+"image");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).into(imageView);
            }
        });
        isDownloaded = false;
        isRecorded = getIntent().getBooleanExtra("isRecorded", false);
        if(!isRecorded){
            seekbar.setVisibility(View.GONE);
            btn_pause.setVisibility(View.GONE);
            btn_start.setVisibility(View.GONE);
        }else{
            StorageReference storageReference2 = FirebaseStorage.getInstance().getReference().child(uid+"/learnEmotion/"+getIntent().getStringExtra("date"));
            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloaded_uri = uri;
                    isDownloaded = true;
                    setMedia();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isDownloaded = false;
                    seekbar.setVisibility(View.GONE);
                    btn_pause.setVisibility(View.GONE);
                    btn_start.setVisibility(View.GONE);
                }
            });
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(isDownloaded&&isPlaying){
                        if(seekBar.getMax()==seekBar.getProgress()){
                            isPlaying=false;
                            mediaPlayer.seekTo(0);
                            playingPosition =0;
                            mediaPlayer.pause();
                            btn_start.setBackgroundResource(R.drawable.play_btn_drawable);
                            btn_start.setImageResource(R.drawable.play_btn_drawable);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.pause();
                    temp = isPlaying;
                    isPlaying = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (isDownloaded) {
                        playingPosition = seekBar.getProgress();
                        mediaPlayer.seekTo(playingPosition);
                        if (temp) {
                            isPlaying = true;
                            sbt = new SeekBarThread();
                            sbt.start();
                            mediaPlayer.start();
                        }
                    }
                }
            });
            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDownloaded){
                        if(isPlaying){
                            isPlaying= false;
                            btn_start.setBackgroundResource(R.drawable.play_btn_drawable);
                            btn_start.setImageResource(R.drawable.play_btn_drawable);
                            mediaPlayer.pause();
                        }else{
                            isPlaying = true;
                            btn_start.setBackgroundResource(R.drawable.pause_btn_drawable);
                            btn_start.setImageResource(R.drawable.pause_btn_drawable);
                            mediaPlayer.start();
                            sbt = new SeekBarThread();
                            sbt.start();
                        }
                    }
                }
            });
            btn_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sbt!=null) {
                        sbt.ThreadStop();
                        sbt = null;
                    }
                    isPlaying = false;
                    mediaPlayer.pause();
                    playingPosition=0;
                    seekbar.setProgress(playingPosition);
                    mediaPlayer.seekTo(0);
                    btn_start.setBackgroundResource(R.drawable.play_btn_drawable);
                    btn_start.setImageResource(R.drawable.play_btn_drawable);
                }
            });
        }
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
    public void setMedia(){
        if(isDownloaded) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), downloaded_uri);
                mediaPlayer.prepare();
                seekbar.setMax(mediaPlayer.getDuration());
                playingPosition = 0;
                seekbar.setProgress(playingPosition);
                seekbar.setClickable(isDownloaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        isPlaying = false;
        if (isDownloaded) {
            mediaPlayer.release();
        }
    }

    class SeekBarThread extends Thread {
        @Override
        public void run(){
            while(isPlaying){
                playingPosition = mediaPlayer.getCurrentPosition();
                seekbar.setProgress(playingPosition);
            }
        }
        public void ThreadStop(){
            isPlaying = false;
            playingPosition = 0;
        }
    }
}
