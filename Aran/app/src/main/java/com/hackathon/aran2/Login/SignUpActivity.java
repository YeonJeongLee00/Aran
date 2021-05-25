package com.hackathon.aran2.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    EditText _emailText;
    EditText _passwordText;
    EditText _passwordCheckText;
    Button _signUpButton;
    TextView _loginLink;
    FirebaseAuth firebaseAuth;
    private Typeface mTypeface;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _passwordCheckText = findViewById(R.id.input_passwordCheck);
        _signUpButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);


        firebaseAuth = FirebaseAuth.getInstance();

        _signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        _passwordCheckText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        _passwordCheckText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    signUp();
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

    public void signUp() {
        Log.d(TAG, "SignUp");

        if (!validate()) {
            onSignUpFailed();
            return;
        }

        _signUpButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("계정을 만들고 있습니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String passwordCheck = _passwordCheckText.getText().toString().trim();

        if (password.equals(passwordCheck)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "계정 생성을 완료하였습니다!", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                Intent intent = new Intent(SignUpActivity.this, UserInfoActivity.class);
                                startActivity(intent);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "계정이 정상적으로 만들어지지 못했습니다!", Toast.LENGTH_SHORT).show();
                                myStartActivity(SignUpActivity.class);
                                return;
                            }
                        }
                    });

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                        }
                    }, 3000);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSignUpFailed() {
        _signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("이메일을 입력해주세요!");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("이메일 형식이 올바르지 않습니다!");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("비밀번호를 입력해주세요!");
            valid = false;
        } else if (password.length() < 6) {
            _passwordText.setError("6글자 이상으로 입력해주세요!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
