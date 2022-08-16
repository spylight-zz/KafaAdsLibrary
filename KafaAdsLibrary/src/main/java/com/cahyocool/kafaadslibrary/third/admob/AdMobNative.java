package com.cahyocool.kafaadslibrary.third.admob;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cahyocool.kafaadslibrary.data.Ad;
import com.cahyocool.kafaadslibrary.third.BaseThirdParty;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class AdMobNative extends BaseThirdParty {
    private AdLoader adLoader;
    private static final String TAG = AdMobNative.class.getSimpleName();

    public AdMobNative(@NonNull Context context,
                       @NonNull final Ad ad) {
        adLoader = new AdLoader.Builder(context, ad.key)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        if (onAdLoadListener != null) {
                            onAdLoadListener.onAdLoaded(ad, unifiedNativeAd);
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.d("onAdFailed","type: [ ADMOB ], kind: [ NATIVE ], errorCode: "+errorCode);
                        if (onAdLoadListener != null) {
                            onAdLoadListener.onAdFailedToLoad(ad);
                        }
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
    }

    @Override
    public void loadPreparedAd() {
        adLoader.loadAd(new AdRequest.Builder().build());
    }
}
