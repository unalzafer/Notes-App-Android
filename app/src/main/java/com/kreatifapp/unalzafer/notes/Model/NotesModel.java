package com.kreatifapp.unalzafer.notes.Model;

public class NotesModel {

    private String idNote, title,text,photoUrl,color;

    public NotesModel() {
    }

    public NotesModel(String idNote, String title, String text, String photoUrl, String color) {
        this.idNote = idNote;
        this.title = title;
        this.text = text;
        this.photoUrl = photoUrl;
        this.color = color;
    }

    public String getIdNote() {
        return idNote;
    }

    public void setIdNote(String idNote) {
        this.idNote = idNote;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
