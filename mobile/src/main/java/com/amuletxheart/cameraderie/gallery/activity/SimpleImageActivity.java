/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.amuletxheart.cameraderie.gallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.R;
import com.amuletxheart.cameraderie.gallery.fragment.BaseFragment;
import com.amuletxheart.cameraderie.gallery.fragment.ImageGalleryFragment;
import com.amuletxheart.cameraderie.gallery.fragment.ImageGridFragment;
import com.amuletxheart.cameraderie.gallery.fragment.ImageListFragment;
import com.amuletxheart.cameraderie.gallery.fragment.ImagePagerFragment;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class SimpleImageActivity extends FragmentActivity {
    BaseFragment fr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
		String tag;
		int titleRes;
		switch (frIndex) {
			default:
			case ImageListFragment.INDEX:
				tag = ImageListFragment.class.getSimpleName();
				fr = (BaseFragment)getSupportFragmentManager().findFragmentByTag(tag);
				if (fr == null) {
					fr = new ImageListFragment();
				}
				titleRes = R.string.ac_name_image_list;
				break;
			case ImageGridFragment.INDEX:
				tag = ImageGridFragment.class.getSimpleName();
				fr = (BaseFragment)getSupportFragmentManager().findFragmentByTag(tag);
				if (fr == null) {
					fr = new ImageGridFragment();
				}
				titleRes = R.string.ac_name_image_grid;
				break;
			case ImagePagerFragment.INDEX:
				tag = ImagePagerFragment.class.getSimpleName();
				fr = (BaseFragment)getSupportFragmentManager().findFragmentByTag(tag);
				if (fr == null) {
					fr = new ImagePagerFragment();
					fr.setArguments(getIntent().getExtras());
				}
				titleRes = R.string.ac_name_image_pager;
				break;
			case ImageGalleryFragment.INDEX:
				tag = ImageGalleryFragment.class.getSimpleName();
				fr = (BaseFragment)getSupportFragmentManager().findFragmentByTag(tag);
				if (fr == null) {
					fr = new ImageGalleryFragment();
				}
				titleRes = R.string.ac_name_image_gallery;
				break;
		}

		setTitle(titleRes);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fr.onBackPressed();
    }
}