package com.fyp.snapshop.Models;

public class ContactsEditor {
    public String name,image;

    public ContactsEditor(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public ContactsEditor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
