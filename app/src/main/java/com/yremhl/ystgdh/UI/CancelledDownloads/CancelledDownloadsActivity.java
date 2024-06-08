package com.yremhl.ystgdh.UI.CancelledDownloads;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yremhl.ystgdh.Models.Admob;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.UI.ActiveDownloads.MainActivity;
import com.yremhl.ystgdh.Utilites.AdsUtilities;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.adapters.CancelledDownloadAdapter;
import com.yremhl.ystgdh.databinding.ActivityCancelledDownloadsBinding;
import com.yremhl.ystgdh.databinding.CancelledDownloadOptionsBinding;
import com.yremhl.ystgdh.network.ApiService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CancelledDownloadsActivity extends AppCompatActivity implements CancelledDownloadAdapter.OnItemClicked {
    ActivityCancelledDownloadsBinding binding ;
    CancelledDownloadViewModel viewModel ;
    CancelledDownload clickedCancelledDownload ;
    CancelledDownloadAdapter adapter ;
    BottomSheetDialog  optionsBottomSheetDialog ;
    CancelledDownloadOptionsBinding optionsBinding ;
    AppCompatActivity activity ;
    ApiService apiService ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Utilities.updateTheme(this);
//        Utilities.updateLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityCancelledDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
       
    }
    
    private void init() {
        activity = this ;
        initAds();
        Utilities.initToolbar(this , binding.toolbar , true);
        initRecycleView();
        initViewModel();
    }


    private void createAds() {
        AdsUtilities.createReDownloadInterstitialAd(activity);
        AdsUtilities.createBannerAd(activity , binding.bannerContainer);
    }

    private void showReDownloadAd() {
        if (Utilities.adsIsRemoved(activity)) return;
        if (AdsUtilities.reDownloadInterstitialAd != null) {
            AdsUtilities.reDownloadInterstitialAd.show(activity);
        }
        AdsUtilities.createReDownloadInterstitialAd(activity);
    }

    private void initAds() {
        if (!Utilities.adsIsRemoved(this)) {
            if (AdsUtilities._admob == null) {
                AdsUtilities.disposable.add(
                        AdsUtilities.getApiService(activity)
                                .getAdmob()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<Admob>() {
                                    @Override
                                    protected void onStart() {
                                        super.onStart();
                                        Log.i("ab_do", "onStart adMob");
                                    }

                                    @Override
                                    public void onSuccess(Admob admob) {
                                        Log.i("ab_do", "onSuccess adMob");
                                        AdsUtilities._admob = admob;
                                        ApplicationInfo ai = null;
                                        try {
                                            Utilities.PrintLog("id " + admob.getApp_id());
                                            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", admob.getApp_id());//you can replace your
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                            Utilities.PrintLog(e.getMessage());
                                        }
                                        MobileAds.initialize(activity, initializationStatus -> {
                                            createAds();
                                        });
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i("ab_do", "error admob " + e.getMessage());
                                    }
                                })
                );
            }
            else {
                createAds();
            }
        }
    }

    private void initRecycleView() {
        adapter = new CancelledDownloadAdapter(this);
        binding.recycleView.setItemAnimator(null);
        binding.recycleView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CancelledDownloadViewModel.class);
        viewModel.getCancelledDownloads().observe(this, new Observer<List<CancelledDownload>>() {
            @Override
            public void onChanged(List<CancelledDownload> cancelledDownloads) {
                adapter.submitList(cancelledDownloads);
            }
        });
    }

    @Override
    public void onClick(CancelledDownload cancelledDownload) {
        clickedCancelledDownload = cancelledDownload ;
        showOptionsDialog();
    }

    private void showOptionsDialog() {
        if (optionsBottomSheetDialog == null || optionsBinding == null) {
            optionsBinding = CancelledDownloadOptionsBinding.inflate(getLayoutInflater());
            optionsBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
            View view = optionsBinding.getRoot();
//            optionsBinding.info.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showFileInfo(clickedCancelledDownload);
//                    optionsBottomSheetDialog.dismiss();
//                }
//            });
            optionsBinding.urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyUrl(clickedCancelledDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.deleteDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDownload(clickedCancelledDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.reDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reDownload(clickedCancelledDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBottomSheetDialog.setContentView(view);
        }
        optionsBinding.FileName.setText(clickedCancelledDownload.getFileName());
        optionsBottomSheetDialog.show();
    }

    private void copyUrl(CancelledDownload clickedCancelledDownload) {
        if (clickedCancelledDownload == null) return;
        Utilities.copyStringToClipboard(this , clickedCancelledDownload.getUrl()
        , binding.recycleView);
    }

    private void reDownload(CancelledDownload clickedCancelledDownload) {
        viewModel.deleteCancelledDownload(clickedCancelledDownload);
        Intent intent = new Intent(getBaseContext() , MainActivity.class);
        intent.setAction(Constant.RE_DOWNLOAD_ACTION);
        intent.putExtra(Constant.RE_DOWNLOAD_EXTRA , clickedCancelledDownload);
        startActivity(intent);
        finish();
        showReDownloadAd();
    }

    private void deleteDownload(CancelledDownload clickedCancelledDownload) {
        viewModel.deleteCancelledDownload(clickedCancelledDownload);
    }

    private void showFileInfo(CancelledDownload clickedCancelledDownload) {
    }

}