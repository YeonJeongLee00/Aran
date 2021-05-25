package com.hackathon.aran2.Login;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";

    EditText _nameText;
    Button _birthButton;
    Button _completeButton;
    Button _manButton;
    Button _womanButton;
    TextView _loginLink;
    Boolean manPressed = false;
    Boolean womanPressed = false;
    final String[] gender = new String[1];
    private DatePickerDialog.OnDateSetListener call;
    private Typeface mTypeface;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        _nameText = findViewById(R.id.input_name);
        _manButton = findViewById(R.id.manButton);
        _womanButton = findViewById(R.id.womanButton);
        _birthButton = findViewById(R.id.birthButton);
        _completeButton = findViewById(R.id.btn_complete);
        _loginLink = findViewById(R.id.link_login);

        this.InitializeListener();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        gender[0] = null;

        if (user == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else {
            for (UserInfo profile : user.getProviderData()) {
                String name = profile.getDisplayName();
                if (user == null) {
                    Intent intent = new Intent(this, UserInfoActivity.class);
                    startActivity(intent);
                }
            }
        }

        _completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUpdate();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        _manButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender[0] = "남자";
                manPressed = true;
                womanPressed = false;
            }
        });

        _womanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender[0] = "여자";
                manPressed = false;
                womanPressed = true;
            }
        });

        _birthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickHandler(_birthButton);
            }
        });

        _nameText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        _nameText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    profileUpdate();
                    return true;
                }
                return false;
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

    private void profileUpdate() {
        Log.d(TAG, "UserInfo");

        if (!validate()) {
            updateFailed();
            return;
        }

        _completeButton.setEnabled(false);

        final String name = _nameText.getText().toString().trim();
        final String birth = _birthButton.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("회원 정보를 등록하고 있습니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (name.length() > 0 && gender[0] != null && birth.length() > 5) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            final String email = user.getEmail();
            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference(uid).child("userInfo");

            Date date = new Date();
            SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
            final Long signUpDate = date.getTime();

            HashMap<String, Object> memberInfo = new HashMap<>();

            memberInfo.put("email", user.getEmail());
            memberInfo.put("name", name);
            memberInfo.put("gender", gender[0]);
            memberInfo.put("birth", birth);
            memberInfo.put("day", signUpDate);

            databaseReference.setValue(memberInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "회원 정보 등록을 완료하였습니다!", Toast.LENGTH_SHORT).show();
                    myStartActivity(LoginActivity.class);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "회원 정보 등록을 실패하였습니다!", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    myStartActivity(SignUpActivity.class);
                }
            });
        }

        else {
            progressDialog.dismiss();
            Toast.makeText(UserInfoActivity.this, "회원 정보를 입력해주세요!", Toast.LENGTH_SHORT).show();
            myStartActivity(UserInfoActivity.class);
        }
    }


    public void updateFailed() {
        _completeButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString().trim();
        String birth = _birthButton.getText().toString().trim();

        if (name.isEmpty()) {
            _nameText.setError("이름을 입력해주세요!");
            valid = false;
        }
        else {
            _nameText.setError(null);
        }

        if (manPressed && womanPressed) {
            valid = false;
        } else {
        }

        if (birth.isEmpty()) {
            valid = false;
        } else {
        }

        return valid;
    }

    public void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void InitializeListener() {
        call = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int temp = month + 1;
                String Month = Integer.toString(temp);

                if (temp < 10)
                    Month = "0" + Month;

                String Day = Integer.toString(dayOfMonth);
                if (dayOfMonth < 10)
                    Day = "0" + Day;
                _birthButton.setText(year + "-" + Month + "-" + Day);
            }
        };
    }

    public void OnClickHandler(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, call, 2000, 0, 1);
        dialog.show();
    }
}