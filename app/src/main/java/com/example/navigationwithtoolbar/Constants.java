package com.example.navigationwithtoolbar;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
    SharedPreferences sharedPreferences;
    Context context;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String password_coded;
    private String name;
    final static String ip ="trading.praedicofinance.com";
//    final static String ip ="192.168.1.8";
//    final static String ip ="192.168.43.216";

    public String getPhone() {
        phone = sharedPreferences.getString("phone","");
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
        sharedPreferences.edit().putString("phone",phone).commit();
    }

    public String getDob() {
        dob = sharedPreferences.getString("dob","");
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
        sharedPreferences.edit().putString("dob",dob).commit();
    }

    public String getGender() {
        gender = sharedPreferences.getString("gender","");
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        sharedPreferences.edit().putString("gender",gender).commit();
    }

    public String getPassword_coded() {
        password_coded = sharedPreferences.getString("coded_password","");
        return password_coded;
    }

    public void setPassword_coded(String password_coded) {
        this.password_coded = password_coded;
        sharedPreferences.edit().putString("coded_password",password_coded).commit();
    }

    public String getName() {
        name = sharedPreferences.getString("name","");
        return name;
    }
    public void setName(String name) {
        this.name = name;
        sharedPreferences.edit().putString("name",name).commit();
    }



    public void removeUser(){
        setEmail("");
        setDob("");
        setGender("");
        setName("");
        setPassword_coded("");
        setPhone("");
    }

    public String getEmail() {
        email = sharedPreferences.getString("useremail","");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        sharedPreferences.edit().putString("useremail",email).commit();
    }


    public String getIp() {
        return ip;
    }

    public Constants(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
    }
}
