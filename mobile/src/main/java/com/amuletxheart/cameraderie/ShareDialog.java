package com.amuletxheart.cameraderie;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amuletxheart.cameraderie.model.ImageWithThumbnail;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;


public class ShareDialog extends Dialog {
    private static final String TAG = ShareDialog.class.getName();

    private Context mContext;
    private ImageWithThumbnail imageWithThumbnail;

    public ShareDialog(final Context context, ImageWithThumbnail imageWithThumbnail){
        super(context);
        mContext = context;
        this.imageWithThumbnail = imageWithThumbnail;

        setTitle("Share to");
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.dialog_share, null);
        setContentView(relativeLayout);

        RelativeLayout facebookLayout = (RelativeLayout) findViewById(R.id.facebookLayout);
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click on shareFacebook button.");
                clickFacebook(v);
            }
        });
        RelativeLayout twitterLayout = (RelativeLayout) findViewById(R.id.twitterLayout);
        twitterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click on shareTwitter button.");
                clickTwitter(v);
            }
        });

        RelativeLayout instagramLayout = (RelativeLayout) findViewById(R.id.instagramLayout);
        instagramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click on shareTwitter button.");
                clickInstagram(v);
            }
        });
    }

    public void clickFacebook(View view){
        Toast.makeText(mContext, "facebook", Toast.LENGTH_SHORT).show();

    }
    public void clickTwitter(View view){
        Fabric.with(mContext, new TweetComposer());

        TweetComposer.Builder builder = new TweetComposer.Builder(mContext)
                .text("#Selfie@NYPSIT")
        .image(imageWithThumbnail.getImageUri());

        builder.show();

    }
    public void clickInstagram(View view) {
        String type = "image/*";
        String captionText = "#Selfie@NYPSIT";

        createInstagramIntent(type, imageWithThumbnail.getImageUri(), captionText);
    }
    private void createInstagramIntent(String type, Uri uri, String caption){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, caption);
        share.setPackage("com.instagram.android");

        // Broadcast the Intent.
        mContext.startActivity(share);
    }
}
