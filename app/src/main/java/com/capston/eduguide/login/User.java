package com.capston.eduguide.login;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    private String id;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String age;
    private String grade;

    //사용자 페이지 프로필 추가
    private String nickname;

    private String intro;

    public User() {
        // 기본 생성자 추가
    }

    public User(String id, String password, String email, String phone, String name, String age) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.grade = "0"; //기본 등급 0으로 설정
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    //사용자 페이지 프로필 추가
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getGrade() { return  grade; }
    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void saveToFirebase() {
        // Get the Firebase database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Save the user object to the "users" collection in Firebase
        databaseRef.child("users").child(id).setValue(this);
    }
}
