package com.amuletxheart.cameraderie.model;

import android.media.Image;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class ImageContainer implements Parcelable {
    private List<ImageWithThumbnail> imageWithThumbnails;

    public ImageContainer(){

    }

    public List<ImageWithThumbnail> getImagesWithThumbnails() {
        return imageWithThumbnails;
    }

    public void setImageWithThumbnail(ImageWithThumbnail imageWithThumbnail){
        List<ImageWithThumbnail> imagesWithThumbnails = new ArrayList<ImageWithThumbnail>();
        imagesWithThumbnails.add(imageWithThumbnail);

        this.setImagesWithThumbnails(imageWithThumbnails);
    }

    public void setImagesWithThumbnails(List<ImageWithThumbnail> imageWithThumbnails) {
        this.imageWithThumbnails = imageWithThumbnails;
        organize();
    }

    public void setImageUris(List<Uri> imageUris){
        for(Uri imageUri : imageUris){
            ImageWithThumbnail imageWithThumbnail = new ImageWithThumbnail();
            imageWithThumbnail.setImageUri(imageUri);
        }

        setImagesWithThumbnails(imageWithThumbnails);
    }

    public void setImageUris(String[] imageUrisArray){
        List<Uri> imageUris = new ArrayList<Uri>();

        for(String imageUri : imageUrisArray){
            imageUris.add(Uri.parse(imageUri));
        }
        setImageUris(imageUris);
    }

    public List<Uri> getImageUris(){
        List<Uri> imageUris = new ArrayList<Uri>();
        for(ImageWithThumbnail imageWithThumbnail : imageWithThumbnails){
            imageUris.add(imageWithThumbnail.getImageUri());
        }

        return imageUris;
    }

    public List<Uri> getThumbnailUris(){
        List<Uri> thumbnailUris = new ArrayList<Uri>();
        for(ImageWithThumbnail imageWithThumbnail : imageWithThumbnails){
            thumbnailUris.add(imageWithThumbnail.getThumbnailUri());
        }
        return thumbnailUris;
    }

    private void organize(){
        Collections.sort(imageWithThumbnails);
        Collections.reverse(imageWithThumbnails);
    }

    protected ImageContainer(Parcel in) {
        if (in.readByte() == 0x01) {
            imageWithThumbnails = new ArrayList<ImageWithThumbnail>();
            in.readList(imageWithThumbnails, ImageWithThumbnail.class.getClassLoader());
        } else {
            imageWithThumbnails = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (imageWithThumbnails == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(imageWithThumbnails);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageContainer> CREATOR = new Parcelable.Creator<ImageContainer>() {
        @Override
        public ImageContainer createFromParcel(Parcel in) {
            return new ImageContainer(in);
        }

        @Override
        public ImageContainer[] newArray(int size) {
            return new ImageContainer[size];
        }
    };
}