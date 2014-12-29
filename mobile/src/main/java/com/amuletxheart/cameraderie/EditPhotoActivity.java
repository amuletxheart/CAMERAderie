package com.amuletxheart.cameraderie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.gallery.activity.SimpleImageActivity;
import com.amuletxheart.cameraderie.gallery.fragment.ImagePagerFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class EditPhotoActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    Uri imageUri;
    ImageView frame;
    PhotoView image;
    DisplayImageOptions options;

    private boolean cameraPreview;
    private String[] imageUris;
    private int imagePosition;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        onWindowFocusChanged(true);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        cameraPreview = getIntent().getBooleanExtra(Constants.Extra.CAMERA_PREVIEW, false);
        imageUris = getIntent().getStringArrayExtra(Constants.Extra.IMAGE_URIS);
        imagePosition = getIntent().getIntExtra(Constants.Extra.IMAGE_POSITION, 0);

        imageUri = Uri.parse(imageUris[imagePosition]);

        frame = (ImageView)findViewById(R.id.frame);
        image = (PhotoView)findViewById(R.id.image);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://" + imageUri.getPath(), image, options);
    }

    public void clickDiscard(View v){
        Log.i(TAG, "Clicked on discard button.");
        frame.setImageResource(android.R.color.transparent);
    }

    public void clickSave(View v){
        Log.i(TAG, "Clicked on save button.");

        Bitmap frameBitmap = null;
        Bitmap imageBitmap = null;

        Drawable frameDrawable = frame.getDrawable();
        if (frameDrawable instanceof BitmapDrawable) {
            BitmapDrawable d = (BitmapDrawable) frameDrawable;
            frameBitmap = d.getBitmap();
        }

        Drawable imageDrawable = image.getDrawable();
        if (imageDrawable instanceof BitmapDrawable) {
            BitmapDrawable d = (BitmapDrawable) imageDrawable;
            imageBitmap = d.getBitmap();
        }
        //crop image to what is visible on screen
        Bitmap croppedBitmap = crop(image);
        //scale image down to size of the frame
        Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(croppedBitmap,
                frameBitmap.getWidth(), frameBitmap.getHeight(), true);
        //overlay the 2 bitmaps together as 1
        Bitmap compositeBitmap = overlay(scaledImageBitmap, frameBitmap);

        String filename = String.format("%d", System.currentTimeMillis());
        File compositeImage = new File(imageUri.getPath().replace(".jpg", "_edited_" + filename + ".jpg"));

        try{
            FileOutputStream outStream = new FileOutputStream(compositeImage);
            compositeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(compositeImage)));
        }
        catch(IOException e){
            Log.e(TAG, "Unable to write the edited image file.", e);
        }

        finish();

        List<String> imageUriList = new ArrayList<String>(Arrays.asList(imageUris));
        imageUriList.add(imagePosition, "file://" + compositeImage.getAbsolutePath());

        String[] imageUris = imageUriList.toArray(new String[imageUriList.size()]);
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.Extra.CAMERA_PREVIEW, cameraPreview);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
        intent.putExtra(Constants.Extra.IMAGE_POSITION, imagePosition);
        intent.putExtra(Constants.Extra.IMAGE_URIS, imageUris);
        startActivity(intent);
    }

    public void biggerView(View v)
    {
         switch (v.getId())
        {
            case R.id.image1: frame.setImageResource(R.drawable.frame_02_small);
                break;
            /*case R.id.image2: im.setImageResource(R.drawable.frame_02_small);
                break;
            case R.id.image3: im.setImageResource(R.drawable.frame_03);
                break;
            case R.id.image4: im.setImageResource(R.drawable.frame_04);
                break;
            case R.id.image5: im.setImageResource(R.drawable.frame_05);
                break;
            case R.id.image6: im.setImageResource(R.drawable.frame_06);
                break;
            case R.id.image7: im.setImageResource(R.drawable.frame_07);
                break;*/
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    private Bitmap crop(PhotoView photoView){
        Bitmap imageBitmap = null;
        Drawable imageDrawable = photoView.getDrawable();
        if (imageDrawable instanceof BitmapDrawable) {
            BitmapDrawable d = (BitmapDrawable) imageDrawable;
            imageBitmap = d.getBitmap();
        }

        RectF rect = photoView.getDisplayRect();
        float viewScale = photoView.getScale();

        float imageRatio = (float)imageBitmap.getWidth() / (float)imageBitmap.getHeight();
        float viewRatio = (float)photoView.getWidth() / (float)photoView.getHeight();

        float scale = 0;
        if (imageRatio > viewRatio) {
            // scale is based on image width
            scale = 1 / ((float)imageBitmap.getWidth() / (float)photoView.getWidth() / viewScale);

        } else {
            // scale is based on image height, or 1
            scale = 1 / ((float)imageBitmap.getHeight() / (float)photoView.getHeight() / viewScale);
        }

        // translate to bitmap scale
        rect.left       = -rect.left / scale;
        rect.top        = -rect.top / scale;
        rect.right      = rect.left + ((float)photoView.getWidth() / scale);
        rect.bottom     = rect.top + ((float)photoView.getHeight() / scale);

        if (rect.top<0) {
            rect.bottom -= Math.abs(rect.top);
            rect.top = 0;
        }
        if (rect.left<0) {
            rect.right -= Math.abs(rect.left);
            rect.left = 0;
        }

        Bitmap croppedImage = Bitmap.createBitmap(imageBitmap,(int)rect.left,(int)rect.top,
                (int)rect.width(), (int)rect.height());

        return croppedImage;
    }
}
