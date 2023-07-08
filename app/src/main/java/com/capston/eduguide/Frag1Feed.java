package com.capston.eduguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.capston.eduguide.post.FeedViewAdapter;
import com.capston.eduguide.post.FeedViewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Frag1Feed extends Fragment {
    FeedViewAdapter adapter;
    ArrayList<FeedViewItem> items = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private ValueEventListener mListener;
    RecyclerView recyclerView;
    String userName;
    String userEmail;
    Integer userGrade;

    Integer feedUserGrade;

    public static Frag1Feed newInstance(){
        return new Frag1Feed();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.frag1_feed, container, false);

        //번들로 Main으로부터 userEmail을 받거나 CommentSimple로부터 userName을 받아옴
        Bundle bundle = getArguments();
        if (userEmail == null){
            if(bundle.getString("userEmail")!= null)
                userEmail = bundle.getString("userEmail");
        }

        Log.d("유저 이메일 테스트",String.valueOf(userEmail));

        // 리스트 뷰 참조 및 Adapter 달기
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerGuide);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        database = FirebaseDatabase.getInstance();

        // 리사이클러뷰에 tAdapter 객체 지정
        adapter = new FeedViewAdapter(getChildFragmentManager(), getActivity());
        callUserName();
        callFeedList();
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    //화면이 전환되면 이벤트 리스너 제거
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(mListener);
    }

    //피드 아이템의 유저 이름으로 등급 검색, 검색된 등급을 피드의 등급에 저장하고 그걸로 뱃지 표시
    public void setUserIconForGrade(FeedViewItem item){

        if(item.getGrade()==0){
            item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.seed, null));
        }
        else if (item.getGrade()==1) {
            item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.sprout, null));
        }
        else if (item.getGrade()==2) {
            item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.seedling, null));
        }
        else if (item.getGrade()==3) {
            item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.tree, null));
        }
        else
            item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.grade1, null));

        String feedUserName = item.getUserName();
        feedUserGrade = 5;

        userDatabaseReference = database.getReference("users");
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HashMap<String, String> value = (HashMap<String, String>)dataSnapshot.getValue();
                    if(String.valueOf(feedUserName).equals(value.get("name"))){
                        feedUserGrade = Integer.parseInt(value.get("grade"));
                        item.setGrade(feedUserGrade);
                        break;
                    }
                    else if(String.valueOf(feedUserName).equals("null")){
                        feedUserGrade = 5;
                        item.setGrade(feedUserGrade);
                        break;
                    }
                }
                if(feedUserGrade==0){
                    item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.seed, null));
                }
                else if (feedUserGrade==1) {
                    item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.sprout, null));
                }
                else if (feedUserGrade==2) {
                    item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.seedling, null));
                }
                else if (feedUserGrade==3) {
                    item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.tree, null));
                }
                else if (feedUserGrade==5)
                    item.setUserIcon(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.grade1, null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //파이어베이스 post의 게시글 전체 불러오기. bpa를 오버라이딩한 생성자로 선언해서 선언시부터 데이터가 들어감.해당 bpa를 피드아이템의 bpa로 추가(set).
    //추가한 아이템의 리스트를 어댑터의 아이템리스트에 등록(setItems). 이후 어댑터 연결.
    public void callFeedList(){
        databaseReference = database.getReference("post");
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FeedViewItem item = snapshot.getValue(FeedViewItem.class);
                    FeedViewItem.BannerPagerAdapter bpa = new FeedViewItem.BannerPagerAdapter(getChildFragmentManager(), item.getFeedId());
                    item.setViewPagerAdapter(bpa);
                    setUserIconForGrade(item);
                    items.add(item);
                }
                adapter.setItems(items);
                //adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(mListener);
    }

    //번들로 받은 userEmail이 있으면 검색해서 userName에 등록. users의 name속성을 받아온다. 이후 받아온 userName을 어댑터에 등록.
    //userEmail이 아니라 userName을 직접 받아오면(CommentSimple로부터) 그냥 바로 등록하고 어댑터에도 바로 등록한다.
    public void callUserName(){
        if(userEmail != null){
            userDatabaseReference = database.getReference("users");
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HashMap<String, String> value = (HashMap<String, String>)dataSnapshot.getValue();
                        if(userEmail.equals(value.get("email"))){
                            userName = value.get("name");
                            userGrade = Integer.parseInt(value.get("grade"));
                            adapter.setUserName(userName,userGrade,userEmail);
                        }
                    }
                    if(userName == null){
                        userName = "";
                        adapter.setUserName(userName,5,userEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            userName = "";
            adapter.setUserName(userName,5,"");
        }
    }
}