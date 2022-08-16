package com.cahyocool.kafaadslibrary.third.admob;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cahyocool.kafaadslibrary.data.Ad;
import com.cahyocool.kafaadslibrary.third.BaseRewardThirdParty;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kafaads.kafaadslibrary.BuildConfig;

public class AdMobReward extends BaseRewardThirdParty {
    private RewardedVideoAd rewardedVideoAd;
    private static final String TAG = AdMobReward.class.getSimpleName();
    private long startTime, endTime;
    private Ad ad;

    public AdMobReward(@NonNull Context context,
                       @NonNull final Ad ad) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "/AdMobReward");
        }
        this.ad = ad;
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                endTime = System.currentTimeMillis();
                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdLoaded(ad, null);
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                endTime = System.currentTimeMillis();
                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdClosed(ad);
                }
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                endTime = System.currentTimeMillis();
                if (onAdLoadListener != null) {
                    onAdLoadListener.onAdFailedToLoad(ad);
                }
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
    }

    @Override
    public void showPreparedAd() {
        rewardedVideoAd.show();
    }

    @Override
    public void loadPreparedAd() {
        startTime = System.currentTimeMillis();
        rewardedVideoAd.loadAd(ad.key, new AdRequest.Builder().build());
    }
}
