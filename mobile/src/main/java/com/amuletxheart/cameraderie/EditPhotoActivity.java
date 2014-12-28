package com.amuletxheart.cameraderie;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amuletxheart.cameraderie.gallery.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class EditPhotoActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    ImageView im;
    int windowwidth;
    int windowheight;
    ImageView ima1, ima2;
    DisplayImageOptions options;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

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

        Uri imageUri = getIntent().getParcelableExtra(Constants.Extra.IMAGE_URI);

        windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        windowheight = getWindowManager().getDefaultDisplay().getHeight();

        System.out.println("width" +windowwidth);
        System.out.println("height" +windowheight);

        ima1 = (ImageView)findViewById(R.id.frame);

        ima2 = (ImageView)findViewById(R.id.image);
        //image will be set here
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://" + imageUri.getPath(), ima2, options);
        //ima2.setImageResource(imageUri);
        ima2.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                layoutParams = (RelativeLayout.LayoutParams) ima2.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        System.out.println("value of x" + x_cord);
                        System.out.println("value of y" + y_cord);

                        if (x_cord > windowwidth) {
                            x_cord = windowwidth;
                        }
                        if (y_cord > windowheight) {
                            y_cord = windowheight;
                        }
                        layoutParams.leftMargin = x_cord - 50;
                        layoutParams.topMargin = y_cord - 150;
                        //   layoutParams.rightMargin = x_cord-25;
                        //   layoutParams.bottomMargin = y_cord-25;
                        ima2.setLayoutParams(layoutParams);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    public void biggerView(View v)
    {
        im=(ImageView)findViewById(R.id.frame);

        switch (v.getId())
        {
            case R.id.image1: im.setImageResource(R.drawable.frame_02_small);
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
}
