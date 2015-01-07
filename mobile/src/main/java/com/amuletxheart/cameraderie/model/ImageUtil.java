package com.amuletxheart.cameraderie.model;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class ImageUtil {

    public static Image loadFromStorage(StorageLocation location){
        Image image = new Image();

        if(location == StorageLocation.DCIM){
            File imageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "");

            Collection<File> imageFiles = FileUtils.listFiles(imageDir, HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE);

            List<String> imageURIList = new ArrayList<String>();

            for(File imageFile : imageFiles){
                imageURIList.add("file://" + imageFile.getAbsolutePath());
            }

            image.setImageUris(imageURIList);
        }
        else if(location == StorageLocation.CAMERADERIE){
            File imageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), location.toString());

        File[] imageFiles = imageDir.listFiles();
        List<String> imageURIList = new ArrayList<String>();

        for(File imageFile : imageFiles){
            imageURIList.add("file://" + imageFile.getAbsolutePath());
        }

        image.setImageUris(imageURIList);
    }

        return image;
    }

    public enum StorageLocation{
        DCIM ("Camera"),
        CAMERADERIE("CAMERAderie");

        private final String str;

        private StorageLocation(String str){
            this.str = str;
        }

        @Override
        public String toString(){
            return str;
        }
    }
}
