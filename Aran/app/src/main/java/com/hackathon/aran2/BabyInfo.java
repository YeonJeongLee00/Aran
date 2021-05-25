package com.hackathon.aran2;

public class BabyInfo {
    String name;
    String date;
    String imageUri;

    public BabyInfo(){

    }

    public BabyInfo(String name, String date, String imageUri){
        this.name = name;
        this.date = date;
        this.imageUri = imageUri;
    }

    public String getDate() {
        return date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }
}
