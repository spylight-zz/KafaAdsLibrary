package com.kafaads.kafaadslibrary;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class KafaAds {
    private static KafaModel kafaModel;
    private String appid;
    private Activity activity;

    public KafaAds(String appid, Activity activity) {
        this.appid = appid;
        this.activity = activity;
    }

    public static void setAds(KafaModel kafaModel) {
        KafaAds.kafaModel = kafaModel;
    }

    public static KafaModel getAds() {
        return kafaModel;
    }

    public void showBanner(String BannerId, int view, Boolean... typeSize) {
        MobileAds.initialize((this.activity).getApplicationContext(), this.appid);
        View f = activity.findViewById(view);
        final AdView mAdView = new AdView((this.activity).getApplicationContext());
        //adaptive banner
        AdSize adSize = null;
        if (typeSize.length == 1) {
            adSize = AdSize.MEDIUM_RECTANGLE;
        } else {
            adSize = getAdSize();
        }
        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(BannerId);
        ((RelativeLayout) f).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("09D583BED8AB039A6F86A0EBE7B92390")
                .build();
        mAdView.loadAd(adRequest);
    }

    public AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = this.activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void showInterstitial() {

    }
}
