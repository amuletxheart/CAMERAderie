package com.amuletxheart.cameraderie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.gallery.activity.SimpleImageActivity;
import com.amuletxheart.cameraderie.gallery.fragment.ImagePagerFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class EditPhotoActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    private EditPhotoActivity editPhotoActivity = this;
    Uri imageUri;
    ImageView frame;
    PhotoView image;
    DisplayImageOptions options;

    private boolean cameraPreview;
    private boolean showControls = true;
    private String[] imageUris;
    private int imagePosition;

    private String[] frameUrls;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    private String[] loadImagesFromStorage(){
        String [] allFrames = null;
        List<String> usingFramesList = new ArrayList<String>();
        String [] usingFrames = null;
        String fileStart;
        try{
            allFrames = getAssets().list("frames");
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fileStart = "l";
            }
            else{
                fileStart = "p";
            }
            for(String str : allFrames){
                if(str.startsWith(fileStart)) {
                    usingFramesList.add(str);
                }
            }
        }
        catch(IOException e){
            Log.e(TAG, "Error loading frames. " + e);
        }

        usingFrames = usingFramesList.toArray(new String[usingFramesList.size()]);

        for(int i = 0; i<usingFrames.length; i++){
            usingFrames[i] = "assets://frames/" + usingFrames[i];
        }

        return usingFrames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        frameUrls = loadImagesFromStorage();

        onWindowFocusChanged(true);

        ImageButton imageButtonDiscard = (ImageButton)findViewById(R.id.imageButtonDiscard);
        setImageButtonEnabled(this, false, imageButtonDiscard, R.drawable.ic_action_cancel);

        ImageButton imageButtonSave = (ImageButton)findViewById(R.id.imageButtonSave);
        setImageButtonEnabled(this, false, imageButtonSave, R.drawable.ic_action_save);

        clickShowControls();

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE_SAFE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        loadListView();

        cameraPreview = getIntent().getBooleanExtra(Constants.Extra.CAMERA_PREVIEW, false);
        imageUris = getIntent().getStringArrayExtra(Constants.Extra.IMAGE_URIS);
        imagePosition = getIntent().getIntExtra(Constants.Extra.IMAGE_POSITION, 0);

        imageUri = Uri.parse(imageUris[imagePosition]);

        frame = (ImageView)findViewById(R.id.frame);
        image = (PhotoView)findViewById(R.id.image);

        //image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        /*image.setMediumScale(2.0f);
        image.setMaximumScale(4.0f);*/

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://" + imageUri.getPath(), image, options);
    }

    public void clickShowControls(){
        PhotoView photoView = (PhotoView)findViewById(R.id.image);
        final LinearLayout controls = (LinearLayout)findViewById(R.id.linearLayoutControls);
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v2) {
                Log.i(TAG, "Clicked on show controls button.");

                if(showControls){
                    controls.setVisibility(View.INVISIBLE);
                    showControls = false;
                }
                else{
                    controls.setVisibility(View.VISIBLE);
                    showControls = true;
                }
            }
        });
    }

    public void clickDiscard(View v){
        Log.i(TAG, "Clicked on discard button.");
        frame.setImageResource(android.R.color.transparent);

        ImageButton imageButtonDiscard = (ImageButton)findViewById(R.id.imageButtonDiscard);
        setImageButtonEnabled(this, false, imageButtonDiscard, R.drawable.ic_action_cancel);

        ImageButton imageButtonSave = (ImageButton)findViewById(R.id.imageButtonSave);
        setImageButtonEnabled(this, false, imageButtonSave, R.drawable.ic_action_save);
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

    private void loadListView(){
        HListView listView = (HListView) findViewById(R.id.listViewFrame);
        listView.setAdapter(new ImageAdapter());
        listView.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "Frame clicked: " + frameUrls[position]);

                ImageButton imageButtonDiscard = (ImageButton)findViewById(R.id.imageButtonDiscard);
                setImageButtonEnabled(editPhotoActivity, true, imageButtonDiscard, R.drawable.ic_action_cancel);

                ImageButton imageButtonSave = (ImageButton)findViewById(R.id.imageButtonSave);
                setImageButtonEnabled(editPhotoActivity, true, imageButtonSave, R.drawable.ic_action_save);

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(frameUrls[position], frame, options);
            }
        });
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

    /**
     * Sets the specified image buttonto the given state, while modifying or
     * "graying-out" the icon as well
     *
     * @param enabled The state of the menu item
     * @param item The menu item to modify
     * @param iconResId The icon ID
     */
    public static void setImageButtonEnabled(Context ctxt, boolean enabled, ImageButton item,
                                             int iconResId) {
        item.setEnabled(enabled);
        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setBackground(icon);
    }

    /**
     * Mutates and applies a filter that converts the given drawable to a Gray
     * image. This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter
     *         applied.
     */
    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        ImageAdapter() {
            inflater = LayoutInflater.from(editPhotoActivity);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_error)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

        @Override
        public int getCount() {
            return frameUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ImageView imageView;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_list_image, parent, false);
                imageView = (ImageView) view.findViewById(R.id.image);
                view.setTag(imageView);
            } else {
                imageView = (ImageView) view.getTag();
            }

            ImageLoader.getInstance().displayImage(frameUrls[position], imageView, options, animateFirstListener);

            return view;
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
