package com.amuletxheart.cameraderie;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.amuletxheart.cameraderie.model.ImageWithThumbnail;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.List;

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
        shareToFacebook(view);
        dismiss();
    }
    public void clickTwitter(View view){
        Fabric.with(mContext, new TweetComposer());

        TweetComposer.Builder builder = new TweetComposer.Builder(mContext)
                .text("#Selfie@NYPSIT")
        .image(imageWithThumbnail.getImageUri());

        builder.show();
        dismiss();
    }
    public void clickInstagram(View view) {
        String type = "image/*";
        String captionText = "#Selfie@NYPSIT";

        createInstagramIntent(type, imageWithThumbnail.getImageUri(), captionText);
        dismiss();
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
    public void shareToFacebook(View view) {
        // uri to the image you want to share
        Uri path = imageWithThumbnail.getImageUri();

        // create email intent first to remove bluetooth + others options
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, "#Selfie@NYPSIT");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "#Selfie@NYPSIT");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.setType("image/jpeg");
        // Create the chooser based on the email intent
        Intent openInChooser = Intent.createChooser(emailIntent, "Share to");

        // Check for other packages that open our mime type
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/jpeg");

        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("com.amuletxheart.cameraderie")) {
                emailIntent.setPackage(packageName);
            }
            intentList.add(new LabeledIntent(emailIntent, packageName, ri.loadLabel(pm), ri.icon));
        }
        // Get our custom intent put here as we only want your app to get it not others
        Intent customIntent = new Intent("facebooktestingimageandtext.intent.action.SEND");
        customIntent.setType("image/jpeg");
        customIntent.setAction("facebooktestingimageandtext.intent.action.SEND");

        resInfo = pm.queryIntentActivities(customIntent, 0);
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("com.amuletxheart.cameraderie")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.setType("image/jpeg");
                if (packageName.contains("com.amuletxheart.cameraderie")) {
                    // My custom facebook intent to do something very simple!
                    intent.putExtra(Intent.EXTRA_TEXT, "caption #testhashtag");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert the list of intents(intentList) to array and add as extra intents
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        mContext.startActivity(openInChooser);
    }
}
