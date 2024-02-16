package com.thipna219166.onlineshoppingapp.Model;

public class User {
    private String name,phone,password;
    //private String adress;

    public User() {
    }
/*
    public User(String name, String phone, String password, String adress*) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        //this.adress = adress;
    }

 */

    public User(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   /* public String getAdress() {
        return adress;
    }*/
}
