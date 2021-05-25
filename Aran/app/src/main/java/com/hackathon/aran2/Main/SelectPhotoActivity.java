package com.hackathon.aran2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static java.security.AccessController.getContext;

public class SelectPhotoActivity extends AppCompatActivity {
    ImageView iv_photo;
    StorageReference storageReference;
    String uid;
    Button btn_save;
    Uri uri;
    private Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        Intent intent = getIntent();
        uri = intent.getParcelableExtra("photoUri");
        iv_photo = findViewById(R.id.select_photo_imageView);
        if (uri == null) {
            Intent outintent = new Intent();
            setResult(RESULT_CANCELED, outintent);
            finish();
        }
        String path = getRealPathFromURI(uri);
        Bitmap image = BitmapFactory.decodeFile(path);
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Bitmap rotateBitmap = exifOrientationToDegrees(image, exifOrientation);

            iv_photo.setImageBitmap(rotateBitmap);
        } catch (IOException e) {
            e.printStackTrace();

            iv_photo.setImageURI(uri);
        }

        uid = "id";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        btn_save = findViewById(R.id.select_photo_saveBtn);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = FirebaseStorage.getInstance().getReference("user/" + uid);
                UploadTask uploadTask = storageReference.putFile(uri);
                final ProgressDialog p = new ProgressDialog(SelectPhotoActivity.this);
                p.setTitle("사진 저장중...");
                p.setCancelable(false);
                p.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
                p.show();
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * ((double) taskSnapshot.getBytesTransferred() / (double) taskSnapshot.getTotalByteCount());
                        p.setProgress((int) progress);
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            p.dismiss();
                            p.dismiss();
                            if (getContext() != null) {
                                Toast.makeText(SelectPhotoActivity.this, "저장 성공", Toast.LENGTH_LONG).show();

                                Intent outintent = new Intent();
                                setResult(RESULT_OK, outintent);
                                finish();


                            }

                        } else {
                            p.dismiss();
                            if (getContext() != null) {
                                Toast.makeText(SelectPhotoActivity.this, "저장 실패", Toast.LENGTH_LONG).show();
                                onBackPressed();

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p.dismiss();
                        if (getContext() != null) {
                            Toast.makeText(SelectPhotoActivity.this, "저장 실패", Toast.LENGTH_LONG).show();

                            onBackPressed();
                        }
                    }
                });
            }
        });
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

        String[] proj = {MediaStore.Images.Media.DATA};

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
