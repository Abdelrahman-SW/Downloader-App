package com.yremhl.ystgdh.UI.CompletedDownloads;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yremhl.ystgdh.Downloader.DownloaderHelper;
import com.yremhl.ystgdh.Models.Admob;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.AdsUtilities;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.adapters.CompletedDownloadAdapter;
import com.yremhl.ystgdh.databinding.ActivityCompletedDownloadsBinding;
import com.yremhl.ystgdh.databinding.CompletedDownloadOptionsBinding;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class CompletedDownloadsActivity extends AppCompatActivity implements CompletedDownloadAdapter.OnItemClicked {
    ActivityCompletedDownloadsBinding binding ;
    CompletedDownloadAdapter adapter ;
    CompletedDownloadsViewModel viewModel ;
    BottomSheetDialog optionsBottomSheetDialog ;
    CompletedDownloadOptionsBinding optionsBinding ;
    CompletedDownload clickedCompletedDownload ;
    CompletedDownloadsActivity activity ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Utilities.updateTheme(this);
//        Utilities.updateLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityCompletedDownloadsBinding.inflate(getLayoutInflater());
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
        AdsUtilities.createBannerAd(activity , binding.bannerContainer);
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

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CompletedDownloadsViewModel.class);
        viewModel.getAllCompletedDownloads().observe(this, new Observer<List<CompletedDownload>>() {
            @Override
            public void onChanged(List<CompletedDownload> completedDownloads) {
                adapter.submitList(completedDownloads);
            }
        });
    }

    private void initRecycleView() {
        adapter = new CompletedDownloadAdapter(this);
        binding.recycleView.setItemAnimator(null);
        binding.recycleView.setAdapter(adapter);
        // set adapter
    }

    @Override
    public void onClick(CompletedDownload completedDownload) {
        clickedCompletedDownload = completedDownload ;
        showOptionsDialog();
    }



    private void showOptionsDialog() {
        if (optionsBottomSheetDialog == null || optionsBinding == null) {
            optionsBinding = CompletedDownloadOptionsBinding.inflate(getLayoutInflater());
            optionsBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
            View view = optionsBinding.getRoot();
//            optionsBinding.info.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showFileInfo(clickedCompletedDownload);
//                    optionsBottomSheetDialog.dismiss();
//                }
//            });
            optionsBinding.shareDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareFile(clickedCompletedDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.copyStringToClipboard(activity , clickedCompletedDownload.getUrl()
                    , binding.getRoot());
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.openFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile(clickedCompletedDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDownload(clickedCompletedDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBottomSheetDialog.setContentView(view);
        }
        optionsBinding.FileName.setText(clickedCompletedDownload.getFileName());
        optionsBottomSheetDialog.show();
    }

    private void shareFile(CompletedDownload clickedCompletedDownload) {
            new ShareCompat.IntentBuilder(this)
                    .setStream(Uri.parse(clickedCompletedDownload.getPath()))
                    .setType("video/mp4")
                    .startChooser();
    }

    private void deleteDownload(CompletedDownload clickedCompletedDownload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.AlertDialogTheme);
        builder.setTitle(R.string.sure);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                   boolean success = Utilities.deleteFile(activity , clickedCompletedDownload.getActualPath() , clickedCompletedDownload.getFileName());
                   if (success) {
                       viewModel.deleteCompletedDownload(clickedCompletedDownload);
                       Snackbar.make(binding.getRoot(), getString(R.string.file_deleted), Snackbar.LENGTH_SHORT).show();
                       DownloaderHelper.removeRequestByID(activity , clickedCompletedDownload.getRequestID());
                   }

            }
        });
        builder.setNegativeButton(getString(R.string.no_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFileInfo(CompletedDownload clickedCompletedDownload) {

    }

    private void openFile(CompletedDownload completedDownload) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setData(Uri.parse(completedDownload.getPath());
            intent.setDataAndType(Uri.parse(completedDownload.getPath()), completedDownload.getType());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setType("video/mp4");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // no Activity to handle this kind of files
            Utilities.PrintLog(e.getMessage());
            Toast.makeText(this, getString(R.string.error_open_app), Toast.LENGTH_SHORT).show();
        }
    }
}