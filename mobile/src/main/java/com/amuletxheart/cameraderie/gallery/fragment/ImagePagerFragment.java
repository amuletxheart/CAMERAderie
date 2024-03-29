/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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
package com.amuletxheart.cameraderie.gallery.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amuletxheart.cameraderie.EditPhotoActivity;
import com.amuletxheart.cameraderie.R;
import com.amuletxheart.cameraderie.ShareDialog;
import com.amuletxheart.cameraderie.gallery.Constants;
import com.amuletxheart.cameraderie.gallery.activity.SimpleImageActivity;
import com.amuletxheart.cameraderie.gallery.activity.TabbedImageActivity;
import com.amuletxheart.cameraderie.gallery.photoview.HackyViewPager;
import com.amuletxheart.cameraderie.model.ImageContainer;
import com.amuletxheart.cameraderie.model.ImageUtil;
import com.amuletxheart.cameraderie.model.ImageWithThumbnail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerFragment extends BaseFragment {
    private static final String TAG = ImagePagerFragment.class.getName();

	public static final int INDEX = 2;

    private ImageContainer imageContainer;
    private ViewPager imagePager;
	private String[] imageUrls;
    private boolean cameraPreview;
    private boolean showControls = true;

	private DisplayImageOptions options;

    public void addButtonListeners() {
        final ImageButton imageButtonTrash = (ImageButton) getView().findViewById(R.id.imageButtonTrash);
        imageButtonTrash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked on trash button.");

                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.delete_dialog_message)
                        .setTitle(R.string.delete_dialog_title);

                builder.setPositiveButton(R.string.delete_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int dialogId) {
                        // User clicked OK button

                        int imageIndex = imagePager.getCurrentItem();
                        Log.i(TAG, "Deleting image index " + imageIndex);

                        ImageWithThumbnail imageWithThumbnail = imageContainer.getImagesWithThumbnails().get(imageIndex);

                        String imageURIString = imageWithThumbnail.getImageUri().toString();
                        Log.i(TAG, "Deleting image URI " + imageURIString);

                        MemoryCacheUtils.removeFromCache(imageURIString, ImageLoader.getInstance().getMemoryCache());
                        DiskCacheUtils.removeFromCache(imageURIString, ImageLoader.getInstance().getDiskCache());
                        int success = getActivity().getContentResolver().delete(imageWithThumbnail.getImageUri(), null, null);

                        imageContainer.getImagesWithThumbnails().remove(imageIndex);

                        getActivity().finish();

                        if(cameraPreview){
                            Log.i(TAG, "Camera preview is true");
                            onBackPressed();
                        }
                        else{
                            if(imageContainer.getImagesWithThumbnails().isEmpty()){
                                onBackPressed();
                            }
                            else{
                                Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
                                intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
                                intent.putExtra(Constants.Extra.IMAGE_POSITION, imageIndex);
                                intent.putExtra(Constants.Extra.IMAGE_CONTAINER, imageContainer);
                                startActivity(intent);
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.delete_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // do nothing
                    }
                });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final ImageButton imageButtonEdit = (ImageButton) getView().findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked on edit button.");

                int imageIndex = imagePager.getCurrentItem();

                String imageURIString = imageUrls[imageIndex];

                Uri imageUri = Uri.parse(imageURIString);
                Log.i(TAG, "ImageContainer URI: " + imageUri.toString());

                Intent intent = new Intent(getActivity(), EditPhotoActivity.class);
                intent.putExtra(Constants.Extra.CAMERA_PREVIEW, cameraPreview);
                intent.putExtra(Constants.Extra.IMAGE_CONTAINER, imageContainer);
                intent.putExtra(Constants.Extra.IMAGE_POSITION, imageIndex);
                startActivity(intent);
            }
        });

        final ImageButton imageButtonShare = (ImageButton) getView().findViewById(R.id.imageButtonShare);
        imageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click on share button.");
                ImageWithThumbnail imageWithThumbnail = imageContainer
                        .getImagesWithThumbnails()
                        .get(imagePager.getCurrentItem());
                final Dialog dialog = new ShareDialog(getActivity(), imageWithThumbnail);
            }
        });

    }

    public void clickShowControls(PhotoView photoView){
        final LinearLayout controls = (LinearLayout)getView().findViewById(R.id.linearLayoutControls);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        final String TWITTER_KEY = "DYvYvBnnqFTWjpbQ7Q16TNRPN";
        final String TWITTER_SECRET = "DJ95kXnrnHYW71h6ypq1n1lemkmN92k4bYcbGh67WlsudzIddq";

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));

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

        imageContainer = getArguments().getParcelable(Constants.Extra.IMAGE_CONTAINER);
        imageUrls = ImageUtil.uriListToStringArray(imageContainer.getImageUris());
        cameraPreview = getArguments().getBoolean(Constants.Extra.CAMERA_PREVIEW);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
		ViewPager pager = (HackyViewPager) rootView.findViewById(R.id.pager);
		pager.setAdapter(new ImageAdapter());
		pager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));

        imagePager = pager;
		return rootView;
	}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButtonListeners();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back button pressed.");

        if(cameraPreview){

        }
        else{
            Intent intent = new Intent(getActivity(), TabbedImageActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private class ImageAdapter extends PagerAdapter {
		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.image);
            photoView.setMediumScale(2.0f);
            photoView.setMaximumScale(4.0f);

            clickShowControls(photoView);

			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			ImageLoader.getInstance().displayImage(imageUrls[position], photoView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "ImageContainer can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}