package com.amuletxheart.cameraderie.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yong Xin Shen on 1/7/2015.
 */
public class ImageUtil {
    private static final String TAG = ImageUtil.class.getName();

    public static Image loadFromStorage(Context context, StorageLocation location){
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

        List<Uri> contentUris = getContentUris(context, new ArrayList<File>(Arrays.asList(imageFiles)));
        List<String> imageURIList = new ArrayList<String>();

        /*for(File imageFile : imageFiles){
            imageURIList.add("file://" + imageFile.getAbsolutePath());
        }*/

        for(Uri uri : contentUris){
            imageURIList.add(uri.toString());
        }

        image.setImageUris(imageURIList);
    }

        return image;
    }

    public static Uri getContentUri(Context context, File imageFile) {
        List<File> imageFilePaths = new ArrayList<File>();
        imageFilePaths.add(imageFile);

        return getContentUris(context, imageFilePaths).get(0);
    }

    public static List<Uri> getContentUris(Context context, List<File> imageFiles){
        List<Uri> contentUris = new ArrayList<Uri>();
        List<String> imageFilePaths = new ArrayList<String>();
        for(File imageFile : imageFiles){
            imageFilePaths.add(imageFile.getAbsolutePath());
        }

        //Delete from MediaStore, adapted from http://stackoverflow.com/a/20780472/1966873
        // Set up the projection (we only need the ID)
        String[] projection = { MediaStore.Images.Media._ID };

        // Match on the file path
        String selection = "";
        for(int i=0; i<imageFilePaths.size(); i++){
            //last element in List
            if(i == imageFilePaths.size()-1){
                selection += MediaStore.Images.Media.DATA + " = ?";
            }
            else{
                selection += MediaStore.Images.Media.DATA + " = ? OR ";
            }
        }

        String[] selectionArgs = imageFilePaths.toArray(new String[imageFilePaths.size()]);

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.i(TAG, "Image found " + contentUri);

            contentUris.add(contentUri);
        }

        c.close();

        return contentUris;
    }

    public static List<Uri> getThumbnails(){
        List<Uri> uris= new ArrayList<Uri>();

        return uris;
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
