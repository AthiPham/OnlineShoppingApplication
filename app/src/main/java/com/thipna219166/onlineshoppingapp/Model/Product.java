package com.thipna219166.onlineshoppingapp.Model;

public class Product {
    private String pname,description,price,image,category,pid,date,time;
    private int storage;

    public Product() {
    }

    public Product(String pname, String description, String price, String image, String category, String pid, String date, String time, int storage) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.storage = storage;
    }

    public String getPname() {
        return pname;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public int getStorage() {return storage;}
}
