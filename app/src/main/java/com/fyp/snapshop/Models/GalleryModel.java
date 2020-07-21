package com.fyp.snapshop.Models;

public class GalleryModel {
    private String galleryImage;
    private String mKey;



    public GalleryModel() {
    }
    public GalleryModel(String galleryImage, String mKey) {
        this.galleryImage = galleryImage;
        this.mKey = mKey;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(String galleryImage) {
        this.galleryImage = galleryImage;
    }
}
