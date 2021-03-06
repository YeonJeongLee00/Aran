package com.hackathon.aran2.Record;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.aran2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecordFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ArrayList<String> emotionsList;
    DatabaseReference databaseReference;
    ArrayList<RecordContent> recordContentArrayList;
    RecordsAdapter recordsAdapter;
    RecyclerView.LayoutManager layoutManager;
    String uid;
    Spinner spinner;
    TextView tv_info;

    private Typeface typeface;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(),
                    "BinggraeMelona.ttf");
        }
        setGlobalFont((ViewGroup) view);
        recyclerView = view.findViewById(R.id.recordMain_recordRecycler);
        layoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        recordContentArrayList = new ArrayList<>();
        uid = "id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        databaseReference =  FirebaseDatabase.getInstance().getReference(uid+"/learnEmotion");
        tv_info = view.findViewById(R.id.recordMain_infoText);
        spinner = view.findViewById(R.id.recordMain_spinner);
        emotionsList = new ArrayList<>();
//?????? ????????? ?????? ???????????? ??????
        //?????? ????????? ????????? ????????? ?????? ?????? ?????????
        //?????????
        emotionsList.add("??????");
        emotionsList.add("??????");
        emotionsList.add("??????");
        emotionsList.add("??????");
        emotionsList.add("?????????");
        emotionsList.add("??????");
        emotionsList.add("????????????");
        emotionsList.add("??????");
        emotionsList.add("?????????");
        emotionsList.add("??????");
        emotionsList.add("?????????");
        emotionsList.add("?????????");

        emotionsList.add("?????????");

        SpinnerAdapter adapter = new SpinnerAdapter(emotionsList, getContext());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onEmotionSelect(emotionsList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recordsAdapter = new RecordsAdapter(recordContentArrayList);
        recyclerView.setAdapter(recordsAdapter);


        RecordIsEmpty(null);
//        childEventListener = new ChildEventListener(){
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(recordsAdapter!=null){
//                    RecordContent recordContent = dataSnapshot.getValue(RecordContent.class);
//                    recordContentArrayList.add(0, recordContent);
//                    RecordIsEmpty(null);
//                    recordsAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        databaseReference.addChildEventListener(childEventListener);
        return view;
    }

    public void RecordIsEmpty(String emotion){
        if(emotion==null){
            if(recordContentArrayList.size()==0){
                tv_info.setText("????????? ????????? ????????????!");
                tv_info.setVisibility(View.VISIBLE);
            }else{
                tv_info.setVisibility(View.GONE);
            }
        }else{
            if(recordContentArrayList.size()==0){
                String temp = "???????????? " + emotion + "??? ???????????? ???????????????!";
                tv_info.setText(temp);
                tv_info.setVisibility(View.VISIBLE);
            }else{
                tv_info.setVisibility(View.GONE);
            }
        }
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
    public void onEmotionSelect(final String emotion){
        if(emotion==null){
            Toast.makeText(getContext(), "????????????", Toast.LENGTH_LONG).show();
        }else{
//            recyclerView.setAdapter(null);
//            recordsAdapter = null;
            recordContentArrayList.clear();
            recordsAdapter.notifyDataSetChanged();
            if(emotion.equals("??????")){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tv_info.setText("????????? . . .");
                        for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                            RecordContent recordContent =Snapshot.getValue(RecordContent.class);
                            recordContentArrayList.add(recordContent);
                        }

                        recordsAdapter.notifyDataSetChanged();
                        RecordIsEmpty(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                RecordIsEmpty(emotion);
                databaseReference.orderByChild("emotion").equalTo(emotion).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tv_info.setText("????????? . . .");

                        for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                            RecordContent recordContent =Snapshot.getValue(RecordContent.class);
                            recordContentArrayList.add(recordContent);
                        }

                        recordsAdapter.notifyDataSetChanged();
                        RecordIsEmpty(emotion);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

}
