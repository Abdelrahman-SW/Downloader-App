package com.yremhl.ystgdh.Utilites;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.yremhl.ystgdh.Models.Admob;
import com.yremhl.ystgdh.network.ApiClient;
import com.yremhl.ystgdh.network.ApiService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import io.reactivex.disposables.CompositeDisposable;

public class AdsUtilities {

    public static InterstitialAd downloadInterstitialAd ;
    public static InterstitialAd reDownloadInterstitialAd ;
    public static InterstitialAd play_Pause_downloadInterstitialAd ;
    public static Admob _admob  ;
    private static ApiService apiService ;
    public static final CompositeDisposable disposable = new CompositeDisposable();

    public static ApiService getApiService(Context context) {
        if (apiService == null) apiService = ApiClient.getClient(context).create(ApiService.class);
        return apiService;
    }

    public static void createBannerAd (AppCompatActivity activity , ViewGroup bannerContainer){
        if (_admob == null) {
            return;
        }
        if (_admob.getBanner() == null) {
            return;
        }
        AdManagerAdView adView = new AdManagerAdView(activity);
        adView.setAdSizes(getAdSize(activity));
        adView.setAdUnitId(_admob.getBanner());
        adView.setAdListener(new AdListener() {
                                 @Override
                                 public void onAdLoaded() {
                                     Log.i("ab_do"  , "onAdLoaded");
                                     if (adView.getParent() != null) {
                                         ((ViewGroup)adView.getParent()).removeView(adView);
                                     }
                                     bannerContainer.addView(adView);
                                     bannerContainer.setVisibility(View.VISIBLE);

                                 }
                                 @Override
                                 public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                     super.onAdFailedToLoad(loadAdError);
                                     bannerContainer.setVisibility(View.GONE);
                                 }
                             }

        );
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static void createDownloadInterstitialAd(AppCompatActivity activity) {
        if (_admob == null) {
            return;
        }
            if (_admob.getInterstitial() == null) {
                return;
            }
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, _admob.getInterstitial(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                             downloadInterstitialAd = interstitialAd ;
                             Utilities.PrintLog("onAdLoadedI");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            downloadInterstitialAd = null;
                            Utilities.PrintLog("onAdFailedToLoadI " + loadAdError.getMessage());
                        }
                    });
        }

    public static void createReDownloadInterstitialAd(AppCompatActivity activity) {
        if (_admob == null) {
            return;
        }
        if (_admob.getInterstitial() == null) {
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, _admob.getInterstitial(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        reDownloadInterstitialAd = interstitialAd ;
                        Utilities.PrintLog("onAdLoadedI");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        reDownloadInterstitialAd = null;
                        Utilities.PrintLog("onAdFailedToLoadI " + loadAdError.getMessage());
                    }
                });
    }

    public static void createPlayPauseDownloadInterstitialAd(AppCompatActivity activity) {
        if (_admob == null) {
            return;
        }
        if (_admob.getInterstitial() == null) {
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, _admob.getInterstitial(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        play_Pause_downloadInterstitialAd = interstitialAd ;
                        Utilities.PrintLog("onAdLoadedI");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        play_Pause_downloadInterstitialAd = null;
                        Utilities.PrintLog("onAdFailedToLoadI " + loadAdError.getMessage());
                    }
                });
    }


    private static AdSize getAdSize(AppCompatActivity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
