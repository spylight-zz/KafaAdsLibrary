package com.cahyocool.kafaadslibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cahyocool.kafaadslibrary.data.Ad;
import com.cahyocool.kafaadslibrary.data.AdDataRepository;
import com.cahyocool.kafaadslibrary.data.AdDataSource;
import com.cahyocool.kafaadslibrary.data.AdType;
import com.cahyocool.kafaadslibrary.models.KafaModel;
import com.cahyocool.kafaadslibrary.third.BaseInterstitialThirdParty;
import com.cahyocool.kafaadslibrary.third.BaseRewardThirdParty;
import com.cahyocool.kafaadslibrary.third.OnAdLoadListener;
import com.cahyocool.kafaadslibrary.third.admob.AdMobBanner;
import com.cahyocool.kafaadslibrary.third.admob.AdMobInterstitial;
import com.cahyocool.kafaadslibrary.third.admob.AdMobNative;
import com.cahyocool.kafaadslibrary.third.admob.AdMobReward;
import com.cahyocool.kafaadslibrary.third.admob.AppOpenManager;
import com.cahyocool.kafaadslibrary.third.facebook.FacebookBanner;
import com.cahyocool.kafaadslibrary.third.facebook.FacebookInterstitial;
import com.cahyocool.kafaadslibrary.third.facebook.FacebookNative;
import com.google.android.gms.ads.MobileAds;
import com.kafaads.kafaadslibrary.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class KafaAds implements OnAdLoadListener {
    private Context context;
    private final AdDataRepository adDataRepository;
    private FrameLayout container;
    private Timer mTimer;
    private int interstitialCount;
    private OnInterstitialAdLoadListener onInterstitialAdLoadListener;
    private OnRewardAdLoadListener onRewardAdLoadListener;
    private LoadTimerTask mTimerTask;
    private AdMobNative adMobNative;
    private AdMobBanner adMobBanner;
    private AdMobInterstitial adMobInterstitial;
    private AdMobReward adMobReward;
    private FacebookBanner facebookBanner;
    private FacebookNative facebookNative;
    private FacebookInterstitial facebookInterstitial;
    private LoadHandler mHandler;
    private Runnable mRunnable;
    private BaseInterstitialThirdParty currentBaseInterstitialThirdParty;
    private BaseRewardThirdParty currentBaseRewardThirdParty;
    private static final int FACEBOOK = 2;
    private static final int ADMOB = 3;
    private static final int HOUSE = 13;
    private static final int BANNER = 101;
    private static final int HALF_BANNER = 103;
    private static final int INTERSTITIAL = 107;
    private static final int REWARD = 110;
    private static final int NATIVE = 109;
    private static final int ENDING = 113;
    private static final int DEFAULT_DURATION_TIME = 3;
    public static final String TAG = "KafaAdsLibraryTest";
    public static Boolean TEST = false;
    private static KafaModel kafaModel;

    public static void setAds(KafaModel kafaModel) {
        KafaAds.kafaModel = kafaModel;
    }

    public static KafaModel getAds() {
        return kafaModel;
    }

    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    public static void initAd(Context context, String appId) {
        MobileAds.initialize(KafaAds.getActivity(context), appId);
    }

    public static void initOpenAd(Application app) {
        AppOpenManager appOpenManager = new AppOpenManager(app);
        Log.d(TAG, "load init openads");
    }

    private KafaAds(Builder builder) {
        context = builder.context;
        adDataRepository = builder.adDataRepository;
        container = builder.container;
        mTimer = new Timer();
        interstitialCount = builder.interstitialCount;
        onInterstitialAdLoadListener = builder.onInterstitialAdLoadListener;
        onRewardAdLoadListener = builder.onRewardAdLoadListener;
        TEST = builder.test_option;

        adDataRepository.getAll(new AdDataSource.OnGetAllSuccessListener() {
            @Override
            public void onSuccess(@NonNull List<Ad> adList) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "//onSuccess");
                }
                mHandler = new LoadHandler(Looper.getMainLooper());
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.handleMessage(new Message());
                    }
                };
            }
        }, new AdDataSource.OnGetAllFailureListener() {
            @Override
            public void onFailure() {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "//onFailure");
                }
                mHandler = new LoadHandler(Looper.getMainLooper());
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mHandler.handleMessage(new Message());
                    }
                };
            }
        });
    }

    @Override
    public void onAdLoaded(@NonNull Ad ad,
                           @Nullable Object data) {
        switch (ad.name.getCode() * ad.type.getCode()) {
            case ADMOB * INTERSTITIAL:
                currentBaseInterstitialThirdParty = adMobInterstitial;

                if (onInterstitialAdLoadListener != null) {
                    onInterstitialAdLoadListener.onAdLoaded();
                }
                break;
            case ADMOB * REWARD:
                currentBaseRewardThirdParty = adMobReward;

                if (onRewardAdLoadListener != null) {
                    onRewardAdLoadListener.onAdLoaded();
                }
                break;
            case FACEBOOK * INTERSTITIAL:
                currentBaseInterstitialThirdParty = facebookInterstitial;

                if (onInterstitialAdLoadListener != null) {
                    onInterstitialAdLoadListener.onAdLoaded();
                }
                break;
            case ADMOB * BANNER:
            case ADMOB * HALF_BANNER: {
                com.google.android.gms.ads.AdView adView =
                        (com.google.android.gms.ads.AdView) data;

                container.removeAllViews();
                container.addView(adView);
                break;
            }
            case ADMOB * NATIVE: {
//                UnifiedNativeAd unifiedNativeAd = (UnifiedNativeAd) data;
//                View view = LayoutInflater.from(container.getContext())
//                        .inflate(R.layout.view_admob_native, container, false);
//                ImageView image = view.findViewById(R.id.view_admob_image);
//                ImageView icon = view.findViewById(R.id.view_admob_icon);
//                TextView headLine = view.findViewById(R.id.view_admob_headLine);
//                TextView body = view.findViewById(R.id.view_admob_body);
//
//                image.setImageDrawable(unifiedNativeAd.getImages().get(0).getDrawable());
//                if (unifiedNativeAd.getIcon() != null) {
//                    icon.setVisibility(View.VISIBLE);
//                    icon.setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
//                } else {
//                    icon.setVisibility(View.GONE);
//                }
//                headLine.setText(unifiedNativeAd.getHeadline());
//                body.setText(unifiedNativeAd.getBody());
//
//                container.removeAllViews();
//                container.addView(view);
                break;
            }
            case FACEBOOK * BANNER:
            case FACEBOOK * HALF_BANNER: {
                com.facebook.ads.AdView adView = (com.facebook.ads.AdView) data;

                container.removeAllViews();
                container.addView(adView);
                break;
            }
            case FACEBOOK * NATIVE: {
//                NativeAd nativeAd = (NativeAd) data;
//                View view = LayoutInflater.from(container.getContext())
//                        .inflate(R.layout.view_facebook_native, container, false);
//                MediaView media = view.findViewById(R.id.view_facebook_media);
//                AdIconView icon = view.findViewById(R.id.view_facebook_icon);
//                TextView headLine = view.findViewById(R.id.view_facebook_headLine);
//                TextView body = view.findViewById(R.id.view_facebook_body);
//                List<View> clickableViews = new ArrayList<>();
//
//                container.removeAllViews();
//                container.addView(view);
//
//                nativeAd.downloadMedia();
//                headLine.setText(nativeAd.getAdvertiserName());
//                body.setText(nativeAd.getAdBodyText());
//                clickableViews.add(icon);
//                clickableViews.add(headLine);
//                clickableViews.add(body);
//
//                nativeAd.registerViewForInteraction(view, media, icon, clickableViews);
                break;
            }
            default:
                onAdFailedToLoad(ad);
                break;
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad) {
        if (ad.type == AdType.INTERSTITIAL &&
                --interstitialCount <= 0) {
            if (onInterstitialAdLoadListener != null) {
                onInterstitialAdLoadListener.onAdFailedToLoad();
            }
        } else if (ad.type == AdType.REWARD) {
          if (onRewardAdLoadListener != null) {
              onRewardAdLoadListener.onAdFailedToLoad();
          }
        } else {
            adDataRepository.next(new AdDataSource.OnNextSuccessListener() {
                @Override
                public void onSuccess(@NonNull Ad ad) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "/onAdFailedToLoad//onSuccess" +
                                "\nad type : " + ad.type +
                                "\nad name : " + ad.name);
                    }

                    load(ad);
                }
            }, new AdDataSource.OnNextFailureListener() {
                @Override
                public void onFailure() {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "/onAdFailedToLoad//onFailure");
                    }
                }
            });
        }
    }

    @Override
    public void onAdClosed(Ad ad) {
        if (ad.type == AdType.INTERSTITIAL &&
                --interstitialCount <= 0) {
            if (onInterstitialAdLoadListener != null) {
                onInterstitialAdLoadListener.onAdClosed();
            }
        } else if (ad.type == AdType.REWARD) {
            if (onRewardAdLoadListener != null) {
                onRewardAdLoadListener.onAdClosed();
            }
        }
    }

    public void load() {
        if (mTimerTask == null) {
            mTimerTask = new LoadTimerTask() {
                @Override
                public void run() {
                    super.run();
                    if (mHandler != null &&
                            mRunnable != null) {
                        mHandler.post(mRunnable);
                        mTimerTask.cancel();
                    }
                }
            };
        }

        if (mTimerTask.isRun()) {
            mTimerTask.cancel();
            mTimerTask = new LoadTimerTask() {
                @Override
                public void run() {
                    super.run();
                    if (mHandler != null &&
                            mRunnable != null) {

                        mHandler.post(mRunnable);
                        mTimerTask.cancel();
                    }
                }
            };
        }

        mTimer.schedule(mTimerTask, 0, TimeUnit.SECONDS.toMillis(DEFAULT_DURATION_TIME));
    }

    private void load(Ad ad) {
        switch (ad.name.getCode() * ad.type.getCode()) {
            case ADMOB * BANNER:
            case ADMOB * HALF_BANNER:
                if (adMobBanner == null) {
                    adMobBanner = new AdMobBanner(context, ad);

                    adMobBanner.setOnAdLoadListener(this);
                }

                adMobBanner.loadPreparedAd();
                break;
            case ADMOB * NATIVE:
                if (adMobNative == null) {
                    adMobNative = new AdMobNative(context, ad);

                    adMobNative.setOnAdLoadListener(this);
                }

                adMobNative.loadPreparedAd();
                break;
            case ADMOB * INTERSTITIAL:
                if (adMobInterstitial == null) {
                    adMobInterstitial = new AdMobInterstitial(context, ad);

                    adMobInterstitial.setOnAdLoadListener(this);
                }

                adMobInterstitial.loadPreparedAd();
                break;
            case ADMOB * REWARD:
                if (adMobReward == null) {
                    adMobReward = new AdMobReward(context, ad);

                    adMobReward.setOnAdLoadListener(this);
                }

                adMobReward.loadPreparedAd();
                break;
            case FACEBOOK * BANNER:
            case FACEBOOK * HALF_BANNER:
                if (facebookBanner == null) {
                    facebookBanner = new FacebookBanner(context, ad);

                    facebookBanner.setOnAdLoadListener(this);
                }

                facebookBanner.loadPreparedAd();
                break;
            case FACEBOOK * NATIVE:
                if (facebookNative == null) {
                    facebookNative = new FacebookNative(context, ad);

                    facebookNative.setOnAdLoadListener(this);
                }

                facebookNative.loadPreparedAd();
                break;
            case FACEBOOK * INTERSTITIAL:
                if (facebookInterstitial == null) {
                    facebookInterstitial = new FacebookInterstitial(context, ad);

                    facebookInterstitial.setOnAdLoadListener(this);
                }

                facebookInterstitial.loadPreparedAd();
                break;
        }
    }

    public void showInterstitial() {
        if (currentBaseInterstitialThirdParty != null) {
            currentBaseInterstitialThirdParty.showPreparedAd();
        }
    }

    public void showReward() {
        if (currentBaseRewardThirdParty != null) {
            currentBaseRewardThirdParty.showPreparedAd();
        }
    }

    public void close() {
        if (mTimerTask != null
                && mTimerTask.isRun()) {
            mTimerTask.cancel();
        }

        mTimerTask = null;
    }

    public static class Builder {
        private Context context;
        private List<Ad> adList;
        private FrameLayout container;
        private AdDataRepository adDataRepository;
        private int interstitialCount;
        private OnInterstitialAdLoadListener onInterstitialAdLoadListener;
        private OnRewardAdLoadListener onRewardAdLoadListener;
        private Boolean test_option = false;

        public Builder(@NonNull Context context) {
            this.context = context;
            adList = new ArrayList<>();
            interstitialCount = 0;
        }

        public Builder setBaseUrl(@Nullable String baseUrl) {
            adDataRepository = new AdDataRepository(baseUrl);
            return this;
        }

        public Builder setAd(@NonNull Ad ad) {
            adList.add(ad);

            return this;
        }

        public Builder setContainer(@NonNull FrameLayout container) {
            this.container = container;

            return this;
        }

        public Builder setOnInterstitialAdLoadListener(OnInterstitialAdLoadListener onInterstitialAdLoadListener) {
            this.onInterstitialAdLoadListener = onInterstitialAdLoadListener;

            return this;
        }

        public Builder setOnRewardAdLoadListener(OnRewardAdLoadListener onRewardAdLoadListener) {
            this.onRewardAdLoadListener = onRewardAdLoadListener;

            return this;
        }

        public Builder setAdmangerTest(Boolean flag) {
            this.test_option = flag;
            return this;
        }

        public KafaAds build() {
            boolean valid = true;
            for (Ad ad : adList) {
                if ((ad.type == AdType.BANNER ||
                        ad.type == AdType.NATIVE) &&
                        container == null) {
                    valid = false;
                    break;
                }

                if (ad.type == AdType.INTERSTITIAL &&
                        container != null) {
                    valid = false;
                    break;
                } else {
                    interstitialCount++;
                }

                if (adDataRepository == null) {
                    adDataRepository = new AdDataRepository(null);
                }

                adDataRepository.add(ad);
            }
            if (adDataRepository == null) {
                valid = false;
            }
            return valid ? new KafaAds(this) : null;
        }
    }

    class LoadTimerTask extends TimerTask {
        private boolean run = false;

        @Override
        public void run() {
            run = true;
        }

        @Override
        public boolean cancel() {
            run = false;

            return super.cancel();
        }

        boolean isRun() {
            return run;
        }
    }

    class LoadHandler extends Handler {
        private final WeakReference<Looper> looper;

        LoadHandler(Looper looper) {
            this.looper = new WeakReference<>(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            adDataRepository.next(new AdDataSource.OnNextSuccessListener() {
                @Override
                public void onSuccess(@NonNull Ad ad) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "//handleMessage//onSuccess" +
                                "\nad type : " + ad.type +
                                "\nad name : " + ad.name);
                    }
                    load(ad);
                }
            }, new AdDataSource.OnNextFailureListener() {
                @Override
                public void onFailure() {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "//handleMessage//onFailure");
                    }
                    onAdFailedToLoad(null);
                }
            });
        }
    }
}
