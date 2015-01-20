package com.amuletxheart.cameraderie.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by L33901 on 1/13/2015.
 */
public class ImageWithThumbnail implements Comparable, Parcelable {
    String imageFilePath;
    Uri imageUri;
    Uri thumbnailUri;

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public ImageWithThumbnail(){

    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    @Override
    public int compareTo(Object another) {
        String anotherImageFilePath = ((ImageWithThumbnail) another).getImageFilePath();
        return imageFilePath.compareTo(anotherImageFilePath);
    }

    protected ImageWithThumbnail(Parcel in) {
        imageFilePath = in.readString();
        imageUri = (Uri) in.readValue(Uri.class.getClassLoader());
        thumbnailUri = (Uri) in.readValue(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageFilePath);
        dest.writeValue(imageUri);
        dest.writeValue(thumbnailUri);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageWithThumbnail> CREATOR = new Parcelable.Creator<ImageWithThumbnail>() {
        @Override
        public ImageWithThumbnail createFromParcel(Parcel in) {
            return new ImageWithThumbnail(in);
        }

        @Override
        public ImageWithThumbnail[] newArray(int size) {
            return new ImageWithThumbnail[size];
        }
    };
}