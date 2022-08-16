package com.cahyocool.kafaadslibrary.third.admob;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kafaads.kafaadslibrary.BuildConfig;
import com.cahyocool.kafaadslibrary.KafaAds;
import com.cahyocool.kafaadslibrary.data.Ad;
import com.cahyocool.kafaadslibrary.third.BaseInterstitialThirdParty;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobInterstitial extends BaseInterstitialThirdParty {
    private InterstitialAd interstitialAd;
    private static final String TAG = AdMobInterstitial.class.getSimpleName();
    private long startTime,endTime;

    public AdMobInterstitial(@NonNull Context context,
                             @NonNull final Ad ad) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "/AdMobInterstitial");
        }

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(ad.key);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                endTime = System.currentTimeMillis();
                if(KafaAds.TEST) {
                    Log.d(KafaAds.TAG, "#1 onAdFailedToLoad() - type: [ ADMOB ], kind: [ INTERSTITIAL ], CURRENT_TIME_MILLIS: " + String.valueOf((endTime-startTime)/1000)+"초");
                    Log.d(KafaAds.TAG,"#1 onAdFailedToLoad() - type: [ ADMOB ], kind: [ INTERSTITIAL ], errorCode: "+errorCode);
                }
                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdFailedToLoad(ad);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                endTime = System.currentTimeMillis();
                if(KafaAds.TEST) {
                    Log.d(KafaAds.TAG, "#1 onAdLoaded() - type: [ ADMOB ], kind: [ INTERSTITIAL ], CURRENT_TIME_MILLIS: " + String.valueOf((endTime-startTime)/1000)+"초");
                }

                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdLoaded(ad, null);
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                endTime = System.currentTimeMillis();
                if(KafaAds.TEST) {
                    Log.d(KafaAds.TAG, "#1 onAdClosed() - type: [ ADMOB ], kind: [ INTERSTITIAL ], CURRENT_TIME_MILLIS: " + String.valueOf((endTime-startTime)/1000)+"초");
                }

                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdClosed(ad);
                }

            }
        });
    }

    @Override
    public void loadPreparedAd() {
        startTime = System.currentTimeMillis();
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void showPreparedAd() {
        interstitialAd.show();
    }
}
