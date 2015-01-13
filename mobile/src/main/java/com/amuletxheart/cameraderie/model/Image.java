package com.amuletxheart.cameraderie.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class Image {
    private List<ImageWithThumbnail> imageUris;

    public List<ImageWithThumbnail> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<ImageWithThumbnail> imageUris) {
        this.imageUris = imageUris;
        organize();
    }

    public void setImageUris(String[] imageUrisArray){
        List<ImageWithThumbnail> imageUriList = new ArrayList<ImageWithThumbnail>();
        for(String imageUri : imageUrisArray){
            ImageWithThumbnail imageWithThumbnail = new ImageWithThumbnail();
            imageWithThumbnail.setImageUri(Uri.parse(imageUri));
            imageUriList.add(imageWithThumbnail);
        }
        setImageUris(imageUriList);
    }

    public String[] imageUrisToArray(){
        String [] imageUriArray = new String[imageUris.size()];
        for(int i = 0; i<imageUris.size(); i++){
            imageUriArray[i] = imageUris.get(i).getImageUri().toString();
        }

        return imageUriArray;
    }

    private void organize(){
        Collections.sort(imageUris);
        Collections.reverse(imageUris);
    }
}
