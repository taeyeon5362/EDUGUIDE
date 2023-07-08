package com.capston.eduguide.guideTool;

public class GuideBoxItem {
    private String keyword; // 박스 키워드 (박스 전면에 표시)
    private String boxInfo; // 박스 설명 (박스 팝업 창에 표시)

    public GuideBoxItem() {
        // Default constructor required for Firebase
    }

    public GuideBoxItem(String keyword, String boxInfo) {
        this.keyword = keyword;
        this.boxInfo = boxInfo;
    }

    // Getter and setter methods

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(String boxInfo) {
        this.boxInfo = boxInfo;
    }
}
