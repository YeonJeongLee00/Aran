package com.hackathon.aran2.Solution;

public class HeartItem {
    public String getContentPosi() {
        return contentPosi;
    }

    public void setContentPosi(String contentPosi) {
        this.contentPosi = contentPosi;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String contentPosi;
    String second;
    Long date;
    String key;
    HeartItem(){}
    HeartItem(String contentPosi, Long date, String key, String second){
        this.contentPosi = contentPosi;
        this.date = date;
        this.second = second;
        this.key = key;

    }
}
