package com.capston.eduguide.search;

public class SearchItem{
    private String postId;
    private String title;
    private String tag;
    private String description;
    private String userId;

    private Integer grade;

    public SearchItem(String postId,String title,String tag,String description,String userId,Integer grade){
        this.postId=postId;
        this.title=title;
        this.tag=tag;
        this.description=description;
        this.userId=userId;
        this.grade=grade;
    }
    public String getPostId(){return postId;}
    public String getTitle(){
        return title;
    }
    public String getTag(){
        return tag;
    }
    public String getDescription(){return description;}
    public String getUserId(){return userId;}
    public Integer getGrade(){
        return grade;
    }
}