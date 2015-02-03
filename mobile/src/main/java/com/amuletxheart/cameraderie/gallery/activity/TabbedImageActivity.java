package com.amuletxheart.cameraderie.gallery.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amuletxheart.cameraderie.R;
import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.gallery.fragment.ImageGridFragment;
import com.amuletxheart.cameraderie.model.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

public class TabbedImageActivity extends ActionBarActivity {
    private static final String STATE_POSITION = "STATE_POSITION";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_image);

        int pagerPosition = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_POSITION);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pagerPosition);
        mViewPager.setPageMargin(20);

        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(STATE_POSITION, mViewPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_image, menu);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment gridFragment1;
        Fragment gridFragment2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            gridFragment1 = new ImageGridFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable(Constants.Extra.IMAGE_SOURCE, ImageUtil.StorageLocation.CAMERADERIE);
            gridFragment1.setArguments(bundle1);

            Bundle bundle2 = new Bundle();
            bundle2.putSerializable(Constants.Extra.IMAGE_SOURCE, ImageUtil.StorageLocation.DCIM);
            gridFragment2 = new ImageGridFragment();
            gridFragment2.setArguments(bundle2);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return gridFragment1;
                case 1:
                    return gridFragment2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_cameraderie);
                case 1:
                    return getString(R.string.title_dcim);
                default:
                    return null;
            }
        }
    }
}
