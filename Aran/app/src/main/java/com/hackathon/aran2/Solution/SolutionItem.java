package com.hackathon.aran2.Solution;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SolutionItem {

    public Integer getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(Integer loveCount) {
        this.loveCount = loveCount;
    }

    public Map<String, Boolean> getIsLove() {
        return isLove;
    }

    public void setIsLove(Map<String, Boolean> isLove) {
        this.isLove = isLove;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentUri() {
        return contentUri;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }

    String contentUri;
    Integer loveCount;
    Map<String, Boolean> isLove = new HashMap<>();
    String contentTitle;

    public SolutionItem(){

    }

    public SolutionItem(String contentUri, String contentTitle){

    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("contentUri", contentUri);
        result.put("loveCount", loveCount);
        result.put("contentTitle", contentTitle);
        result.put("isLove", isLove);
        return result;
    }
}
