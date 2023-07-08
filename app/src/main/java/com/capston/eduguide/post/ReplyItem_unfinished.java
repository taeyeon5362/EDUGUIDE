package com.capston.eduguide.post;

import android.graphics.drawable.Drawable;

public class ReplyItem_unfinished {
    private Drawable userIcon;
    private String replyStr ;
    private String username;

    public ReplyItem_unfinished(Drawable userIcon, String replyStr, String username){
        this.userIcon = userIcon;
        this.replyStr = replyStr;
        this.username = username;
    }

    public void setReplyUserIcon(Drawable icon) {
        userIcon = icon;
    }
    public void setReplyStr(String reply) {
        replyStr = reply ;
    }
    public void setReplyUsername(String name) { username = name ; }

    public Drawable getReplyUserIcon() {
        return this.userIcon ;
    }
    public String getReplyStr() {
        return this.replyStr ;
    }
    public String getReplyUsername() { return this.username ; }

}
