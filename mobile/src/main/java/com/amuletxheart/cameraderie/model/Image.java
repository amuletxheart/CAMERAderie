package com.amuletxheart.cameraderie.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class Image {
    private List<String> imageUris;

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
        organize();
    }

    public void setImageUris(String[] imageUrisArray){
        setImageUris(new ArrayList<String>(Arrays.asList(imageUrisArray)));
    }

    public String[] imageUrisToArray(){
        return imageUris.toArray(new String[imageUris.size()]);
    }

    private void organize(){
        Collections.sort(imageUris);
        Collections.reverse(imageUris);
    }
}
