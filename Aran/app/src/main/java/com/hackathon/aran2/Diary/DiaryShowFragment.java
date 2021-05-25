package com.hackathon.aran2.Diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hackathon.aran2.R;

import java.util.ArrayList;

public class DiaryShowFragment extends Fragment {
    boolean temp = false;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button addBtn;
    DaysAdapter daysAdapter;
    ArrayList<DiaryContent> diaryContentArrayList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    String uid;
    TextView txtinfo;
    private Typeface typeface;
    View view;
    ChildEventListener childEventListener;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_diary_main, container, false);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(),
                    "BinggraeMelona.ttf");
        }
        setGlobalFont((ViewGroup) view);
        recyclerView = view.findViewById(R.id.diaryMain_recyclerView);
        uid="id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        databaseReference= firebaseDatabase.getReference(uid+"/diary");
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        addBtn = view.findViewById(R.id.diaryMain_addBtn);
        diaryContentArrayList.clear();

        daysAdapter = new DaysAdapter(getContext(), diaryContentArrayList, getActivity());
        recyclerView.setAdapter(daysAdapter);
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        txtinfo = view.findViewById(R.id.diaryMain_networkTxt);
        if(info==null){
            txtinfo.setVisibility(View.VISIBLE);
            txtinfo.setText("인터넷에 연결되지 않았습니다!");
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("인터넷에 연결되지 않았습니다!").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }else {
            txtinfo.setVisibility(View.VISIBLE);
            DiaryisEmpty();
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DaysAddActivity.class);
                    intent.putExtra("isModi", false);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_show, R.anim.slide_left_remove);

                }
            });
        }
        return view;
    }
    public void DiaryisEmpty(){
        if(diaryContentArrayList.size()==0){
            txtinfo.setText("일기가 없습니다! 추가하기 버튼을 눌러 기록해주세요");
            txtinfo.setVisibility(View.VISIBLE);
        }else{
            txtinfo.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        databaseReference.removeEventListener(childEventListener);
    }
    @Override
    public void onResume(){
        super.onResume();
        diaryContentArrayList.clear();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                txtinfo.setText("로딩중 . . .");
                DiaryContent diaryContent = dataSnapshot.getValue(DiaryContent.class);
                diaryContentArrayList.add(0, diaryContent);
                daysAdapter.notifyDataSetChanged();
                DiaryisEmpty();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DiaryContent diaryContent = dataSnapshot.getValue(DiaryContent.class);
                int order = diaryContentArrayList.indexOf(diaryContent);
                if(order>=0&& diaryContentArrayList.size()>order) {
                    diaryContentArrayList.remove(order);
                    diaryContentArrayList.add(order, diaryContent);
                    daysAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DiaryContent remove = dataSnapshot.getValue(DiaryContent.class);
                diaryContentArrayList.remove(remove);
                DiaryisEmpty();
                daysAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
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

}

