package com.capston.eduguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.capston.eduguide.guideTool.GuideFragment;
import com.capston.eduguide.post.FeedViewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Frag3Posting extends Fragment {

    private View view;
    private ViewPager vp;
    private String prepId="null";
    private String feedId = "";
    private String userName;
    private String userEmail;
    private Integer userGrade;
    private
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference userDatabaseReference = firebaseDatabase.getReference("users");
    DatabaseReference postDatabaseReference = firebaseDatabase.getReference("post");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.frag3_posting, container, false);

        //피드 아이템의 bpa 이용. 피드 아이디를 넘기지 않으므로 일반 가이드객체 생성해서 연결.
        vp = (ViewPager) view.findViewById(R.id.vp);
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(getChildFragmentManager());
        vp.setAdapter(bannerPagerAdapter);
        vp.setCurrentItem(0);

        Bundle Mainbundle = getArguments();

        AppCompatEditText postTitle = view.findViewById(R.id.postTitle); //제목
        AppCompatEditText postInfo = view.findViewById(R.id.postInfo); //내용
        AppCompatEditText postTag = view.findViewById(R.id.postTag); //태그

        //피드의 아이디를 받아오는 이벤트 리스너를 실시간 리스너로 변경(여러 사람이 한번에 등록시 중복 방지)
        postDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey() != null) {
                        prepId = snapshot.getKey();
                    }
                }
                if(!(prepId.equals("null")))
                    feedId = String.valueOf((Integer.parseInt(prepId)+1));
                else
                    feedId = "0";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Main에서 받아온 userEmail로 users 검색, 일치하는 데이터의 userName("name")와 uerGrade(grade)를 받아옴.
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    HashMap<String, Object> user = (HashMap<String, Object>)userSnapshot.getValue();
                    if(Mainbundle.getString("userEmail")!=null) {
                        userEmail = Mainbundle.getString("userEmail");
                        if (Mainbundle.getString("userEmail").equals((String) user.get("email"))) {
                            userName = (String)user.get("name");
                            userGrade = Integer.parseInt((String) user.get("grade"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //등록 버튼 이벤트리스너
        view.findViewById(R.id.postingSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //유저 아이디 로직 추가 필요
                Integer pWriterId = 0;

                String pTitle = String.valueOf(postTitle.getText()); //제목 받아오기
                String pInfo = String.valueOf(postInfo.getText()); //내용 받아오기
                String pTag = String.valueOf(postTag.getText()); //태그 받아오기

                FeedViewItem item = new FeedViewItem();

                item.setTitle(pTitle);
                item.setText(pInfo);
                item.setTag(pTag);
                if(userName!=null)
                    item.setUserName(userName);
                item.setGrade(userGrade);
                item.setBookmark_count(0);
                item.setLike_count(0);
                String fId = feedId;
                item.setFeedId(fId);

                GuideFragment guideAdapter = (GuideFragment) bannerPagerAdapter.getItem(vp.getCurrentItem());
                guideAdapter.regGuideData(fId);
                databaseReference.child("post").child(fId).setValue(item);

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userEmail",userEmail);
                    Frag1Feed feed = new Frag1Feed();
                    feed.setArguments(bundle);
                    activity.replaceFragment(feed);
                } // 등록 후 메인 피드로 전환. 전환할 때 유저네임 번들로 넘겨줌.
            }
        });

        return view;
    }

    private class BannerPagerAdapter extends FragmentPagerAdapter {

        GuideFragment guide = new GuideFragment();

        public BannerPagerAdapter(FragmentManager fm){
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return guide;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
