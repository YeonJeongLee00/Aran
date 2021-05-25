package com.hackathon.aran2.Solution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hackathon.aran2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SolutionActivity extends AppCompatActivity implements  View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button case1, case2, case3, case4;
    String now;
    private Typeface mTypeface;
    SolutionsAdapter  solution1Adapter, solution2Adapter, solution3Adapter, solution4Adapter;
    ArrayList<SolutionItem> SolutionItems1,SolutionItems2,SolutionItems3,SolutionItems4;

    int nowId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.solutionAct_SolList);
        layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        SolutionItems1 = new ArrayList<>();
        SolutionItems2 = new ArrayList<>();
        SolutionItems3 = new ArrayList<>();
        SolutionItems4 = new ArrayList<>();
        now = "case1";
        nowId = R.id.solutionAct_case1;
        solution1Adapter = new SolutionsAdapter("case1", SolutionItems1);
        solution2Adapter = new SolutionsAdapter("case2", SolutionItems2);
        solution3Adapter = new SolutionsAdapter("case3", SolutionItems3);
        solution4Adapter = new SolutionsAdapter("case4", SolutionItems4);
        case1 = findViewById(R.id.solutionAct_case1);
        case2 = findViewById(R.id.solutionAct_case2);
        case3 = findViewById(R.id.solutionAct_case3);
        case4 = findViewById(R.id.solutionAct_case4);
        case1.setOnClickListener(this);
        case2.setOnClickListener(this);
        case3.setOnClickListener(this);
        case4.setOnClickListener(this);
        recyclerView.setAdapter(solution1Adapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("solution/case1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    SolutionItem solutionItem = Snapshot.getValue(SolutionItem.class);
                    SolutionItems1.add(solutionItem);
                }
                solution1Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.solutionAct_case1:
                if(!now.equals("case1")){
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(solution1Adapter);
                    case1.setBackgroundResource(R.color.colorHoneydew);
                    findViewById(nowId).setBackgroundResource(R.color.colorLavenderblush);
                }

                nowId = R.id.solutionAct_case1;
                now ="case1";
                break;
            case R.id.solutionAct_case2:
                if(!now.equals("case2")){
                    if(SolutionItems2.size()==0){
                        loadData("case2");
                    }
                    case2.setBackgroundResource(R.color.colorHoneydew);
                    findViewById(nowId).setBackgroundResource(R.color.colorLavenderblush);
                    now ="case2";
                    nowId = R.id.solutionAct_case2;
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(solution2Adapter);
                }

                break;
            case R.id.solutionAct_case3:
                if(!now.equals("case3")){
                    if(SolutionItems3.size()==0){
                        loadData("case3");
                    }
                    case3.setBackgroundResource(R.color.colorHoneydew);
                    findViewById(nowId).setBackgroundResource(R.color.colorLavenderblush);
                    now ="case3";
                    nowId = R.id.solutionAct_case3;
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(solution3Adapter);
                }

                break;
            case R.id.solutionAct_case4:
                if(!now.equals("case4")){
                    if(SolutionItems4.size()==0){
                        loadData("case4");
                    }
                    case4.setBackgroundResource(R.color.colorHoneydew);
                    findViewById(nowId).setBackgroundResource(R.color.colorLavenderblush);
                    nowId = R.id.solutionAct_case4;
                    now ="case4";
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(solution4Adapter);
                }

                break;
        }
    }

    public String getUid(){
        String id = "id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return id;
    }
    public void loadData(String caseString){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("solution/"+caseString);

        if(caseString.equals("case2")) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                        SolutionItem solutionItem = Snapshot.getValue(SolutionItem.class);
                        SolutionItems2.add(solutionItem);
                    }
                    solution2Adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(caseString.equals("case3")){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                        SolutionItem solutionItem = Snapshot.getValue(SolutionItem.class);
                        SolutionItems3.add(solutionItem);
                    }
                    solution3Adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if (caseString.equals("case4")){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                        SolutionItem solutionItem = Snapshot.getValue(SolutionItem.class);
                        SolutionItems4.add(solutionItem);
                    }
                    solution4Adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//        }else{
//            DatabaseReference heartReference = FirebaseDatabase.getInstance().getReference(getUid()+"/loves");
//            heartReference.orderByChild("date").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    HeartItem heartItem = dataSnapshot.getValue(HeartItem.class);
//                    HeartItems.add(heartItem);
//                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(heartItem.contentPosi+"/"+heartItem.second);
//                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            HeartSolutions.add(solutionItem);
//                            loves.add(solutionItem.contentTitle);
//                            heartAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    HeartItem heartItem = dataSnapshot.getValue(HeartItem.class);
//                    int index = loves.indexOf(heartItem.contentPosi);
//                    if(index >=0&&!recyclerView.getAdapter().equals(heartAdapter)){
//                        HeartItems.remove(index);
//                        loves.remove(index);
//                        HeartSolutions.remove(index);
//                        heartAdapter.notifyItemRemoved(index);
//                    }
//                    HeartItems.remove(heartItem);
//                    HeartSolutions.remove(index);
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }
    }

}
