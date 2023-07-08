package com.capston.eduguide.post;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.Frag1Feed;
import com.capston.eduguide.MainActivity;
import com.capston.eduguide.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentSimpleAdapter extends RecyclerView.Adapter<CommentSimpleAdapter.ViewHolder> {

    private Context context;
    public ArrayList<CommentItem> commentItemList;
    private FragmentManager fm;
    private String userName;
    private String feedId;
    private String cId;

    public interface OnItemClickEventListener { void onItemClick(int a_position);}

    private OnItemClickEventListener mItemClickListener = new OnItemClickEventListener() {
        @Override
        public void onItemClick(int a_position) {
            notifyItemChanged(mCheckedPosition, null);
            mCheckedPosition = a_position;
            notifyItemChanged(a_position, null);
        }
    };

    private int mCheckedPosition = -1;

    //CommentAdapter의 생성자
    public CommentSimpleAdapter(Context context, ArrayList<CommentItem> commentItemList, FragmentManager fm) {
        this.context = context;
        this.commentItemList = commentItemList;
        this.fm = fm;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView commentText;
        public ImageView userImage;
        public ImageView deleteComment;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference DatabaseReference = database.getReference("users");

        //클릭리스너는 뷰홀더에서 작성
        public ViewHolder(@NonNull View itemView, final OnItemClickEventListener a_itemClickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.commentUserName);
            commentText = itemView.findViewById(R.id.comment);
            userImage = itemView.findViewById(R.id.commentUserImage);
            deleteComment = itemView.findViewById(R.id.deleteComment);

            //댓글 삭제 이벤트. 클릭한 아이템의 위치를 받아와 위치에 해당하는 댓글 아이템 받아옴.
            //받아온 댓글의 comment를 비교해서 일치하면 키값(댓글id)를 받고, 받은 키값으로 comment의 아이템 제거.
            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pos = Integer.toString(getAdapterPosition());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    CommentItem item= commentItemList.get(Integer.parseInt(pos));
                    cId = "";
                    database.getReference("comment").child(feedId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
                                if (value.get("comment").equals(item.getComment())) {
                                    cId = (String) dataSnapshot.getKey();
                                    database.getReference("comment").child(feedId).child(cId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            notifyItemRemoved(Integer.parseInt(cId));
                                            //notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        public void setItem(CommentItem item){
            username.setText(item.getUsername());
            commentText.setText(item.getComment());
            String commUserName = item.getUsername();
            //유저 이름으로 파이어베이스에서 유저 등급 받아오기. 받아온 등급으로 유저의 뱃지 표시
            ValueEventListener mListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        HashMap<String, String> value = (HashMap<String, String>)snapshot.getValue();
                        if(commUserName.equals(value.get("name"))){
                            Integer userGrade = Integer.valueOf(value.get("grade"));
                            userImage.setImageResource(grade(userGrade));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            DatabaseReference.addValueEventListener(mListener);
        }
    }

    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.post_comment_item, parent, false);
        CommentSimpleAdapter.ViewHolder cvh = new CommentSimpleAdapter.ViewHolder(view,mItemClickListener);

        return cvh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시. 댓글의 네임과 유저네임이 일치하지 않으면 삭제버튼 비활성화
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommentItem item = commentItemList.get(position);

        final int color;
        if (holder.getAdapterPosition() == mCheckedPosition) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_200);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent);
        }
        holder.itemView.setBackgroundColor(color);

        holder.setItem(item);
        if(userName == null){
            holder.deleteComment.setVisibility(View.GONE);
        } else if (!(userName.equals(item.getUsername()))) {
            holder.deleteComment.setVisibility(View.GONE);
        }
    }



    //지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemCount() { return commentItemList.size(); }

    // 아이템 데이터 추가를 위한 함수
    public void addComment(ArrayList<CommentItem> commentItemList, String userName, String comment){
        CommentItem item = new CommentItem(comment,userName);

        item.setUsername(userName);
        item.setComment(comment);

        commentItemList.add(item);
    }

    public CommentItem getSelected(){
        if (mCheckedPosition > -1) {
            return commentItemList.get(mCheckedPosition);
        }
        return null;
    }

    public void setUserName(String userName) { this.userName = userName; }
    public void setFeedId(String feedId) { this.feedId = feedId; }

    public int getCheckedPosition() {
        return mCheckedPosition;
    }

    public void clearSelected() {
        mCheckedPosition = -1;
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
        else
            return R.drawable.grade1;
    }
}