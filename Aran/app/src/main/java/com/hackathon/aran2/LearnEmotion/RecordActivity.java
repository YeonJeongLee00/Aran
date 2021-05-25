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
    // 완료버튼
    Button saveBtn, backBtn;
    ImageButton play,pause;
    Button record;
    SeekBar seekBar;
    SeekBarThread sbt;

    private boolean isRead;
    boolean isRecord = false;
    int position = 0; // 다시 시작 기능을 위한 현재 재생 위치 확인 변수
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
        permissionCheck(); // 권한 확인
        byteArray = getIntent().getByteArrayExtra("drawing");
        File sdcard = Environment.getExternalStorageDirectory();
        final File file = new File(sdcard, "recorded.mp4");

        filename = file.getPath();
        filename = file.getAbsolutePath();
        Log.d("MainActivity", "저장할 파일 명 : " + filename);
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
        // 감정 받아오기.

        editText = findViewById(R.id.EditText);
        // 저장하기 누르면 녹음파일이 db에 저장되어야함
        // intent를 했을때 drawImageActivity로 값을 전달하는 동시에 main으로 intent해야하는데 그걸 모르겠음

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
                        Toast.makeText(getApplicationContext(), "질문에 대한 답변 또는 녹음을 해주세요!", Toast.LENGTH_LONG).show();
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
                    p.setTitle("학습한 기록을 클라우드에 올리고 있습니다.");
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
                                            builder.setTitle("학습 기록 저장").setMessage("감정을 학습한 기록이 저장 완료되었습니다!").setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                builder.setTitle("학습 기록 저장").setMessage("감정을 학습한 기록이 저장 완료되었습니다!").setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

        // 재생
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying) {
                    isPlaying = true;
                    play.setBackgroundResource(R.drawable.pause_btn_drawable);
                    play.setImageResource(R.drawable.pause_btn_drawable);
//                    play.setText("일시정지");
                    player.start();
                    sbt = new SeekBarThread();
                    sbt.start();

                }else{
                    isPlaying= false;
                    play.setBackgroundResource(R.drawable.play_btn_drawable);
                    play.setImageResource(R.drawable.play_btn_drawable);
//                    play.setText("재생");
                    player.pause();
                }
//                if(ClickPlay == 0 | ClickPlay == 3) {
//                    playAudio();
//                    play.setText("재생");
//                    ClickPlay = 1;
//                }else if(ClickPlay == 1){
//                    pauseAudio();
//                    ClickPlay = 2 ;
//                    play.setText("일시정지");
//                }else if(ClickPlay == 2){
//                    resumeAudio();
//                    play.setText("재생");
//                    ClickPlay = 3;
//                }
//                playAudio();

                // +
            }
        });
        //일시정지
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sbt!=null){
                    sbt.ThreadStop();
                    sbt=null;
                }
                play.setBackgroundResource(R.drawable.play_btn_drawable);
                play.setImageResource(R.drawable.play_btn_drawable);
//                play.setText("재생");
                player.pause();
                player.seekTo(0);
                seekBar.setProgress(0);
                isPlaying = false;
            }
        });
        // 재시작

        // 정지
        // 녹음하기
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickRecord == 0){
//                    play.setText("재생");
                    play.setEnabled(false);
                    pause.setEnabled(false);
                    recordAudio();
                    record.setText("녹음중..");
                    ClickRecord = 1;
                }else if(ClickRecord == 1){
                    stopRecording();
                    pause.setEnabled(true);
                    play.setEnabled(true);
                    record.setText("녹음하기");
                    ClickRecord = 0;
                }
            }
        });
        // 녹음중지

    }

    String EmotionData[] = {"부러움", "행복", "서운함", "사랑", "슬픔", "뿌듯함", "무서움", "부끄러움", "미안", "놀람", "고마움", "화남"};

    // 부러움
    String Q0[] = {"부러운 감정을 느껴본적이 있나요?", "언제 부러운 감정을 느낄 것 같나요?", "부럽다는 감정을 느꼈을 때 어떻게 하고 싶나요?", "고맙다는 감정을 표현해보세요."};
    // 행복
    String Q1[] = {"행복한 감정을 느껴본적이 있나요?", "언제 행복이란 감정을 느낄 것 같나요?", "행복한 감정을 느꼈을 때 어떻게 하고 싶나요?", "행복한 감정을 표현해보세요."};
    //서운함
    String Q2[] = {"서운한 감정을 느껴본적이 있나요?", "언제 서운한 감정을 느낄 것 같나요?", "서운한 감정을 느꼈을 때 어떻게 하고 싶나요?", "서운한 감정을 표현해보세요."};
    // 사랑
    String Q3[] = {"사랑이란 감정을 느껴본적이 있나요?", "언제 사랑이란 감정을 느낄 것 같나요?", "사랑이란 감정을 느꼈을 때 어떻게 하고 싶나요?", "사랑이란 감정을 표현해보세요."};
    // 슬픔
    String Q4[] = {"슬픈 감정을 느껴본적이 있나요?", "언제 슬픈 감정을 느낄 것 같나요?", "슬픈 감정을 느꼈을 때 어떻게 하고 싶나요?", "슬픈 감정을 표현해보세요."};
    // 뿌듯함
    String Q5[] = {"뿌듯한 감정을 느껴본적이 있나요?", "언제 뿌듯한 감정을 느낄 것 같나요?", "뿌듯한 감정을 느꼈을 때 어떻게 하고 싶나요?", "뿌듯한 감정을 표현해보세요."};
    // 무서움
    String Q6[] = {"무서운 감정을 느껴본적이 있나요?", "언제 무서운 감정을 느낄 것 같나요?", "무서운 감정을 느꼈을 때 어떻게 하고 싶나요?", "무서운 감정을 표현해보세요."};
    // 부끄러움
    String Q7[] = {"부끄러운 감정을 느껴본적이 있나요?", "언제 부끄러운 감정을 느낄 것 같나요?", "부끄러운 감정을 느꼈을 때 어떻게 하고 싶나요?", "부끄러운 감정을 표현해보세요."};
    // 미안
    String Q8[] = {"미안한 감정을 느껴본적이 있나요?", "언제 미안한 감정을 느낄 것 같나요?", "미안한 감정을 느꼈을 때 어떻게 하고 싶나요?", "미안한 감정을 표현해보세요."};
    // 놀람
    String Q9[] = {"놀란다는 감정을 느껴본적이 있나요?", "언제 놀란다는 감정을 느낄 것 같나요?", "놀란다는 감정을 느꼈을 때 어떻게 하고 싶나요?", "놀란다는 감정을 표현해보세요."};
    //고마움
    String Q10[] = {"고마운 감정을 느껴본적이 있나요?", "언제 고마운 감정을 느낄 것 같나요?", "고마운 감정을 느꼈을 때 어떻게 하고 싶나요?", "고맙운 감정을 표현해보세요."};
    // 화남
    String Q11[] = {"화난 감정을 느껴본적이 있나요?", "언제 화난 감정을 느낄 것 같나요?", "화난 감정을 느꼈을 때 어떻게 하고 싶나요?", "화난 감정을 표현해보세요."};


    private void showText() {
        Random random = new Random();
        int i;
        System.out.println(emotion + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");


        if (emotion.equals("부러움")) {
            i = random.nextInt(Q0.length - 1);
            question.setText(Q0[i]);
        } else if (emotion.equals("행복")) {
            i = random.nextInt(Q1.length - 1);
            question.setText(Q1[i]);
        } else if (emotion.equals("서운함")) {
            i = random.nextInt(Q2.length - 1);
            question.setText(Q2[i]);
        } else if (emotion.equals("사랑")) {
            i = random.nextInt(Q3.length - 1);
            question.setText(Q3[i]);
        } else if (emotion.equals("슬픔")) {
            i = random.nextInt(Q4.length - 1);
            question.setText(Q4[i]);
        } else if (emotion.equals("뿌듯함")) {
            i = random.nextInt(Q5.length - 1);
            question.setText(Q5[i]);
        } else if (emotion.equals("무서움")) {
            i = random.nextInt(Q6.length - 1);
            question.setText(Q6[i]);
        } else if (emotion.equals("부끄러움")) {
            i = random.nextInt(Q7.length - 1);
            question.setText(Q7[i]);
        } else if (emotion.equals("미안")) {
            i = random.nextInt(Q8.length - 1);
            question.setText(Q8[i]);
        } else if (emotion.equals("놀람")) {
            i = random.nextInt(Q9.length - 1);
            question.setText(Q9[i]);
        } else if (emotion.equals("고마움")) {
            i = random.nextInt(Q10.length - 1);
            question.setText(Q10[i]);
        } else if (emotion.equals("화남")) {
            i = random.nextInt(Q11.length - 1);
            question.setText(Q11[i]);
        } else if (emotion.equals("random")) {
            // 여기서 질문을 어떻게 뽑아야 할지 모르겠다.
            i = random.nextInt(Q0.length - 1);
            question.setText(Q0[i]);
        }

    }

    // 녹음하기
    private void recordAudio() {
        recorder = new MediaRecorder();
        isRecord = true;
        /* 그대로 저장하면 용량이 크다.
         * 프레임 : 한 순간의 음성이 들어오면, 음성을 바이트 단위로 전부 저장하는 것
         * 초당 15프레임 이라면 보통 8K(8000바이트) 정도가 한순간에 저장됨
         * 따라서 용량이 크므로, 압축할 필요가 있음 */
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 어디에서 음성 데이터를 받을 것인지
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 압축 형식 설정
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setOutputFile(filename);

        try {
            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "녹음 시작됨.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 녹음 중지
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
                Toast.makeText(this, "녹음 완료됨.", Toast.LENGTH_SHORT).show();
                pause.setEnabled(true);
                play.setEnabled(true);
                record.setText("녹음하기");
                ClickRecord = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 재생
    private void playAudio() {

        // closePlayer();

//            player = new MediaPlayer();
//            player.setDataSource(filename);

        player.start();

        Toast.makeText(this, "재생 시작됨.", Toast.LENGTH_SHORT).show();

    }

    // 일시정지
    private void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();

            Toast.makeText(this, "일시정지됨.", Toast.LENGTH_SHORT).show();
        }
    }

    // 재시작
    private void resumeAudio() {
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();

            Toast.makeText(this, "재시작됨.", Toast.LENGTH_SHORT).show();
        }
    }

    // 정지
    private void stopAudio() {
        if (player != null && player.isPlaying()) {
            player.stop();

            Toast.makeText(this, "중지됨.", Toast.LENGTH_SHORT).show();
        }
    }

    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // 권한체크
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
