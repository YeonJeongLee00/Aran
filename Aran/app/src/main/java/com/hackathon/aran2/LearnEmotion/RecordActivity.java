package com.hackathon.aran2.LearnEmotion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class RecordActivity extends AppCompatActivity {
    MediaRecorder recorder;
    SimpleDateFormat dateInfo = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    ImageView imageView;
    String uid;
    String filename;
    Uri fileUri;
    MediaPlayer player;
    EditText editText;
    private TextView question;
    boolean isClicked = false;
    // ????????????
    Button saveBtn, backBtn;
    ImageButton play,pause;
    Button record;
    SeekBar seekBar;
    SeekBarThread sbt;

    private boolean isRead;
    boolean isRecord = false;
    int position = 0; // ?????? ?????? ????????? ?????? ?????? ?????? ?????? ?????? ??????
    byte[] byteArray;
    int ClickPlay = 0;
    int ClickRecord = 0;
    boolean isPlaying;
    String emotion;
    boolean temp;

    private Typeface mTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        permissionCheck(); // ?????? ??????
        byteArray = getIntent().getByteArrayExtra("drawing");
        File sdcard = Environment.getExternalStorageDirectory();
        final File file = new File(sdcard, "recorded.mp4");

        filename = file.getPath();
        filename = file.getAbsolutePath();
        Log.d("MainActivity", "????????? ?????? ??? : " + filename);
        question = findViewById(R.id.question);
        backBtn = findViewById(R.id.back);
        saveBtn = findViewById(R.id.save);
        Intent intent = getIntent();
        emotion = intent.getStringExtra("emotion");
        System.out.println(emotion + "DDDD");
        showText();

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        record = findViewById(R.id.record);
        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.imageView);
        Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(img);

        play.setEnabled(false);
        pause.setEnabled(false);
        // ?????? ????????????.

        editText = findViewById(R.id.EditText);
        // ???????????? ????????? ??????????????? db??? ??????????????????
        // intent??? ????????? drawImageActivity??? ?????? ???????????? ????????? main?????? intent??????????????? ?????? ????????????

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                return;
            }
        });
        sbt = new SeekBarThread();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(isPlaying){
                    if(seekBar.getMax() == seekBar.getProgress()){
                        isPlaying=false;
                        player.seekTo(0);
                        position = 0;
                        player.pause();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.pause();
                temp = isPlaying;
                isPlaying = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                position = seekBar.getProgress();
                player.seekTo(position);
                if (temp) {
                    isPlaying = true;
                    sbt = new SeekBarThread();
                    sbt.start();
                    player.start();
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isClicked) {
                    if (!isRecord && editText.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "????????? ?????? ?????? ?????? ????????? ????????????!", Toast.LENGTH_LONG).show();
                        isClicked = false;
                        return;
                    }
                    isClicked = true;
                    fileUri = Uri.fromFile(file);

                    final Date dateD = new Date();
                    uid = "id";
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    }
                    final String date = data.format(dateD);
                    final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                    StorageReference storageReference = firebaseStorage.getReference(uid + "/learnEmotion").child(date);

                    final ProgressDialog p = new ProgressDialog(RecordActivity.this);
                    p.setCancelable(false);
                    p.setTitle("????????? ????????? ??????????????? ????????? ????????????.");
                    p.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
                    p.show();
                    if (isRecord) {
                        UploadTask uploadTask = storageReference.putFile(fileUri);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    StorageReference reference = firebaseStorage.getReference(uid + "/learnEmotion").child(date + "image");
                                    UploadTask uploadTask = reference.putBytes(byteArray);
                                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid + "/learnEmotion").child(date);
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("isRecorded", isRecord);
                                            hashMap.put("date", date);
                                            hashMap.put("stringRecord", editText.getText().toString());
                                            hashMap.put("question", question.getText().toString());
                                            hashMap.put("emotion", emotion);
                                            databaseReference.setValue(hashMap);
                                            p.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                                            builder.setTitle("?????? ?????? ??????").setMessage("????????? ????????? ????????? ?????? ?????????????????????!").setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent outintent = new Intent();
                                                    setResult(RESULT_OK, outintent);
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                        }
                                    });

                                }
                            }
                        });
                    }else{

                        StorageReference reference = firebaseStorage.getReference(uid + "/learnEmotion").child(date + "image");
                        UploadTask uploadTask = reference.putBytes(byteArray);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid + "/learnEmotion").child(date);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isRecorded", isRecord);
                                hashMap.put("date", date);
                                hashMap.put("stringRecord", editText.getText().toString());
                                hashMap.put("question", question.getText().toString());
                                hashMap.put("emotion", emotion);
                                databaseReference.setValue(hashMap);
                                p.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                                builder.setTitle("?????? ?????? ??????").setMessage("????????? ????????? ????????? ?????? ?????????????????????!").setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent outintent = new Intent();
                                        setResult(RESULT_OK, outintent);
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        });
                    }
                }
            }
        });

        // ??????
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying) {
                    isPlaying = true;
                    play.setBackgroundResource(R.drawable.pause_btn_drawable);
                    play.setImageResource(R.drawable.pause_btn_drawable);
//                    play.setText("????????????");
                    player.start();
                    sbt = new SeekBarThread();
                    sbt.start();

                }else{
                    isPlaying= false;
                    play.setBackgroundResource(R.drawable.play_btn_drawable);
                    play.setImageResource(R.drawable.play_btn_drawable);
//                    play.setText("??????");
                    player.pause();
                }
//                if(ClickPlay == 0 | ClickPlay == 3) {
//                    playAudio();
//                    play.setText("??????");
//                    ClickPlay = 1;
//                }else if(ClickPlay == 1){
//                    pauseAudio();
//                    ClickPlay = 2 ;
//                    play.setText("????????????");
//                }else if(ClickPlay == 2){
//                    resumeAudio();
//                    play.setText("??????");
//                    ClickPlay = 3;
//                }
//                playAudio();

                // +
            }
        });
        //????????????
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sbt!=null){
                    sbt.ThreadStop();
                    sbt=null;
                }
                play.setBackgroundResource(R.drawable.play_btn_drawable);
                play.setImageResource(R.drawable.play_btn_drawable);
//                play.setText("??????");
                player.pause();
                player.seekTo(0);
                seekBar.setProgress(0);
                isPlaying = false;
            }
        });
        // ?????????

        // ??????
        // ????????????
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickRecord == 0){
//                    play.setText("??????");
                    play.setEnabled(false);
                    pause.setEnabled(false);
                    recordAudio();
                    record.setText("?????????..");
                    ClickRecord = 1;
                }else if(ClickRecord == 1){
                    stopRecording();
                    pause.setEnabled(true);
                    play.setEnabled(true);
                    record.setText("????????????");
                    ClickRecord = 0;
                }
            }
        });
        // ????????????

    }

    String EmotionData[] = {"?????????", "??????", "?????????", "??????", "??????", "?????????", "?????????", "????????????", "??????", "??????", "?????????", "??????"};

    // ?????????
    String Q0[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "???????????? ????????? ????????? ??? ????????? ?????? ??????????", "???????????? ????????? ??????????????????."};
    // ??????
    String Q1[] = {"????????? ????????? ??????????????? ??????????", "?????? ???????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    //?????????
    String Q2[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    // ??????
    String Q3[] = {"???????????? ????????? ??????????????? ??????????", "?????? ???????????? ????????? ?????? ??? ??????????", "???????????? ????????? ????????? ??? ????????? ?????? ??????????", "???????????? ????????? ??????????????????."};
    // ??????
    String Q4[] = {"?????? ????????? ??????????????? ??????????", "?????? ?????? ????????? ?????? ??? ??????????", "?????? ????????? ????????? ??? ????????? ?????? ??????????", "?????? ????????? ??????????????????."};
    // ?????????
    String Q5[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    // ?????????
    String Q6[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    // ????????????
    String Q7[] = {"???????????? ????????? ??????????????? ??????????", "?????? ???????????? ????????? ?????? ??? ??????????", "???????????? ????????? ????????? ??? ????????? ?????? ??????????", "???????????? ????????? ??????????????????."};
    // ??????
    String Q8[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    // ??????
    String Q9[] = {"???????????? ????????? ??????????????? ??????????", "?????? ???????????? ????????? ?????? ??? ??????????", "???????????? ????????? ????????? ??? ????????? ?????? ??????????", "???????????? ????????? ??????????????????."};
    //?????????
    String Q10[] = {"????????? ????????? ??????????????? ??????????", "?????? ????????? ????????? ?????? ??? ??????????", "????????? ????????? ????????? ??? ????????? ?????? ??????????", "????????? ????????? ??????????????????."};
    // ??????
    String Q11[] = {"?????? ????????? ??????????????? ??????????", "?????? ?????? ????????? ?????? ??? ??????????", "?????? ????????? ????????? ??? ????????? ?????? ??????????", "?????? ????????? ??????????????????."};


    private void showText() {
        Random random = new Random();
        int i;
        System.out.println(emotion + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");


        if (emotion.equals("?????????")) {
            i = random.nextInt(Q0.length - 1);
            question.setText(Q0[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q1.length - 1);
            question.setText(Q1[i]);
        } else if (emotion.equals("?????????")) {
            i = random.nextInt(Q2.length - 1);
            question.setText(Q2[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q3.length - 1);
            question.setText(Q3[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q4.length - 1);
            question.setText(Q4[i]);
        } else if (emotion.equals("?????????")) {
            i = random.nextInt(Q5.length - 1);
            question.setText(Q5[i]);
        } else if (emotion.equals("?????????")) {
            i = random.nextInt(Q6.length - 1);
            question.setText(Q6[i]);
        } else if (emotion.equals("????????????")) {
            i = random.nextInt(Q7.length - 1);
            question.setText(Q7[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q8.length - 1);
            question.setText(Q8[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q9.length - 1);
            question.setText(Q9[i]);
        } else if (emotion.equals("?????????")) {
            i = random.nextInt(Q10.length - 1);
            question.setText(Q10[i]);
        } else if (emotion.equals("??????")) {
            i = random.nextInt(Q11.length - 1);
            question.setText(Q11[i]);
        } else if (emotion.equals("random")) {
            // ????????? ????????? ????????? ????????? ?????? ????????????.
            i = random.nextInt(Q0.length - 1);
            question.setText(Q0[i]);
        }

    }

    // ????????????
    private void recordAudio() {
        recorder = new MediaRecorder();
        isRecord = true;
        /* ????????? ???????????? ????????? ??????.
         * ????????? : ??? ????????? ????????? ????????????, ????????? ????????? ????????? ?????? ???????????? ???
         * ?????? 15????????? ????????? ?????? 8K(8000?????????) ????????? ???????????? ?????????
         * ????????? ????????? ?????????, ????????? ????????? ?????? */
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // ???????????? ?????? ???????????? ?????? ?????????
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // ?????? ?????? ??????
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setOutputFile(filename);

        try {
            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "?????? ?????????.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ?????? ??????
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            player = new MediaPlayer();
            try {
                player.setDataSource(filename);
                player.prepare();
                seekBar.setMax(player.getDuration());
                isPlaying =false;
                Toast.makeText(this, "?????? ?????????.", Toast.LENGTH_SHORT).show();
                pause.setEnabled(true);
                play.setEnabled(true);
                record.setText("????????????");
                ClickRecord = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ??????
    private void playAudio() {

        // closePlayer();

//            player = new MediaPlayer();
//            player.setDataSource(filename);

        player.start();

        Toast.makeText(this, "?????? ?????????.", Toast.LENGTH_SHORT).show();

    }

    // ????????????
    private void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();

            Toast.makeText(this, "???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    // ?????????
    private void resumeAudio() {
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();

            Toast.makeText(this, "????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    // ??????
    private void stopAudio() {
        if (player != null && player.isPlaying()) {
            player.stop();

            Toast.makeText(this, "?????????.", Toast.LENGTH_SHORT).show();
        }
    }

    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // ????????????
    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
    class SeekBarThread extends Thread {
        @Override
        public void run(){
            while(isPlaying){
                position = player.getCurrentPosition();
                seekBar.setProgress(position);

            }
        }
        public void ThreadStop(){
            player.pause();
            isPlaying = false;
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
    @Override
    public void onPause(){
        super.onPause();
        this.stopRecording();

    }


}
