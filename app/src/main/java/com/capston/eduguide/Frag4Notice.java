package com.capston.eduguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.notice.NoticeAdapter;
import com.capston.eduguide.notice.NoticeItem;
import com.capston.eduguide.post.CommentSimple;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Frag4Notice extends Fragment {

    public static Frag4Notice frag4Notice = new Frag4Notice();

    private View view;

    private RecyclerView recyclerView;

    private NoticeAdapter noticeAdapter;

    private List<String> noticeList;

    private DatabaseReference databaseReference;


    String userEmail;
    String userName;
    Integer userGrade;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4_notice, container, false);

        recyclerView = view.findViewById(R.id.notice_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(noticeAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Bundle bundle = getArguments();
        if (userEmail == null){
            if(bundle.getString("userEmail")!= null) {
                userEmail = bundle.getString("userEmail");
                callUserName();
            }
        }
        databaseReference.child("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String feedUserId = dataSnapshot.getKey();
                    if (userName.equals(feedUserId)) {
                        for(DataSnapshot noticeSnapshot:dataSnapshot.getChildren()){
                            String title=noticeSnapshot.getValue(String.class);
                            noticeList.add(title);
                        }


                    }

                }

                recyclerView.setAdapter(noticeAdapter);
                noticeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    noticeAdapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, String title) {
            //post 노드에서 해당 제목과 일치하는 데이터 찾기
            DatabaseReference postRef = databaseReference.child("post");
            postRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //post의 필요한 데이터 가져오기
                        String postId = dataSnapshot.getKey();
                        String title = dataSnapshot.child("title").getValue(String.class);
                        String tag = dataSnapshot.child("tag").getValue(String.class);
                        String text = dataSnapshot.child("text").getValue(String.class);
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        Integer grade = dataSnapshot.child("grade").getValue(Integer.class);

                        Fragment fragment = new CommentSimple();

                        Bundle bundle = new Bundle();
                        bundle.putString("feedId", postId);
                        bundle.putString("title_text", title);
                        bundle.putString("tag_text", tag);
                        bundle.putString("main_text", text);
                        bundle.putString("feedUser_name", userName);
                        bundle.putInt("feedUser_grade", grade);
                        bundle.putString("userEmail", userEmail);
                        bundle.putInt("userGrade", userGrade);

                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    });
        return view;
    }

    public void callUserName(){

        if(userEmail != null){
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HashMap<String, String> value = (HashMap<String, String>)dataSnapshot.getValue();
                        if(userEmail.equals(value.get("email"))){
                            userName = value.get("name");
                            userGrade = Integer.parseInt(value.get("grade"));

                        }
                    }
                    if(userName == null){
                        userName = "";

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            userName = "";

        }
    }
}