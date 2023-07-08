package com.capston.eduguide.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.R;
import com.capston.eduguide.post.FeedViewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserScrapActivity extends AppCompatActivity {
    ArrayList<FeedViewItem> items;
    UserScrapAdapter scrapAdapter;
    private RecyclerView scrapRecyclerView;
    private String userEmail; // userEmail 추가
    private String feedId;
    String bookmarkKey;
    //ArrayList<String> bookmarkList; //bookmarkList 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_scrap);

        // userEmail 값 받기
        userEmail = getIntent().getStringExtra("userEmail");
        bookmarkKey = userEmail.substring(0,userEmail.lastIndexOf('.'));

        // 스크랩 관련 뷰 초기화
        scrapRecyclerView = findViewById(R.id.my_scraps);
        items = new ArrayList<>();
        scrapAdapter = new UserScrapAdapter(this, items, userEmail);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        scrapRecyclerView.setLayoutManager(gridLayoutManager);

        // Firebase에서 스크랩 데이터 가져오기 및 설정
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        databaseReference.child("post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                databaseReference.child("bookmark").child(bookmarkKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> bookmarkList = new ArrayList<>();
                        for (DataSnapshot bookmarkSnapshot : snapshot.getChildren()) {
                            String feedId = bookmarkSnapshot.getKey();
                            bookmarkList.add(feedId);
                        }

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            FeedViewItem item = postSnapshot.getValue(FeedViewItem.class);
                            Log.d("////////post", item.getFeedId());

                            if (bookmarkList.contains(item.getFeedId())) {
                                items.add(item);
                            }
                        }
                        scrapAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 에러 처리
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });


        scrapRecyclerView.setAdapter(scrapAdapter); // 어댑터 설정 추가
    }
}
