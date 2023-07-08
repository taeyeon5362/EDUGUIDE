package com.capston.eduguide.post;

import android.graphics.drawable.Drawable;

public class CommentItem {
    private String commentStr ;
    private String username;

    public CommentItem(){
    }

    public CommentItem(String commentStr, String username){
        this.commentStr = commentStr;
        this.username = username;
    }

    public void setComment(String comment) {
        commentStr = comment ;
    }
    public void setUsername(String name) { username = name ; }

    public String getComment() {
        return this.commentStr ;
    }
    public String getUsername() {
        return this.username;
    }

}