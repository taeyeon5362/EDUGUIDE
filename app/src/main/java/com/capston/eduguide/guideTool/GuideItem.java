package com.capston.eduguide.guideTool;

import java.util.List;

public class GuideItem {
    private String postId; // 게시글 아이디
    private List<GuideBoxItem> guideBoxItems; // 가이드 박스 리스트

    public GuideItem(String postId, List<GuideBoxItem> guideBoxItemList) {
        this.postId = postId;
        this.guideBoxItems = guideBoxItemList;
    }

    // Getter and setter methods

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<GuideBoxItem> getGuideBoxList() {
        return guideBoxItems;
    }

    public void setGuideBoxList(List<GuideBoxItem> guideBoxItemList) {
        this.guideBoxItems = guideBoxItemList;
    }
}