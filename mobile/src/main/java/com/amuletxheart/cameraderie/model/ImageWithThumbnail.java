package com.amuletxheart.cameraderie.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by L33901 on 1/13/2015.
 */
public class ImageWithThumbnail implements Parcelable, Comparable{
    Uri imageUri;
    Uri thumbnailUri;

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

    protected ImageWithThumbnail(Parcel in) {
        imageUri = (Uri) in.readValue(Uri.class.getClassLoader());
        thumbnailUri = (Uri) in.readValue(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    @Override
    public int compareTo(Object another) {
        Uri uri = ((ImageWithThumbnail) another).getImageUri();
        return imageUri.compareTo(uri);
    }
}