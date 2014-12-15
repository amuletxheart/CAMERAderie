package com.amuletxheart.cameraderie;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amuletxheart.cameraderie.camera.CameraActivity;
import com.amuletxheart.cameraderie.gallery.activity.HomeActivity;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/asenine.ttf");
        TextView myTextView = (TextView)findViewById(R.id. textViewTitle);
        myTextView.setTypeface(myTypeface);
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

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
