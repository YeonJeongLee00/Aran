package com.hackathon.aran2.Solution;

public class RealSolutionItem {
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String contents;
    String date;
    String imageUri;
    String title;
    public RealSolutionItem(){

    }

    public RealSolutionItem(String contents, String date, String imageUri, String title){
        this.contents = contents;
        this.date = date;
        this.imageUri = imageUri;
        this.title =title;
    }
}
