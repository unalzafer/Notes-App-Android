package com.kreatifapp.unalzafer.notes.Model;

import java.util.ArrayList;

public class UserModel {

    public String name;
    public String email;
    public static String photoUrl;
    private ArrayList<NotesModel> List;

    public  UserModel(){}

    public UserModel(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getPhotoUrl() {
        return photoUrl;
    }

    public static void setPhotoUrl(String photoUrl) {
        UserModel.photoUrl = photoUrl;
    }

    public ArrayList<NotesModel> getList() {
        return List;
    }

    public void setList(ArrayList<NotesModel> list) {
        List = list;
    }
}
