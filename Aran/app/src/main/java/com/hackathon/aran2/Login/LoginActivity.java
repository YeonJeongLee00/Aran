package com.hackathon.aran2.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.hackathon.aran2.Main.MainActivity;
import com.hackathon.aran2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private boolean saveLoginData;
    private SharedPreferences appData;
    private String Email;
    private String Password;
    private Typeface mTypeface;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signUpLink;

    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signUpLink = findViewById(R.id.link_signup);

        firebaseAuth = firebaseAuth.getInstance();

        if (saveLoginData) {
            _emailText.setText(Email);
            _passwordText.setText(Password);
            login();
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _passwordText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        _passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });

        _signUpLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the SignUp activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
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

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("로그인 하는 중입니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            save();
                            Toast.makeText(LoginActivity.this, "로그인 완료!", Toast.LENGTH_SHORT).show();
                            myStartActivity(MainActivity.class);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "등록된 회원 정보가 없습니다!", Toast.LENGTH_LONG).show();
                            _loginButton.setEnabled(true);
                            _emailText.setText("");
                            _passwordText.setText("");
                        }
                    }
                });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        if (email.isEmpty()) {
            _emailText.setError("이메일을 입력해주세요!");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("이메일 형식이 올바르지 않습니다!");
            valid = false;
        }
        else {
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

    private void save() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SAVE_LOGIN_DATA", true);
        editor.putString("Email", _emailText.getText().toString().trim());
        editor.putString("Password", _passwordText.getText().toString().trim());

        editor.commit();
    }

    private void load() {
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        Email = appData.getString("Email", "");
        Password = appData.getString("Password", "");
    }
}
