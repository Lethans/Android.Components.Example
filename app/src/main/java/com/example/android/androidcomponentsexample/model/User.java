package com.example.android.androidcomponentsexample.model;

public class User {
    private String fullName;
    private String email;
    private String password;
    private String telephone;
    private String registerType;

    public User(String fullName, String email, String password, String telephone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
    }

    public User() {
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
