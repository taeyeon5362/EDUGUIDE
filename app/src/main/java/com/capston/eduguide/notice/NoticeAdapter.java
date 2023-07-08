package com.capston.eduguide.notice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.R;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private static List<String> noticeList;
    private static OnItemClickListener onItemClickListener;

    public NoticeAdapter(List<String> noticeList){
        this.noticeList= noticeList;

    }
    public interface OnItemClickListener{
        void onItemClick(int position,String title);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        String notice = noticeList.get(position);
        holder.titleTextView.setText("'"+notice+"' 게시글에 새로운 댓글이 달렸습니다");
    }
    @Override
    public int getItemCount(){
        return noticeList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(onItemClickListener !=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            String title = noticeList.get(position);
                            onItemClickListener.onItemClick(position,title);
                        }
                    }
                }
            });
        }
    }
}