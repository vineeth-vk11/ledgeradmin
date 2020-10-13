package com.adminsyndicate.HeadsHelper.Models;

import java.util.ArrayList;

public class HeadsModel {

    String address;
    String email;
    String password;
    String phone;
    String name;
    String id;
    String image;
    ArrayList<CompaniesModel> existingCompanies;

    public HeadsModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CompaniesModel> getExistingCompanies() {
        return existingCompanies;
    }

    public void setExistingCompanies(ArrayList<CompaniesModel> existingCompanies) {
        this.existingCompanies = existingCompanies;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
