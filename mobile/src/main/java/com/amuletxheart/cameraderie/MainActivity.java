package com.amuletxheart.cameraderie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.amuletxheart.cameraderie.camera.CameraActivity;
import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.gallery.activity.GalleryHomeActivity;
import com.amuletxheart.cameraderie.gallery.activity.SimpleImageActivity;
import com.amuletxheart.cameraderie.gallery.activity.TabbedImageActivity;
import com.amuletxheart.cameraderie.gallery.fragment.ImageGridFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File imageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CAMERAderie");

        if (! imageDir.exists()){
            if (! imageDir.mkdirs()){
                Log.e(TAG, "Failed to create directory");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void clickCamera(View view){
        Log.i(TAG, "Clicked on camera button.");

        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void clickGallery(View view){
        Log.i(TAG, "Clicked on gallery button.");

        progressDialog = new ProgressDialog(this);
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        progressDialog.show();
        progressDialog.setContentView(progressBar);

        Intent intent = new Intent(this, TabbedImageActivity.class);
        startActivity(intent);
    }
}
