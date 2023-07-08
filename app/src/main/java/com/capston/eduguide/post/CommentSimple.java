package com.capston.eduguide.post;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.capston.eduguide.Frag1Feed;
import com.capston.eduguide.Frag4Notice;
import com.capston.eduguide.MainActivity;
import com.capston.eduguide.R;
import com.capston.eduguide.guideTool.GuideFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CommentSimple extends Fragment {

    /*public static Comment newInstance(){
        return new Comment();
    }*/
    private TextView main;
    private TextView tag;
    private TextView username;
    LinearLayout commentInput;
    private ViewPager vp;
    private EditText comm;
    private Integer gradeInt;
    private ImageView userImage;
    private String userName;
    private String userEmail;
    private Integer userGrade;
    private ImageView feedUserImage;
    private String feedUserName;
    private String fId;

    private String title;

    FeedViewItem item;
    CommentSimpleAdapter adapter;
    BannerPagerAdapter bpa;

    ArrayList<CommentItem> comments = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference DatabaseReference = firebaseDatabase.getReference("");


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 뒤로가기 버튼을 누르면 메인피드로 화면 전환. 전환될 때 새로운 피드 객체를 생성하므로 번들로 userName과 grade 전달.
                Bundle bundle = new Bundle();
                bundle.putString("userEmail",userEmail);
                Frag1Feed feed = new Frag1Feed();
                feed.setArguments(bundle);
                ((MainActivity) requireActivity()).replaceFragment(feed);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag1_detail, container, false);

        main = view.findViewById(R.id.commentMain);
        tag = view.findViewById(R.id.tag);
        username = view.findViewById(R.id.userName);
        comm = view.findViewById(R.id.commentDetail);
        feedUserImage = view.findViewById(R.id.feedUserImage);
        userImage = view.findViewById(R.id.userImage);
        Button delete = (Button)view.findViewById(R.id.back);
        Button input = (Button) view.findViewById(R.id.commentInput);

        //FeedViewAdapter에서 번들로 여러가지 값을 받아오고 저장.
        Bundle bundle = getArguments();
        if (getArguments() != null)
        {
            userName = bundle.getString("userName");
            userEmail = bundle.getString("userEmail");
            String mainStr = bundle.getString("main_text");
            String tagStr = bundle.getString("tag_text");
            String userName = bundle.getString("feedUser_name");
            feedUserName = bundle.getString("feedUser_name");
            title = bundle.getString("title_text");
            main.setText(mainStr);
            tag.setText(tagStr);
            username.setText(userName);
            gradeInt =  bundle.getInt("feedUser_grade");
            feedUserImage.setImageResource(grade(gradeInt));
            fId = bundle.getString("feedId");
            userGrade = bundle.getInt("userGrade");
            userImage.setImageResource(grade(userGrade));
        }

        /*DatabaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    HashMap<String, Object> user = (HashMap<String, Object>)userSnapshot.getValue();
                    if(userEmail!=null) {
                        if (userEmail.equals((String) user.get("email"))) {
                            userName = (String)user.get("name");
                            userGrade = Integer.parseInt((String) user.get("grade"));
                            userImage.setImageResource(grade(userGrade));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        //번들로 받아온 fId를 통해 오버라이딩한 생성자로 가이드객체 생성.
        vp = (ViewPager) view.findViewById(R.id.vp);
        bpa = new BannerPagerAdapter(getChildFragmentManager(),fId);
        vp.setAdapter(bpa);
        vp.setCurrentItem(0);

        //fId에 해당하는 댓글 전체 불러오기
        ValueEventListener mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CommentItem comm = snapshot.getValue(CommentItem.class);
                    comments.add(comm);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        DatabaseReference.child("comment").child(fId).addValueEventListener(mListener);      //댓글 불러오기

        // 댓글 리사이클러뷰 참조
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 댓글 리사이클러뷰에 Adapter 객체 지정
        adapter = new CommentSimpleAdapter(getContext(),comments,getChildFragmentManager());
        adapter.setUserName(userName);
        adapter.setFeedId(fId);
        recyclerView.setAdapter(adapter);

        //유저 데이터가 존재하고 이름이 같을때만 삭제 버튼 활성화
        if(userName!=null){
            Log.d("//////////////", userName+"  "+feedUserName);
            if(userName.equals(feedUserName)){
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference().child("post").child(fId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("guide").child(fId).removeValue();
                                database.getReference().child("comment").child(fId).removeValue();
                                database.getReference().child("bookmark").child(userEmail.substring(0,userEmail.lastIndexOf("."))).child(fId).removeValue();
                                database.getReference().child("like").child(userName).child(fId).removeValue();
                                Bundle bundle = new Bundle();
                                bundle.putString("userEmail",userEmail);
                                Frag1Feed feed = new Frag1Feed();
                                feed.setArguments(bundle);
                                ((MainActivity) requireActivity()).replaceFragment(feed);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) { Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show(); }
                        });
                    }
                });
            }
            else{ delete.setVisibility(View.GONE); }
        }
        else{
            Log.d("//////////////", String.valueOf(userName)+"  "+feedUserName);
            delete.setVisibility(View.GONE);
            input.setVisibility(View.GONE);
            comm.setClickable(false);
        }

        input.setOnClickListener(new View.OnClickListener() {                      //댓글 입력시 이벤트
            @Override
            public void onClick(View v) {
                String comment = comm.getText().toString();
                String userName = bundle.getString("userName");;             //어댑터로 받아온 유저네임 등록
                comm.setText("");
                adapter.addComment(comments,userName,comment);
                adapter.notifyItemInserted(comments.size());
                recyclerView.setAdapter(adapter);

                //파이어베이스에 데이터 입력
                DatabaseReference.child("comment").child(fId).setValue(comments);

                //댓글 입력시 notice 데이터 저장
                if(feedUserName!=userName) {
                    DatabaseReference noticeRef = DatabaseReference.child("notice");
                    DatabaseReference feedUserNameRef = noticeRef.child(feedUserName);
                    DatabaseReference titleRef = feedUserNameRef.push();

                    titleRef.setValue(title);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(comments.size()-1);
                    }
                }, 200);


                //recyclerView.scrollToPosition(comments.size()-1);
            }
        });
        return view;
    }
    private class BannerPagerAdapter extends FragmentPagerAdapter {
        GuideFragment guide;
        String feedId;

        public BannerPagerAdapter(FragmentManager fm,String feedId){
            super(fm);
            this.feedId = feedId;
            //if(!MainActivity.getCurrentMenu().equals("posting"))
            //getItem(0);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(!(feedId.equals(""))){ guide = new GuideFragment(feedId); }
            return guide;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private int grade(Integer gradeInt) {
        if(gradeInt == 0)
            return R.drawable.seed;
        else if(gradeInt == 1)
            return R.drawable.sprout;
        else if(gradeInt == 2)
            return R.drawable.seedling;
        else if(gradeInt == 3)
            return R.drawable.tree;
        else if(gradeInt == 5)
            return R.drawable.grade1;
        else
            return R.drawable.grade1;
    }


}