package com.yremhl.ystgdh.UI.ScheduleDownloads;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yremhl.ystgdh.Downloader.Downloader;
import com.yremhl.ystgdh.Fragments.DatePickerFragment;
import com.yremhl.ystgdh.Fragments.TimePickerFragment;
import com.yremhl.ystgdh.Models.Admob;
import com.yremhl.ystgdh.Models.ScheduleDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.AdsUtilities;
import com.yremhl.ystgdh.Utilites.AlarmHelper;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.adapters.ScheduleDownloadAdapter;
import com.yremhl.ystgdh.databinding.ActivityScheduleDownloadsBinding;
import com.yremhl.ystgdh.databinding.ScheduleDownloadOptionsBinding;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lib.folderpicker.FolderPicker;


public class ScheduleDownloadsActivity extends AppCompatActivity implements ScheduleDownloadAdapter.OnItemClicked, TimePickerDialog.OnTimeSetListener , DatePickerDialog.OnDateSetListener {
    ActivityScheduleDownloadsBinding binding ;
    ScheduleDownloadsViewModel viewModel ;
    ScheduleDownloadAdapter adapter ;
    ScheduleDownload clickedScheduleDownload ;
    BottomSheetDialog optionsBottomSheetDialog , downloadBottomSheetDialog ;
    ScheduleDownloadOptionsBinding optionsBinding ;
    ScheduleDownloadsActivity activity ;
    Calendar calendar ;
    private TextView time , date ;
    EditText downloadUrl ;
    Button download ;
    TextView location ;
    TextView downloadHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Utilities.updateTheme(this);
//        Utilities.updateLocale(this);
        super.onCreate(savedInstanceState);
        binding  = ActivityScheduleDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    public void init () {
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
         viewModel = new ViewModelProvider(this).get(ScheduleDownloadsViewModel.class);
         viewModel.getAllScheduleDownloads().observe(this, new Observer<List<ScheduleDownload>>() {
             @Override
             public void onChanged(List<ScheduleDownload> scheduleDownloads) {
                 updateAdapter(scheduleDownloads);
             }
         });
    }

    private void updateAdapter(List<ScheduleDownload> scheduleDownloads) {
           adapter.submitList(scheduleDownloads);
    }

    private void initRecycleView() {
         adapter = new ScheduleDownloadAdapter(this);
         binding.recycleView.setItemAnimator(null);
         binding.recycleView.setAdapter(adapter);
    }

    @Override
    public void onClick(ScheduleDownload scheduleDownload) {
          clickedScheduleDownload = scheduleDownload ;
          showOptionsDialog();
    }

    private void showOptionsDialog() {
        if (optionsBottomSheetDialog == null || optionsBinding == null) {
            optionsBinding = ScheduleDownloadOptionsBinding.inflate(getLayoutInflater());
            optionsBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
            View view = optionsBinding.getRoot();
//
            optionsBinding.downloadNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // cancel alarm and start download now
                    startDownloadingNow();
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.copyStringToClipboard(activity , clickedScheduleDownload.getUrl()
                            , binding.getRoot());
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.editDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editScheduleDownload();
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.deleteDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteScheduleDownload();
                    optionsBottomSheetDialog.dismiss();
                    Snackbar.make(binding.getRoot() , R.string.cancel_done, Snackbar.LENGTH_SHORT).show();
                }
            });
            optionsBottomSheetDialog.setContentView(view);
        }
        optionsBinding.FileName.setText(clickedScheduleDownload.getFileName());
        optionsBottomSheetDialog.show();
    }

    private void startDownloadingNow() {
        deleteScheduleDownload();
        Downloader downloader = new Downloader(activity);
        downloader.startDownloading(clickedScheduleDownload.getUrl() , clickedScheduleDownload.getPath() , clickedScheduleDownload.getFileName());
        Snackbar.make(binding.getRoot() , R.string.download_started, Snackbar.LENGTH_SHORT).show();
        finish();
    }

    private void showDownloadDialog() {
        if (downloadBottomSheetDialog !=null) {
            updateDownloadDialogData();
            downloadBottomSheetDialog.show();
            return;
        }
        calendar = (Calendar) clickedScheduleDownload.getCalendar().clone();
        downloadBottomSheetDialog = new BottomSheetDialog(this , R.style.bottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.download_from_url, binding.getRoot(), false);
        downloadUrl = view.findViewById(R.id.downloadEditTxt);
        download = view.findViewById(R.id.startDownloadBtn);
        download.setText(R.string.edit);
        location = view.findViewById(R.id.download_location);
        time = view.findViewById(R.id.download_time);
        date = view.findViewById(R.id.download_date);
        downloadHint = view.findViewById(R.id.download_hint);
        //location.setText(Constant.DEFAULT_DOWNLOAD_PATH);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDownloadLocation();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit the schedule Download with the new data
                updateScheduleDownload();

            }
        });
        downloadBottomSheetDialog.setContentView(view);
        updateDownloadDialogData();
        downloadBottomSheetDialog.show();
    }

    private void updateScheduleDownload() {
        clickedScheduleDownload.setCalendar(calendar);
        clickedScheduleDownload.setUrl(downloadUrl.getText().toString());
        clickedScheduleDownload.setPath(location.getText().toString());
        clickedScheduleDownload.setType(Utilities.getMimeType(downloadUrl.getText().toString()));
        clickedScheduleDownload.setFileName(URLUtil.guessFileName(downloadUrl.getText().toString() , null , null));
        viewModel.updateScheduleDownload(clickedScheduleDownload);
        AlarmHelper.CancelAlarm(this , clickedScheduleDownload.getId());
        AlarmHelper.prepareAlarm(this , clickedScheduleDownload);
        downloadBottomSheetDialog.dismiss();
        Snackbar.make(binding.recycleView ,"تم التعديل بنجاح" , Snackbar.LENGTH_SHORT).show();
    }

    private void updateDownloadDialogData() {
        calendar = (Calendar) clickedScheduleDownload.getCalendar().clone();
        downloadUrl.setText(clickedScheduleDownload.getUrl());
        time.setText(Utilities.FormatToTime(calendar , activity));
        date.setText(Utilities.FormatToDate(calendar));
        location.setText(clickedScheduleDownload.getPath());
    }

    private void editScheduleDownload() {
        showDownloadDialog();
    }

    private void deleteScheduleDownload() {
        // first cancel alarm
        AlarmHelper.CancelAlarm(this , clickedScheduleDownload.getId());
        // remove schedule download from database
        viewModel.deleteScheduleDownload(clickedScheduleDownload);
    }

    private void selectDownloadLocation() {
        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent , 1);
    }

    private void showDateDialog() {
        DatePickerFragment datePickerFragment = DatePickerFragment.getInstance(Calendar.getInstance());
        datePickerFragment.show(getSupportFragmentManager() , "date");

    }

    private void showTimeDialog() {
        TimePickerFragment timePickerFragment = TimePickerFragment.getInstance(Calendar.getInstance());
        timePickerFragment.show(getSupportFragmentManager() , "time");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date.setText(Utilities.FormatToDate(calendar));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        time.setText(Utilities.FormatToTime(calendar , this));
    }

}