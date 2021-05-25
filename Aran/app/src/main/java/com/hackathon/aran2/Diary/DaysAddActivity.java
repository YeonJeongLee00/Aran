package com.hackathon.aran2.Diary;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.aran2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class  DaysAddActivity extends AppCompatActivity implements View.OnClickListener{
    EditText stateEditText, emotionEditText, todayContext;
    LinearLayout stateLayout, emotionLayout;
    CheckBox showBtn_state, showBtn_emotion;
    String date, inputDate;
    String uid;
    ViewPageAdapter viewPageAdapter;
    ArrayList<String> modiList;
    Button btn_removePhoto, btn_addDiary;
    ImageButton btn_addPhoto,btn_explain;
    ArrayList<Uri> imageList;
    RadioGroup.OnCheckedChangeListener OnCheckedChangeListener_positive,OnCheckedChangeListener_negative,OnCheckedChangeListener_input;
    RadioGroup rg_selectPositiveGroup, rg_selectNegativeGroup, rg_selectStateGroup;
    RadioGroup rg_input;
    private Typeface mTypeface;
    SimpleDateFormat dateInfo = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    Map<String, String> saveList = new HashMap<>();
    boolean isLoading = false;
    ViewPager vp_imageViews;
    ProgressDialog p;
    boolean photoModi = false;
    EditText babyDiary;
    boolean isModi;
    ImageButton left, right;
    int beforIdState, beforeIdEmotion;
    final static int SHOW_IMAGES = 13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_add);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        uid = "id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        left = findViewById(R.id.imageButton2);
        right = findViewById(R.id.imageButton1);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vp_imageViews.getAdapter()!=null){
                    int currentItem = vp_imageViews.getCurrentItem();
                    if(currentItem+1 < vp_imageViews.getAdapter().getCount()){
                        vp_imageViews.setCurrentItem(currentItem+1);
                    }
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vp_imageViews.getAdapter()!=null){
                    int currentItem = vp_imageViews.getCurrentItem();
                    if(currentItem-1 >=0){
                        vp_imageViews.setCurrentItem(currentItem-1);
                    }
                }
            }
        });
        showBtn_state = findViewById(R.id.addDiary_showState);
        showBtn_emotion = findViewById(R.id.addDiary_showEmotion);
        stateEditText = findViewById(R.id.addDiary_stateEditText);
        emotionEditText = findViewById(R.id.addDiary_emotionEditText);
        stateLayout = findViewById(R.id.addDiary_stateContainer);
        emotionLayout = findViewById(R.id.addDiary_emotionContainer);
        btn_addPhoto = findViewById(R.id.addDiary_addPhotoBtn);
        todayContext =findViewById(R.id.addDiary_Contents);
        ViewGroup.LayoutParams editTextParam = todayContext.getLayoutParams();
        editTextParam.height = (int)(getApplicationContext().getResources().getDisplayMetrics().widthPixels*0.4);
        todayContext.setLayoutParams(editTextParam);
        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(DaysAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck== PackageManager.PERMISSION_GRANTED){
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent,"사진을 골라주세요"), SHOW_IMAGES);
                }else{
                    Toast.makeText(DaysAddActivity.this, "아이의 사진을 가져오기 위해서는 갤러리 접근 권한이 필요합니다(READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    if(ActivityCompat.shouldShowRequestPermissionRationale(DaysAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(DaysAddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
                    }else{
                        ActivityCompat.requestPermissions(DaysAddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
                    }
                }
            }


        });
        showBtn_emotion.setChecked(true);
        showBtn_emotion.setOnClickListener(this);
        showBtn_state.setChecked(true);
        showBtn_state.setOnClickListener(this);

        vp_imageViews = findViewById(R.id.addDiary_photoPager);
        isModi = getIntent().getBooleanExtra("isModi", false);

        rg_selectPositiveGroup = findViewById(R.id.addDiary_positiveRadioGroup);
        rg_selectNegativeGroup = findViewById(R.id.addDiary_negativeRadioGroup);
        rg_input = findViewById(R.id.addDiary_input);
        babyDiary = findViewById(R.id.addDiary_babyDiary);
        if(!isModi){
            RadioButton radioButton = findViewById(R.id.emotion_happy);
            radioButton.setChecked(true);
            beforeIdEmotion = R.id.emotion_happy;
            radioButton.setBackgroundResource(R.drawable.custom_radio_onclick);
            emotionEditText.setText(radioButton.getText());
            emotionEditText.setClickable(false);
            emotionEditText.setEnabled(false);

            RadioButton stateRadio = findViewById(R.id.state_health);
            stateRadio.setChecked(true);
            beforIdState = R.id.state_health;
            stateRadio.setBackgroundResource(R.drawable.custom_radio_onclick);
            stateEditText.setText(stateRadio.getText());

        }else{
            babyDiary.setText(getIntent().getStringExtra("baby"));
            todayContext.setText(getIntent().getStringExtra("content"));

            int emoId = getIntent().getIntExtra("emotionId", R.id.emotion_happy);
            RadioButton radioButton = findViewById(emoId);
            if(radioButton==null){
                radioButton = findViewById(R.id.emotion_happy);
                beforeIdEmotion = R.id.emotion_happy;
                emotionEditText.setText(radioButton.getText());
                radioButton.setBackgroundResource(R.drawable.custom_radio_onclick);
            }else{
                radioButton.setChecked(true);
                beforeIdEmotion = emoId;
                emotionEditText.setText(radioButton.getText());
                radioButton.setBackgroundResource(R.drawable.custom_radio_onclick);

            }
            if(radioButton.getText().equals("직접 입력")){
                emotionEditText.setText(getIntent().getStringExtra("emotion"));
                emotionEditText.setEnabled(true);
                emotionEditText.setClickable(true);

            }else{
                emotionEditText.setEnabled(false);
                emotionEditText.setClickable(false);
            }
            int stateId = getIntent().getIntExtra("stateId", R.id.state_health);
            RadioButton stateRadio = findViewById(stateId);
            if(stateRadio==null){
                stateRadio = findViewById(R.id.state_what);
                beforIdState = R.id.state_what;
                stateRadio.setBackgroundResource(R.drawable.custom_radio_onclick);
                stateEditText.setText(stateRadio.getText());
            }else{
                stateRadio.setChecked(true);
                beforIdState = stateId;
                stateRadio.setBackgroundResource(R.drawable.custom_radio_onclick);
                stateEditText.setText(stateRadio.getText());
            }

            if(stateRadio.getText().equals("직접 입력")){
                stateEditText.setText(getIntent().getStringExtra("state"));
                stateEditText.setEnabled(true);
                stateEditText.setClickable(true);
            }else{
                stateEditText.setEnabled(false);
                stateEditText.setClickable(false);
            }
        }
        modiList = getIntent().getStringArrayListExtra("imageUri");
        btn_removePhoto = findViewById(R.id.addDiary_removePhoto);
        imageList = new ArrayList<>();
        if(modiList==null||modiList.size()==0){
            viewPageAdapter = new ViewPageAdapter(getApplicationContext(), imageList);
            vp_imageViews.setAdapter(viewPageAdapter);
            photoModi= true;
            ViewGroup.LayoutParams vp_imageViewsParam = vp_imageViews.getLayoutParams();
            vp_imageViewsParam.height = 0;

            vp_imageViews.setLayoutParams(vp_imageViewsParam);

        }else{
            photoModi = false;
            DiaryViewpageAdapter TempviewPageAdapter = new DiaryViewpageAdapter(getApplicationContext(), modiList, false);
            vp_imageViews.setAdapter(TempviewPageAdapter);
            ViewGroup.LayoutParams vp_imageViewsParam = vp_imageViews.getLayoutParams();
            vp_imageViewsParam.height = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
            vp_imageViews.setLayoutParams(vp_imageViewsParam);
            btn_removePhoto.setVisibility(View.VISIBLE);
        }
        btn_addDiary = findViewById(R.id.addDiary_btn);
        btn_addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputExist()){
                    btn_addDiary.setClickable(false);
                    isLoading = true;
                    saveInfo();
                }
            }
        });


        btn_removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DaysAddActivity.this);
                builder.setMessage("이미지를 삭제하시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        photoModi = true;
                        ViewGroup.LayoutParams vp_imageViewsParam = vp_imageViews.getLayoutParams();
                        vp_imageViewsParam.height = 0;
                        vp_imageViews.setLayoutParams(vp_imageViewsParam);
                        btn_removePhoto.setVisibility(View.GONE);
                        left.setVisibility(View.GONE);
                        right.setVisibility(View.GONE);
                        vp_imageViews.setAdapter(null);
                        imageList.clear();
                        dialog.dismiss();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        OnCheckedChangeListener_positive = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                findViewById(checkedId).setBackgroundResource(R.drawable.custom_radio_onclick);
                findViewById(beforeIdEmotion).setBackgroundResource(R.drawable.custom_radio_btn);
                beforeIdEmotion = checkedId;
                emotionEditText.setClickable(false);
                emotionEditText.setEnabled(false);
                RadioButton radioButton = findViewById(rg_selectPositiveGroup.getCheckedRadioButtonId());
                emotionEditText.setText(radioButton.getText());
                System.out.print(radioButton.getText());
                rg_selectNegativeGroup.setOnCheckedChangeListener(null);
                rg_selectNegativeGroup.clearCheck();
                rg_input.setOnCheckedChangeListener(null);
                rg_input.clearCheck();
                rg_selectNegativeGroup.setOnCheckedChangeListener(OnCheckedChangeListener_negative);
                rg_input.setOnCheckedChangeListener(OnCheckedChangeListener_input);
            }
        };
        OnCheckedChangeListener_negative = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                findViewById(checkedId).setBackgroundResource(R.drawable.custom_radio_onclick);
                findViewById(beforeIdEmotion).setBackgroundResource(R.drawable.custom_radio_btn);
                beforeIdEmotion = checkedId;
                emotionEditText.setClickable(false);
                emotionEditText.setEnabled(false);
                RadioButton radioButton = findViewById(rg_selectNegativeGroup.getCheckedRadioButtonId());
                emotionEditText.setText(radioButton.getText());
                System.out.print(radioButton.getText());
                rg_selectPositiveGroup.setOnCheckedChangeListener(null);
                rg_selectPositiveGroup.clearCheck();
                rg_input.setOnCheckedChangeListener(null);
                rg_input.clearCheck();
                rg_selectPositiveGroup.setOnCheckedChangeListener(OnCheckedChangeListener_positive);
                rg_input.setOnCheckedChangeListener(OnCheckedChangeListener_input);
            }
        };
        OnCheckedChangeListener_input = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                findViewById(checkedId).setBackgroundResource(R.drawable.custom_radio_onclick);
                findViewById(beforeIdEmotion).setBackgroundResource(R.drawable.custom_radio_btn);
                beforeIdEmotion = checkedId;
                emotionEditText.setEnabled(true);
                emotionEditText.setClickable(true);
                emotionEditText.setText("");
                emotionEditText.setHint("직접 감정을 입력하세요");
                rg_selectNegativeGroup.setOnCheckedChangeListener(null);
                rg_selectNegativeGroup.clearCheck();
                rg_selectPositiveGroup.setOnCheckedChangeListener(null);
                rg_selectPositiveGroup.clearCheck();
                rg_selectPositiveGroup.setOnCheckedChangeListener(OnCheckedChangeListener_positive);
                rg_selectNegativeGroup.setOnCheckedChangeListener(OnCheckedChangeListener_negative);
            }
        };
        rg_selectNegativeGroup.setOnCheckedChangeListener(OnCheckedChangeListener_negative);
        rg_selectPositiveGroup.setOnCheckedChangeListener(OnCheckedChangeListener_positive);
        rg_input.setOnCheckedChangeListener(OnCheckedChangeListener_input);
        rg_selectStateGroup = findViewById(R.id.addDiary_selectStateGroup);
        rg_selectStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                findViewById(checkedId).setBackgroundResource(R.drawable.custom_radio_onclick);
                findViewById(beforIdState).setBackgroundResource(R.drawable.custom_radio_btn);
                beforIdState = checkedId;
                stateEditText.setEnabled(false);
                stateEditText.setClickable(false);
                stateEditText.setText(((RadioButton)findViewById(checkedId)).getText());
                if(((RadioButton)findViewById(checkedId)).getText().equals("직접 입력")){
                    stateEditText.setEnabled(true);
                    stateEditText.setClickable(true);
                    stateEditText.setHint("아이의 상태를 입력하세요");
                    stateEditText.setText("");
                }
            }
        });
        final ImageButton addDiary_back = findViewById(R.id.addDiary_backBtn);
        addDiary_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btn_explain = findViewById(R.id.addDiary_explainPhoto);
        btn_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DaysAddActivity.this);
                builder.setTitle("사진 추가 방법").setMessage("1. 사진 추가 버튼을 누릅니다.\n2.두 개 이상의 사진을 고르고 싶을 땐 꾹 눌러서 추가해주세요.\n※세 장까지 추가할 수 있습니다.").setCancelable(true).setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

    public boolean checkInputExist(){
        if(emotionEditText.getText().toString().equals("")){
            Toast.makeText(this, "아이의 감정이 입력되지 않았어요!", Toast.LENGTH_LONG).show();
            return false;
        }else if(stateEditText.getText().toString().equals("")){
            Toast.makeText(this, "아이의 상태가 입력되지 않았어요!", Toast.LENGTH_LONG).show();
            return false;
        }else if(babyDiary.getText().toString().equals("")){
            Toast.makeText(this, "오늘 어떤일이 있었는 지 입력되지 않았어요!", Toast.LENGTH_LONG).show();

            return false;
        }else{
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case SHOW_IMAGES:
                    btn_removePhoto.setVisibility(View.VISIBLE);
                    if(data.getClipData()!=null) {
                        right.setVisibility(View.VISIBLE);
                        left.setVisibility(View.VISIBLE);
                        photoModi=true;
                        imageList.clear();
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            if(i>=3){
                                break;
                            }
                            imageList.add(clipData.getItemAt(i).getUri());
                        }

                        ViewGroup.LayoutParams vp_imageViewsParam = vp_imageViews.getLayoutParams();
                        vp_imageViewsParam.height = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                        vp_imageViews.setLayoutParams(vp_imageViewsParam);

                        viewPageAdapter = new ViewPageAdapter(getApplicationContext(),imageList);
                        vp_imageViews.setAdapter(viewPageAdapter);

                    }else if(data.getData()!=null){
                        right.setVisibility(View.INVISIBLE);
                        left.setVisibility(View.INVISIBLE);
                        imageList.clear();
                        imageList.add(data.getData());
                        photoModi = true;

                        vp_imageViews.setAdapter(new ViewPageAdapter(getApplicationContext(), imageList));
                        ViewGroup.LayoutParams vp_imageViewsParam = vp_imageViews.getLayoutParams();
                        vp_imageViewsParam.height = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                        vp_imageViews.setLayoutParams(vp_imageViewsParam);
                    }

                    break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(!isLoading){
            super.onBackPressed();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addDiary_showEmotion:
                if(showBtn_emotion.isChecked()){
                    emotionLayout.setVisibility(View.VISIBLE);
                }else{
                    emotionLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.addDiary_showState:
                if(showBtn_state.isChecked()){
                    stateLayout.setVisibility(View.VISIBLE);
                }else{
                    stateLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    public void saveInfo(){

        String emotion = emotionEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String content = todayContext.getText().toString();
        saveList.clear();
        Date today = new Date();
        date = data.format(today);
        inputDate = dateInfo.format(today);
        if(isModi){
            date = getIntent().getStringExtra("id");
            inputDate = getIntent().getStringExtra("day");
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(uid+"/diary").child(date);
        final Hashtable<String, Object> diaryContent = new Hashtable<>();
        diaryContent.put("id", date);
        diaryContent.put("emotion", emotion);
        diaryContent.put("state", state);
        diaryContent.put("content", content);
        diaryContent.put("baby", babyDiary.getText().toString());
        diaryContent.put("emotionId", beforeIdEmotion);
        diaryContent.put("stateId", beforIdState);

        diaryContent.put("day", inputDate);
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        p = new ProgressDialog(DaysAddActivity.this);
        p.setCancelable(false);
        p.setTitle("클라우드에 올리는 중입니다.");
        p.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        p.show();
        final ArrayList<String> imageUri =new ArrayList<>();
        if(!photoModi){
            diaryContent.put("imageUri", modiList);
            databaseReference.setValue(diaryContent);
            p.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(DaysAddActivity.this);
            builder.setTitle("일기 저장").setMessage("일기가 성공적으로 저장되었습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("id", date);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }else {
            for (int i = 0; i < imageList.size(); i++) {
                final StorageReference storageReference = storage.getReference().child(uid+"/diary/" + date + "_" + i + ".jpg");
                final String saveFile = uid+"/diary/" + date + "_" + i + ".jpg";
                UploadTask uploadTask = storageReference.putFile(imageList.get(i));

                final int position = i;
                p.setMessage(i + "/" + imageList.size());
                double progress = 100.0 * ((double)i / (double)imageList.size());
                p.setProgress((int) progress);

                Task<Uri> uriTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * ((double)taskSnapshot.getBytesTransferred() /(double) taskSnapshot.getTotalByteCount());
                        p.setProgress((int) progress);

                        System.out.println("Upload is " + progress + "% done" + position);
                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            imageUri.add(saveFile);
                            if (position == imageList.size() - 1) {
                                diaryContent.put("imageUri", imageUri);
                                databaseReference.setValue(diaryContent);
                                p.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(DaysAddActivity.this);
                                builder.setTitle("일기 저장").setMessage("일기가 성공적으로 저장되었습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("id", date);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }
                });
            }
            if (imageList.size() <= 0) {

                databaseReference.setValue(diaryContent);
                p.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(DaysAddActivity.this);
                builder.setTitle("일기 저장").setMessage("일기가 성공적으로 저장되었습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("id", date);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }

    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.slide_right_show, R.anim.slide_right_remove);
    }

    public Bitmap exifOrientationToDegrees(Bitmap bitmap, int exifOrientation) {
        Matrix matrix = new Matrix();
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.setRotate(90);
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            matrix.setRotate(180);
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            matrix.setRotate(270);
        } else {
            return bitmap;
        }
        try {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotated;
        } catch (OutOfMemoryError e) {
            return bitmap;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        cursor.close();

        return path;
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
