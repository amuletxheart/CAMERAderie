package com.amuletxheart.cameraderie.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class Image {
    private List<ImageWithThumbnail> imageWithThumbnails;

    public List<ImageWithThumbnail> getImageWithThumbnails() {
        return imageWithThumbnails;
    }

    public void setImageWithThumbnails(List<ImageWithThumbnail> imageWithThumbnails) {
        this.imageWithThumbnails = imageWithThumbnails;
        organize();
    }

    public void setImageUris(String[] imageUrisArray){
        List<ImageWithThumbnail> imageUriList = new ArrayList<ImageWithThumbnail>();
        for(String imageUri : imageUrisArray){
            ImageWithThumbnail imageWithThumbnail = new ImageWithThumbnail();
            imageWithThumbnail.setImageUri(Uri.parse(imageUri));
            imageUriList.add(imageWithThumbnail);
        }
        setImageWithThumbnails(imageUriList);
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
}
