package com.hackathon.aran2.Diary;

import java.util.ArrayList;

public class DiaryContent {
    public String state;
    public String emotion;
    public String content;
    public String baby;
    public ArrayList<String> imageUri;
    public String day;
    public String id;
    public Integer emotionId;
    public Integer stateId;
    public DiaryContent(String id, String state, String day, String emotion, String content, String baby, ArrayList<String> imageUri, Integer stateId, Integer emotionId){
        this.id = id;
        this.day = day;
        this.state = state;
        this.baby = baby;
        this.emotion = emotion;
        this.content = content;
        this.imageUri = imageUri;
        this.emotionId = emotionId;
        this.stateId = stateId;
    }


    public DiaryContent(){

    }
    public String id(){return id;}
    public void setId(String id){this.id = id;}
    public String getState() {
        return state;
    }
    public Integer getEmotionId(){return this.emotionId;}
    public void setEmotionId(Integer emotionId){
        this.emotionId = emotionId;
    }
    public Integer getStateId(){return this.stateId;}
    public void setStateId(Integer StateId){
        this.stateId = StateId;
    }
    public String baby(){
        return baby;
    }

    public void setBaby(String baby){
        this.baby = baby;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getImageUri() {
        return imageUri;
    }

    public void setImageUri(ArrayList<String> imageUri) {
        this.imageUri = imageUri;
    }

    public void setDay(String day){
        this.day = day;
    }

    public String getDay(){
        return this.day;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof DiaryContent){
            DiaryContent diaryContent = (DiaryContent)o;
            if(diaryContent.id.equals(this.id)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

}
