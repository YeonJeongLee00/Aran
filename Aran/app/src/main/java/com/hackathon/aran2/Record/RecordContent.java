package com.hackathon.aran2.Record;

public class RecordContent {
    String date, emotion, question,stringRecord;

    boolean isRecorded;

    public boolean getIsRecorded() {
        return isRecorded;
    }

    public void setIsRecorded(boolean isRecorded) {
        this.isRecorded = isRecorded;
    }



    public String getDate() {
        return date;
    }

    public String getStringRecord() {
        return stringRecord;
    }

    public void setStringRecord(String stringRecord) {
        this.stringRecord = stringRecord;
    }



    public RecordContent(){

    }

    public RecordContent(String date, String emotion, String question, String stringRecord, boolean isRecorded){
        this.date = date;
        this.emotion = emotion;
        this.question = question;
        this.isRecorded = isRecorded;
        this.stringRecord = stringRecord;
    }
    public String getQuestion(){return  question;}
    public void setQuestion(String question) {
        this.question = question;
    }



    public void setDate(String date){
        this.date = date;
    }


    public String getEmotion(){
        return emotion;
    }
    public void setEmotion(String emotion){
        this.emotion = emotion;
    }

}
