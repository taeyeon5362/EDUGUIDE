package com.capston.eduguide.post;

import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.capston.eduguide.MainActivity;
import com.capston.eduguide.guideTool.GuideFragment;

public class FeedViewItem {
    private String feedId;
    private Drawable userIcon;
    private String titleStr;
    private String textStr;
    private String tagStr;
    private String userName;
    private Integer like_count;
    private Integer bookmark_count;
    private Integer grade;
    private BannerPagerAdapter viewPagerAdapter;
    private String userEmail;


    public void setFeedId(String id) {
        feedId = id;
    }

    public void setUserIcon(Drawable icon) {
        userIcon = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setText(String text) {
        textStr = text;
    }
    public void setTag(String tag){ tagStr = tag; }
    public void setUserName(String name) {
        userName = name;
    }

    public void setLike_count(Integer count) {
        like_count = count;
    }

    public void setBookmark_count(Integer count) {
        bookmark_count = count;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setViewPagerAdapter(BannerPagerAdapter bpa) {
        this.viewPagerAdapter = bpa;
    }

    public String getFeedId() {
        return this.feedId;
    }

    public Drawable getUserIcon() {
        return this.userIcon;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getText() {
        return this.textStr;
    }
    public String getTag(){ return this.tagStr; }
    public String getUserName() { return this.userName ;}
    public Integer getLike_count() {
        return this.like_count;
    }

    public Integer getBookmark_count() {
        return this.bookmark_count;
    }

    public Integer getGrade() {
        return this.grade;
    }
    public BannerPagerAdapter getViewPagerAdapter() {
        return this.viewPagerAdapter;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public static class BannerPagerAdapter extends FragmentPagerAdapter {

        GuideFragment guide;
        String feedId;
        public Integer adapterId;

        //바인드 될 때마다 호출.getItem()을 임의로 호출해서 바인드 될 때마다 가이드 객체가 생성되도록 함.
        public BannerPagerAdapter(FragmentManager fm,String feedId) {
            super(fm);
            this.feedId = feedId;
            if(!MainActivity.getCurrentMenu().equals("posting"))
                getItem(0);
        }

        //뷰페이저가 노출될 때 호출하는 함수로, 노출될 때 한번만 호출된다. 호출될때 feedId가 존재하면
        //오버라이딩 생성자를 통해 데이터가 존재하는 가이드 객체 생성, 아니면 일반 객체 생성
        @Override
        public Fragment getItem(int position) {
            if(!(feedId.equals(""))){
                guide = new GuideFragment(feedId);
            }
            /*else if(feedId.equals("feed")){

            }
            else
                guide = new GuideFragment();*/
            return guide;
        }

        @Override
        public int getCount() {
            return 1;
        }

        public Integer getAdapterId(){
            return this.adapterId;
        }

        public void setAdapterId(int position){
            this.adapterId = position;
        }

    }
}