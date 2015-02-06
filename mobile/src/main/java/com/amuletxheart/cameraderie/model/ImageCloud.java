package com.amuletxheart.cameraderie.model;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by L33901 on 2/4/2015.
 */
public class ImageCloud {
    private static final String TAG = ImageCloud.class.getName();

    public ImageCloud(){
        Map config = new HashMap();
        config.put("cloud_name", "cameraderie");
        config.put("api_key", "533745631351682");
        config.put("api_secret", "gL_vHB8hELaXRG3I-amlAPtqatU");
        Cloudinary cloudinary = new Cloudinary(config);
        Map response = null;

        Map options = new HashMap();
        options.put("type", "upload");
        options.put("prefix", "nyp_open_house_2015");
        try {
            response = cloudinary.api().resources(Collections.emptyMap());

        } catch (Exception e) {
            Log.e(TAG, "Unable to load images from cloudinary.", e);
        }
    }
}
