package com.capston.eduguide.post;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.capston.eduguide.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.capston.eduguide.guideTool.GuideFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FeedViewItem> feedViewItemList = new ArrayList<FeedViewItem>() ;
    private FragmentManager fm;
    private int position;

    private ValueEventListener mListener;
    private String userName;
    private Integer userGrade;
    private Integer feedUserGrade;
    private String userEmail;

    // ListViewAdapter의 생성자
    public FeedViewAdapter(FragmentManager fm, Context context){
        this.context = context;
        this.fm = fm;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView titleText;
        public TextView tagText;
        public ImageView userImage;
        public Button like;
        public TextView like_count;
        public Button bookmark;
        public TextView bookmark_count;
        public ViewPager vp;
        public FeedViewItem.BannerPagerAdapter bpa;



        ViewHolder(View itemView){
            super(itemView);
            // 뷰 객체에 대한 참조.
            like = itemView.findViewById(R.id.like_button);
            like_count = itemView.findViewById(R.id.like_count);
            bookmark = itemView.findViewById(R.id.bookmark_button);
            bookmark_count = itemView.findViewById(R.id.bookmark_count);
            username = itemView.findViewById(R.id.userName);
            titleText = itemView.findViewById(R.id.guideTitle);
            tagText = itemView.findViewById(R.id.tag);
            userImage = itemView.findViewById(R.id.feedUserImage);
            vp = (ViewPager) itemView.findViewById(R.id.vp);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    String pos = Integer.toString(getAdapterPosition());
                    FeedViewItem item = feedViewItemList.get(Integer.parseInt(pos));
                    if(v.isSelected()){
                        int count = Integer.parseInt(like_count.getText().toString());
                        count += 1;
                        like_count.setText(Integer.toString(count));
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference();
                        databaseReference.child("post").child(item.getFeedId()).child("like_count").setValue(count);
                        databaseReference.child("like").child(userName).child(item.getFeedId()).child("postId").setValue(pos);
                    }
                    else{
                        int count = Integer.parseInt(like_count.getText().toString());
                        if(count != 0){
                            count -= 1;
                            like_count.setText(Integer.toString(count));
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("post").child(item.getFeedId()).child("like_count").setValue(count);
                            databaseReference.child("like").child(userName).child(item.getFeedId()).removeValue();

                        }
                    }
                }
            });

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    String pos = Integer.toString(getAdapterPosition());
                    FeedViewItem item = feedViewItemList.get(Integer.parseInt(pos));
                    if(v.isSelected()){
                        int count = Integer.parseInt(bookmark_count.getText().toString());
                        count += 1;
                        bookmark_count.setText(Integer.toString(count));
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference();
                        databaseReference.child("post").child(item.getFeedId()).child("bookmark_count").setValue(count);
                        databaseReference.child("bookmark").child(userEmail.substring(0,userEmail.lastIndexOf("."))).child(item.getFeedId()).child("postId").setValue(pos);
                    }
                    else{
                        int count = Integer.parseInt(bookmark_count.getText().toString());
                        if(count != 0){
                            count -= 1;
                            bookmark_count.setText(Integer.toString(count));
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("post").child(item.getFeedId()).child("bookmark_count").setValue(count);
                            databaseReference.child("bookmark").child(userEmail.substring(0,userEmail.lastIndexOf("."))).child(item.getFeedId()).removeValue();
                        }
                    }
                }
            });

            //제목부분 클릭시 상세화면으로 넘어감. 넘어갈때 여러가지 값을 번들로 넘겨줌
            titleText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        FeedViewItem item = feedViewItemList.get(pos);
                        String titleStr = item.getTitle();
                        String textStr = item.getText();
                        String tagStr = item.getTag();
                        String usernameStr = item.getUserName();
                        Integer grade = item.getGrade();
                        String feedId = item.getFeedId();

                        // TODO : use item data.
                        Bundle bundle = new Bundle();
                        //bundle.putInt("position",pos);
                        bundle.putString("title_text",titleStr);
                        bundle.putString("main_text",textStr);
                        bundle.putString("tag_text",tagStr);
                        bundle.putString("feedUser_name",usernameStr);
                        bundle.putInt("feedUser_grade",grade);
                        bundle.putString("userName",userName);
                        bundle.putString("feedId",feedId);
                        bundle.putInt("userGrade",userGrade);
                        bundle.putString("userEmail",userEmail);

                        CommentSimple comment = new CommentSimple();
                        comment.setArguments(bundle);

                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame,comment)
                                //.addToBackStack(null)
                                .commit();

                    }
                }
            });
        }

        //리사이클러뷰의 아이템이 바인드 될때 호출. 각 뷰에 아이템을 지정하고 아이템의 feedId로 가이드 생성자를 선언하여
        //데이터가 존재하는 가이드 객체 생성.
        public void setItem(FeedViewItem item){
            username.setText(item.getUserName());
            titleText.setText(item.getTitle());
            tagText.setText(item.getTag());
            like_count.setText(String.valueOf(item.getLike_count()));
            bookmark_count.setText(String.valueOf(item.getBookmark_count()));
            Glide
                    .with(context)
                    .load(item.getUserIcon())
                    .apply(new RequestOptions().override(50,50))
                    .into(userImage);
            if(userName != null) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference likeDatabaseReference = database.getReference("like");
                likeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (userName.equals(dataSnapshot.getKey())) {
                                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                                    if (item.getFeedId().equals(keySnapshot.getKey())) {
                                        if (!like.isSelected())
                                            like.setSelected(!like.isSelected());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference bookmarkDatabaseReference = database.getReference("bookmark");
                bookmarkDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d("///////////////",String.valueOf(userEmail));
                            if (userEmail.substring(0,userEmail.lastIndexOf(".")).equals(dataSnapshot.getKey())) {
                                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                                    if (item.getFeedId().equals(keySnapshot.getKey())) {
                                        if (!bookmark.isSelected())
                                            bookmark.setSelected(!bookmark.isSelected());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                bpa = new FeedViewItem.BannerPagerAdapter(getFm(),item.getFeedId());
                bpa = item.getViewPagerAdapter();
            }
        }

        //뷰페이저 세팅.
        public void setVp(int position,FeedViewItem.BannerPagerAdapter bpa){
            vp.setId(position);
            vp.setOffscreenPageLimit(1);
            vp.setAdapter(bpa);
        }
    }

    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.post_feedview_item, parent, false);
        FeedViewAdapter.ViewHolder vh = new FeedViewAdapter.ViewHolder(view);
        return vh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FeedViewItem item = feedViewItemList.get(position);
        holder.setItem(item);

        holder.setVp(position+1,holder.bpa);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position ;
    }
    @Override
    public int getItemCount() {
        return feedViewItemList.size();
    }

    public void setItems(ArrayList<FeedViewItem> items){
        feedViewItemList = items;
    }

    public void setUserName(String userName,Integer userGrade,String userEmail) {
        this.userName = userName;
        this.userGrade = userGrade;
        this.userEmail = userEmail;
    }
    //public FeedViewItem getItem(int position){ return feedViewItemList.get(position); }
    //public void setItem(int position, FeedViewItem item){ feedViewItemList.set(position, item);}
    //public int getPosition(){return position;}
    //public void setPosition(int position) {this.position = position;}
    public void setFeedUserGrade(Integer grade){
        this.feedUserGrade = grade;
    }

    public FragmentManager getFm() {
        return fm;
    }
}