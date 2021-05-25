package com.hackathon.aran2.Solution;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.aran2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoveFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ArrayList<HeartItem> HeartItems;
    ArrayList<SolutionItem> HeartSolutions;
    ArrayList<String> loves;
    RecyclerView.LayoutManager layoutManager;
    LovesAdapter heartAdapter;
    private Typeface typeface;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loves, container, false);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(),
                    "BinggraeMelona.ttf");
        }
        setGlobalFont((ViewGroup) view);
        recyclerView = view.findViewById(R.id.loves_recyclerView);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        HeartItems = new ArrayList<>();
        loves = new ArrayList<>();
        HeartSolutions = new ArrayList<>();
        heartAdapter = new LovesAdapter(HeartSolutions, HeartItems);
        recyclerView.setAdapter(heartAdapter);
        DatabaseReference heartReference = FirebaseDatabase.getInstance().getReference(getUid()+"/loves");
        heartReference.orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HeartItem heartItem = dataSnapshot.getValue(HeartItem.class);
                HeartItems.add(heartItem);
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(heartItem.contentPosi+"/"+heartItem.second);
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SolutionItem solutionItem = dataSnapshot.getValue(SolutionItem.class);
                        HeartSolutions.add(solutionItem);
                        loves.add(solutionItem.contentTitle);
                        heartAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                HeartItem heartItem = dataSnapshot.getValue(HeartItem.class);
                int index = loves.indexOf(heartItem.key);
                if(index >=0){
                    HeartItems.remove(index);
                    loves.remove(index);
                    HeartSolutions.remove(index);
                    //                 recyclerView.removeViewAt(index);
                    heartAdapter.notifyItemRemoved(index);
                    //     heartAdapter.notifyItemRangeRemoved(index, HeartSolutions.size());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
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
    public String getUid(){
        String id = "id";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return id;
    }
}
