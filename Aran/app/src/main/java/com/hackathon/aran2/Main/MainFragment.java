package com.hackathon.aran2.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hackathon.aran2.InfoBean;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    Button button;
    View v;
    ImageView iv_babyPhoto;
    TextView tv_name, tv_age, tv_signUpDate;
    final int RECEIVE_PHOTO = 11;
    final int IMAGE_CROP = 12;
    final int IMAGE_SAVE = 13;
    String uid;
    Uri imageUri;
    InfoBean infoBean;
    boolean isLoad;
    StorageReference storageReference;
    Button logOutButton;
    String Realuri;
    Long signUpLong;
    String name;
    Integer age;
    private Typeface typeface;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(),
                    "BinggraeMelona.ttf");
        }
        setGlobalFont((ViewGroup) v);
        iv_babyPhoto = v.findViewById(R.id.main_babyImage);
        tv_name = v.findViewById(R.id.main_babyName);
        uid = "id";

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        tv_age = v.findViewById(R.id.main_babyAge);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable backGround = (GradientDrawable) getContext().getDrawable(R.drawable.image_round);
            iv_babyPhoto.setBackground(backGround);
            iv_babyPhoto.setClipToOutline(true);
        }
        tv_signUpDate = v.findViewById(R.id.main_signUpDate);
        iv_babyPhoto.getLayoutParams().width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.5);
        iv_babyPhoto.getLayoutParams().height = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.5);


        button = v.findViewById(R.id.main_selectPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if(permissionCheck== PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RECEIVE_PHOTO);
                }else{
                   if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Toast.makeText(getContext(), "아이의 사진을 가져오기 위해서는 갤러리 접근 권한이 필요합니다(READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
                    }else{
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
                    }
                }
            }
        });
        iv_babyPhoto.setImageResource(R.drawable.default_user);

        if (isLoad != true) {
            storageReference = FirebaseStorage.getInstance().getReference("user/" + uid);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (getContext() != null) {
                        Realuri = uri.toString();
                        Glide.with(getContext()).asBitmap()
                                .load(uri.toString()).apply(new RequestOptions().placeholder(R.drawable.default_user).circleCrop().centerCrop()).into(iv_babyPhoto);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iv_babyPhoto.setImageResource(R.drawable.default_user);
                }
            });
            getString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isLoad) {
                    }
                    final Long temp = ((System.currentTimeMillis() - signUpLong)/86400000)+1;

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_name.setText(name);
                                tv_signUpDate.setText(String.valueOf(temp)+ "일 째");
                                tv_age.setText(String.valueOf(age) + "살");

                            }
                        });
                    }
                }
            }).start();

        }else{
            Long temp = ((System.currentTimeMillis() - signUpLong)/86400000)+1;

            tv_name.setText(name);
            tv_signUpDate.setText(String.valueOf(temp)+ "일 째");
            tv_age.setText(String.valueOf(age) + "살");
            if (getContext() != null) {
                Glide.with(getContext()).asBitmap()
                        .load(Realuri).apply(new RequestOptions().circleCrop().centerCrop().placeholder(R.drawable.default_user)).into(iv_babyPhoto);
            }
        }

        return v;
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case RECEIVE_PHOTO:
                    Intent intent1 = new Intent(getContext() , SelectPhotoActivity.class);
                    intent1.putExtra("photoUri", data.getData());
                    startActivityForResult(intent1, IMAGE_SAVE);
                    break;
                case IMAGE_SAVE:
                    storageReference = FirebaseStorage.getInstance().getReference("user/" + uid);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (getContext() != null) {
                                Realuri = uri.toString();
                                Glide.with(getContext()).asBitmap()
                                        .load(uri.toString()).apply(new RequestOptions().circleCrop().centerCrop()).into(iv_babyPhoto);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            iv_babyPhoto.setImageResource(R.drawable.default_user);
                        }
                    });
                    break;
            }
        }
    }

    public void getString() {
        uid = "id";

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid + "/userInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoBean = dataSnapshot.getValue(InfoBean.class);
                name = infoBean.getName();
                String[] year = infoBean.getBirth().split("-");
                int a = Integer.valueOf(year[0]);
                Date date = new Date();
                SimpleDateFormat day2 = new SimpleDateFormat("yyyy-MM-dd");
                String signUpDate = day2.format(date);
                String[] d = signUpDate.split("-");
                int b = Integer.valueOf((d[0]));
                age = b - a + 1;
                signUpLong = infoBean.getDay();
                isLoad = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int grantResults[]){
        switch (requestCode){
            case 300:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(getContext(), "저장공간 접근 권한 거부함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}