package com.hackathon.aran2;

public class InfoBean {
    public void setDay(Long day) {
        this.day = day;
    }

    String birth, email, name, gender;
    Long day;

    public Long getDay() {
        return day;
    }

    public InfoBean(){

    }
    public InfoBean(String birth, String email, String name, String gender, Long day, Long signUpDate){
        this.birth = birth;
        this.email = email;
        this.name = name;
        this.day = day;
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}