package com.capston.eduguide.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.R;

import java.util.ArrayList;

public class ReplyAdapter_unfinished extends RecyclerView.Adapter<ReplyAdapter_unfinished.ViewHolder> {

    private Context context;
    private ArrayList<ReplyItem_unfinished> replyItemList = new ArrayList<>();

    ReplyAdapter_unfinished(Context context, ArrayList<ReplyItem_unfinished> replyItemList){
        this.replyItemList = replyItemList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView replyUserName;
        public TextView replyText;
        public ImageView replyUserImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            replyUserName = itemView.findViewById(R.id.replyUserName);
            replyText = itemView.findViewById(R.id.reply);
            replyUserImage = itemView.findViewById(R.id.replyUserImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.reply_item, parent, false);
        ViewHolder rvh = new ViewHolder(view);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReplyItem_unfinished item = replyItemList.get(position);
        holder.replyText.setText(item.getReplyStr());
        holder.replyUserName.setText(item.getReplyUsername());
        holder.replyUserImage.setImageResource(R.drawable.grade1);
    }

    @Override
    public int getItemCount() {
        return replyItemList.size();
    }

    //아이템 추가 함수
    public void addReply(ArrayList<ReplyItem_unfinished> replyItemList, Drawable replyUserImage, String replyUserName, String reply){
        ReplyItem_unfinished item = new ReplyItem_unfinished(replyUserImage,reply,replyUserName);

        item.setReplyUsername(replyUserName);
        item.setReplyUserIcon(replyUserImage);
        item.setReplyStr(reply);

        replyItemList.add(item);
    }
}
