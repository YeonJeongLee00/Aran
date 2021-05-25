package com.hackathon.aran2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.aran2.Diary.DiaryShowFragment;
import com.hackathon.aran2.LearnEmotion.LearnFragment;
import com.hackathon.aran2.Login.LoginActivity;
import com.hackathon.aran2.R;
import com.hackathon.aran2.Record.RecordFragment;
import com.hackathon.aran2.Solution.LoveFragment;
import com.hackathon.aran2.Solution.LovesSolutionActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    FrameLayout frameLayout;
    DiaryShowFragment diaryShowFragment;
    FragmentManager fm;
    boolean isLoad;
    LearnFragment learnFragment;
    RecordFragment recordFragment;
    FragmentTransaction tran;
    MainFragment mainFragment;
    String uid;
    Button logOutButton;
    LoveFragment loveFragment;
    private SharedPreferences appData;
    ListView listView = null;
    private Typeface mTypeface;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageButton menu;

    private DrawerLayout drawerLayout;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //로그인이 안되어있으면 로그인 화면으로 이동.

        uid = user.getUid();

        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        frameLayout = findViewById(R.id.main_frameLayout);
        loveFragment = new LoveFragment();
        diaryShowFragment = new DiaryShowFragment();
        learnFragment = new LearnFragment();
        mainFragment = new MainFragment();
        recordFragment = new RecordFragment();

        menu = (ImageButton) findViewById(R.id.menu_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer_menuList);

        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        tran.replace(R.id.main_frameLayout, mainFragment);
        tran.commit();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        tran = fm.beginTransaction();
                        tran.replace(R.id.main_frameLayout, mainFragment);
                        menu.setVisibility(View.VISIBLE);
                        tran.commit();
                        break;
                    case 1:
                        menu.setVisibility(View.GONE);
                        tran = fm.beginTransaction();
                        tran.replace(R.id.main_frameLayout, learnFragment);
                        tran.commit();
                        break;
                    case 2:
                        menu.setVisibility(View.GONE);
                        tran = fm.beginTransaction();
                        tran.replace(R.id.main_frameLayout, diaryShowFragment);
                        tran.commit();
                        break;
                    default:
                        menu.setVisibility(View.GONE);
                        tran = fm.beginTransaction();
                        tran.replace(R.id.main_frameLayout, recordFragment);
                        tran.commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String Email = user.getEmail();
        final String[] items = {Email,  "솔루션 책갈피", "로그아웃", "비밀번호 변경", "회원탈퇴"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        listView = (ListView) findViewById(R.id.drawer_menuList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ConstraintLayout contentView = (ConstraintLayout) findViewById(R.id.drawer_content);

                switch (position) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        startActivitySolution();
                        //솔루션
                        break;
                    }
                    case 2: {
                        createLogOutPopUp();
                        break;
                    }
                    case 3: {
                        createUpdatePopUp();
                        break;
                    }
                    case 4: {
                        createDeletePopUp();
                        break;
                    }

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }
    private void startActivitySolution(){
        Intent intent = new Intent(this, LovesSolutionActivity.class);
        startActivity(intent);
    }
    private void deleteLogInData() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SAVE_LOGIN_DATA", false);
        editor.putString("Email", null);
        editor.putString("Password", null);

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    private void updatePassword(String newPW) {
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPW).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "비밀번호 변경이 완료되었습니다!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "다시 로그인 해주세요!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "비밀번호 변경이 실패하였습니다!", Toast.LENGTH_LONG).show();
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

    private void deleteUserInfo() {
        storageDelete();
        databaseDelete();
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "회원탈퇴를 실패하였습니다!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void databaseDelete() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid);
        databaseReference.removeValue();
    }

    private void storageDelete() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference("user").child(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
             }
        });
    }

    private void userReAuthenticate() {
    }

    private void createLogOutPopUp(){
        AlertDialog.Builder alt_LogOut = new AlertDialog.Builder(this);
        alt_LogOut.setMessage("정말 로그아웃 하시겠습니까 ?").setCancelable(
                false).setPositiveButton("아니요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).setNegativeButton("네", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteLogInData();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다!", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alert = alt_LogOut.create();
        alert.setTitle("아란");
        alert.setIcon(R.drawable.sad);
        alert.show();
    }

    private void createUpdatePopUp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("아란");
        alert.setIcon(R.drawable.sad);
        final EditText newPassword = new EditText(this);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alert.setView(newPassword);
        alert.setMessage("바꾸고 싶은 비밀번호를 입력하세요!").setCancelable(false)
                .setPositiveButton("변경 안할래요!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).setNegativeButton("비밀번호를 변경할래요!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("비밀번호를 변경하는 중입니다...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String temp = newPassword.getText().toString();
                if (temp.length() < 6) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "6자 이상으로 입력하셔야합니다!", Toast.LENGTH_LONG).show();
                    return ;
                }

                deleteLogInData();
                updatePassword(temp);
                FirebaseAuth.getInstance().signOut();

                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        alert.show();
    }

    private void createDeletePopUp(){
        AlertDialog.Builder alt_Delete = new AlertDialog.Builder(this);
        alt_Delete.setMessage("정말 회원탈퇴 하시겠습니까 ?").setCancelable(
                false).setPositiveButton("아니요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).setNegativeButton("네", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteLogInData();
                deleteUserInfo();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "정상적으로 회원탈퇴가 되었습니다!", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alert = alt_Delete.create();
        alert.setTitle("아란");
        alert.setIcon(R.drawable.sad);
        alert.show();
    }
}