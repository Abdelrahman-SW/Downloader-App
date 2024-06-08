package com.yremhl.ystgdh.UI.ActiveDownloads;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.yremhl.ystgdh.Downloader.Downloader;
import com.yremhl.ystgdh.Fragments.DatePickerFragment;
import com.yremhl.ystgdh.Fragments.TimePickerFragment;
import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.Admob;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.UI.CompletedDownloads.CompletedDownloadsActivity;
import com.yremhl.ystgdh.UI.ScheduleDownloads.ScheduleDownloadsActivity;
import com.yremhl.ystgdh.UI.Settings.SettingsActivity;
import com.yremhl.ystgdh.Utilites.AdsUtilities;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.adapters.ActiveDownloadsAdapter;
import com.yremhl.ystgdh.databinding.ActiveDownloadOptionsBinding;
import com.yremhl.ystgdh.databinding.ActivityMainBinding;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lib.folderpicker.FolderPicker;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, ActiveDownloadsAdapter.OnPausePlayBtnClicked, ActiveDownloadsAdapter.OnItemClicked {

    ActivityMainBinding binding;
    private boolean IsMoreBtnClicked = false;

    private ConsentInformation consentInformation;
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private AppCompatActivity activity;
    private BottomSheetDialog downloadBottomSheetDialog, optionsBottomSheetDialog;
    private boolean dateSet = false;
    private boolean timeSet = false;
    private TextView time, date;
    private Calendar calendar;
    EditText downloadUrl;
    Button download;
    TextView location;
    TextView downloadHint;
    TextView fileName;
    Downloader downloader;
    ActiveDownloadsViewModel viewModel;
    ActiveDownloadsAdapter activeDownloadsAdapter;
    ActiveDownloadOptionsBinding optionsBinding;
    ActiveDownload clickedActiveDownload;
    PowerMenu powerMenu;
    SharedPreferences sharedPreferences;
    String orderValue;
    boolean isFromAnime;
    Intent animeIntent;
    String _fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Utilities.updateTheme(this);
//        Utilities.saveDeviceLanguage(this);
//        Utilities.updateLocale(this);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //binding.BottomNavigationView.getMenu().findItem(2).setEnabled(false);
        setContentView(binding.getRoot());
        if (!Utilities.checkIfStoragePermissionGranted(this))
            Utilities.requestStoragePermission(this);
        init();
    }


    private void init() {
        activity = this;
        isFromAnime = false;

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("ab_do", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initAds();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("ab_do", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initAds();
        }

        initSharedPreference();
        Utilities.updateStopServiceValue(this, false);
        initAppBar();
        initBottomBar();
        Utilities.initToolbar(this, binding.toolbar, false);
        //initImageSlider();
        prepareNavigationDrawer();
        initListeners();
        initRecycleView();
        initViewModel(orderValue);
        initDownloader();
        checkActions();
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    private void createAds() {
        AdsUtilities.createDownloadInterstitialAd(activity);
        AdsUtilities.createPlayPauseDownloadInterstitialAd(activity);
        AdsUtilities.createBannerAd(activity, binding.bannerContainer);
    }

    private void initAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
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
            } else {
                createAds();
            }
        }
    }


    private void initSharedPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        orderValue = sharedPreferences.getString(Constant.ORDER_BY_KEY, Constant.ORDER_BY_DATE_DESC);
    }

    @Override
    protected void onStart() {
        binding.BottomNavigationView.setSelectedItemId(R.id.anchor);
        super.onStart();
    }

    private void initBottomBar() {
        binding.BottomNavigationView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        binding.BottomNavigationView.setBackground(null);
        binding.BottomNavigationView.setSelectedItemId(R.id.anchor);
        binding.BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shutDown) {
                    finishAffinity();
                    return true;
                } else if (item.getItemId() == R.id.settings) {
                    startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.popupMenu) {
                    //show popup menu
                    showPopMenu();
                    return true;
                } else if (item.getItemId() == R.id.completed_downloads) {
                    startActivity(new Intent(getBaseContext(), CompletedDownloadsActivity.class));
                    return true;

                }
                if (item.getItemId() != R.id.anchor) {
                    binding.BottomNavigationView.setSelectedItemId(R.id.anchor);
                }
                return false;
            }
        });
    }

    private void showPopMenu() {
        Utilities.updateLocale(activity);
//        PopupMenu popup = new PopupMenu(this , binding.anchorPop);
//        popup.inflate(R.menu.popup_menu);
//        try {
//            Field[] fields = popup.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                if ("mPopup".equals(field.getName())) {
//                    field.setAccessible(true);
//                    Object menuPopupHelper = field.get(popup);
//                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
//                            .getClass().getName());
//                    Method setForceIcons = classPopupHelper.getMethod(
//                            "setForceShowIcon", boolean.class);
//                    setForceIcons.invoke(menuPopupHelper, true);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        popup.show();
        if (powerMenu == null) {
            TypedValue typedValuePopText = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.popMenuTextColor, typedValuePopText, true);
            @ColorInt int textColor = typedValuePopText.data;


            powerMenu = new PowerMenu.Builder(this)
                    .addItem(new PowerMenuItem(getString(R.string.play_all), R.drawable.ic_play__3_custom)) // add an item.
                    .addItem(new PowerMenuItem(getString(R.string.pauseAll), R.drawable.ic_pause_custom)) // aad an item list.
                    .addItem(new PowerMenuItem(getString(R.string.deleteAll), R.drawable.ic_baseline_delete_24_custom)) // aad an item list.
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(15f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setTextColor(textColor)
                    .setTextGravity(Gravity.START)
                    .setPadding(12)
                    .setTextSize(12)
                    .setIconPadding(8)
                    .setSelectedTextColor(Color.WHITE)
                    .setMenuColor(Color.WHITE)
                    .setIconSize(24)
                    .setDividerHeight(10)
                    .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                        @Override
                        public void onItemClick(int position, PowerMenuItem item) {
                            powerMenu.dismiss();
                            if (position == 0) {
                                // play All
                                downloader.resumeAllDownload();
                                showResumeAd();
                            } else if (position == 1) {
                                // pause All
                                downloader.pauseAllDownload();
                            }
                            if (position == 2) {
                                // remove All
                                downloader.cancelAllDownload();
                                viewModel.deleteAllActiveDownloads();
                            }
                        }
                    })
                    .build();
        }
        try {
            powerMenu.showAsDropDown(binding.anchorPop);
        } catch (Exception e) {
            Log.i("ab_do", e.getMessage());
        }
    }

    private void checkActions() {
        int flags = getIntent().getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
            // The activity was launched from history
            Utilities.PrintLog("The activity was launched from history");
            return;
        }
        if (getIntent().getAction() == null) return;
        Utilities.PrintLog(getIntent().getAction());
        if (getIntent().getAction().equals(Constant.RE_DOWNLOAD_ACTION)) {
            CancelledDownload cancelledDownload = getIntent().getParcelableExtra(Constant.RE_DOWNLOAD_EXTRA);
            if (cancelledDownload == null) return;
            Utilities.PrintLog("url " + cancelledDownload.getUrl() + " " + "path" + cancelledDownload.getPath());
            downloader.startDownloading(cancelledDownload.getUrl(), cancelledDownload.getActualPath(), cancelledDownload.getFileName());
        } else if (getIntent().getAction().equals(Constant.DOWNLOAD_ANIME_ACTION)) {
            // download anime
            DownloadAnime(getIntent());
        } else if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            downloadNewLink(getIntent().getData().toString());
        }
    }


    private void downloadNewLink(String url) {
        String fileName = URLUtil.guessFileName(url, null, null);
        if (fileName.endsWith(".bin")) fileName = fileName.replace(".bin", ".mp4");
        showDownloadDialog(false, url, fileName);
    }

    private void DownloadAnime(Intent intent) {
        isFromAnime = true;
        animeIntent = intent;
        String url = intent.getStringExtra(Constant.ANIME_EXTRA_URL);
        String file_name = intent.getStringExtra(Constant.ANIME_EXTRA_FILE_NAME);
        //downloader.startDownloading(url, Constant.DEFAULT_DOWNLOAD_PATH, file_name);
        showDownloadDialog(false, url, file_name);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() == null) return;
        if (intent.getAction().equals(Constant.DOWNLOAD_ANIME_ACTION)) {
            // download anime
            DownloadAnime(intent);
        } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            // download external link
            downloadNewLink(intent.getData().toString());
        }
    }

    private void initRecycleView() {
        activeDownloadsAdapter = new ActiveDownloadsAdapter(this);
        binding.recycleView.setAdapter(activeDownloadsAdapter);
        binding.recycleView.setItemAnimator(null);
    }

    private void initDownloader() {
        downloader = new Downloader(this);
    }

    private void initViewModel(String orderValue) {
        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(ActiveDownloadsViewModel.class);
        observeTheCorrectList(orderValue);
    }

    private void observeTheCorrectList(String orderValue) {
        switch (orderValue) {
            case Constant.ORDER_BY_DATE_ASC:
                viewModel.getActiveDownloadsByTimeAsc().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
            case Constant.ORDER_BY_NAME_ASC:
                viewModel.getActiveDownloadsByNameAsc().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
            case Constant.ORDER_BY_NAME_DESC:
                viewModel.getActiveDownloadsByNameDesc().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
            case Constant.ORDER_BY_SIZE_ASC:
                viewModel.getActiveDownloadsBySizeAsc().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
            case Constant.ORDER_BY_SIZE_DESC:
                viewModel.getActiveDownloadsBySizeDesc().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
            default:
                viewModel.getActiveDownloads().observe(this, new Observer<List<ActiveDownload>>() {
                    @Override
                    public void onChanged(List<ActiveDownload> activeDownloads) {
                        updateAdapter(activeDownloads);
                    }
                });
                break;
        }
    }

    private void updateAdapter(List<ActiveDownload> activeDownloads) {
        if (activeDownloads == null) return;
        activeDownloadsAdapter.submitList(activeDownloads);
        if (activeDownloads.size() == 0) {
            // empty list
            setAnimation();
            binding.noActiveDownload.setVisibility(View.VISIBLE);

        } else binding.noActiveDownload.setVisibility(View.GONE);
    }

    private void setAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.addTarget(binding.noActiveDownload);
            slide.setDuration(800);
            TransitionManager.beginDelayedTransition(binding.drawerLayout, slide);
        }
    }

    private void initListeners() {
//        binding.recycleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (IsMoreBtnClicked) {
//                    CollapseMoreBtn();
//                }
//                return true ;
//            }
//        });
        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsMoreBtnClicked) {
                    CollapseMoreBtn();
                } else {
                    ExpandMoreBtn();
                }
            }
        });
        binding.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadDialog(false, null, null);
                CollapseMoreBtn();
            }
        });
        binding.scheduleDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadDialog(true, null, null);
                CollapseMoreBtn();
            }
        });
    }

    private void ExpandMoreBtn() {
        binding.downloadBtn.setVisibility(View.VISIBLE);
        binding.scheduleDownloadBtn.setVisibility(View.VISIBLE);
        binding.moreBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate_more_btn_expand));
        binding.downloadBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.expand_btns));
        binding.scheduleDownloadBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.expand_btns));
        IsMoreBtnClicked = true;
    }

    private void CollapseMoreBtn() {
        binding.moreBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate_more_btn_collapse));
        binding.scheduleDownloadBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.collapse_btns));
        binding.downloadBtn.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.collapse_btns));
        binding.scheduleDownloadBtn.setVisibility(View.GONE);
        binding.downloadBtn.setVisibility(View.GONE);
        IsMoreBtnClicked = false;
    }

    private void initAppBar() {
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
//                Utilities.PrintLog("verticalOffset " + verticalOffset);
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarTxt.setVisibility(View.VISIBLE);
                } else {
                    binding.toolbarTxt.setVisibility(View.GONE);
                }
            }
        });
    }

//    private void initImageSlider() {
//        List<SlideModel> slideModels = new ArrayList<>();
//        if (Locale.getDefault().getLanguage().equals("en")) {
//            slideModels.add(new SlideModel(R.drawable.download_files_img_en_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_video_img_en_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_musics_img_en_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_pdf_img_en_white, ScaleTypes.CENTER_CROP));
//        }
//        else {
//            slideModels.add(new SlideModel(R.drawable.download_files_img_ar_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_videos_img_ar_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_musics_img_ar_white, ScaleTypes.CENTER_CROP));
//            slideModels.add(new SlideModel(R.drawable.download_pdf_img_ar_white, ScaleTypes.CENTER_CROP));
//        }
//        //binding.imageSlider.setImageList(slideModels);
//    }

    private void prepareNavigationDrawer() {
        NavigationMenuView navigationMenuView = (NavigationMenuView) binding.navDrawer.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        binding.navDrawer.setItemIconTintList(null);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        binding.navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return Utilities.handleNavigationItemSelected(binding.drawerLayout, activity, item.getItemId());
            }
        });
    }

    private void showDownloadDialog(boolean isSchedule, String url, String fileNameTxt) {
        if (downloadBottomSheetDialog != null) {
            if (downloadBottomSheetDialog.isShowing()) downloadBottomSheetDialog.dismiss();
            checkTypeOfDownload(isSchedule);
            updateDialog(url, fileNameTxt);
            downloadBottomSheetDialog.show();
            return;
        }
        calendar = Calendar.getInstance();
        downloadBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.download_from_url, binding.getRoot(), false);
        downloadUrl = view.findViewById(R.id.downloadEditTxt);
        download = view.findViewById(R.id.startDownloadBtn);
        location = view.findViewById(R.id.download_location);
        time = view.findViewById(R.id.download_time);
        date = view.findViewById(R.id.download_date);
        fileName = view.findViewById(R.id.fileNameEditTxt);
        downloadHint = view.findViewById(R.id.download_hint);
        ImageView fromClipboard = view.findViewById(R.id.fromClipboard);
        ImageView autoFill = view.findViewById(R.id.auto);
        ViewGroup viewGroup1 = view.findViewById(R.id.root1);
        viewGroup1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        ViewGroup viewGroup2 = view.findViewById(R.id.root2);
        viewGroup2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        fromClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadUrl.setText("");
                downloadUrl.setText(Utilities.getStringFromClipboard(activity));
            }
        });
        autoFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadUrl.getText().toString().trim().length() != 0) {
                    fileName.setText("");
                    String autoName = URLUtil.guessFileName(downloadUrl.getText().toString(), null, null);
                    if (autoName.endsWith(".bin")) {
                        autoName = autoName.replace(".bin", ".mp4");
                    }
                    fileName.setText(autoName);
                }
            }
        });
        checkTypeOfDownload(isSchedule);
        location.setText(Constant.DEFAULT_DOWNLOAD_PATH);
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
        downloadBottomSheetDialog.setContentView(view);
        updateDialog(url, fileNameTxt);
        downloadBottomSheetDialog.show();
    }

    private void updateDialog(String url, String fileNameTxt) {
        if (url != null)
            downloadUrl.setText(url);
        if (fileNameTxt != null)
            fileName.setText(fileNameTxt);
        else fileName.setText("");
    }

    private void checkTypeOfDownload(boolean isSchedule) {
        if (isSchedule) {
            download.setText(R.string.add);
            time.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
            downloadHint.setVisibility(View.VISIBLE);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scheduleNewDownload(calendar);
                }
            });
        } else {
            download.setText(R.string.start);
            time.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            downloadHint.setVisibility(View.GONE);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNewDownload();
                }
            });
        }
    }

    private void scheduleNewDownload(Calendar calendar) {
        if (!permissionsIsValid()) return;
        if (!timeSet || !dateSet) {
            downloadBottomSheetDialog.dismiss();
            Snackbar.make(binding.getRoot(), getString(R.string.error1), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (dataIsInvalid()) return;
        // start schedule New Download here ..
        downloader.scheduleDownload(getApplication(), calendar, downloadUrl.getText().toString(), location.getText().toString(), fileName.getText().toString());
        downloadBottomSheetDialog.dismiss();
        //Snackbar.make(binding.getRoot() ,   getString(R.string.schedule_done) , Snackbar.LENGTH_SHORT).show();
        startActivity(new Intent(getBaseContext(), ScheduleDownloadsActivity.class));
    }

    private boolean permissionsIsValid() {
        if (!Utilities.checkIfStoragePermissionGranted(this)) {
            Utilities.requestStoragePermission(this);
            downloadBottomSheetDialog.dismiss();
            return false;
        }
        return true;
    }

    private void startNewDownload() {
        Log.i("ab_do", "isFromAnime " + isFromAnime);
        _fileName = Utilities.updateFileNameExt(downloadUrl.getText().toString(), fileName.getText().toString());
        if (!permissionsIsValid()) return;
        if (dataIsInvalid()) return;
        downloader.startDownloading(downloadUrl.getText().toString(), location.getText().toString(), _fileName);
        downloadBottomSheetDialog.dismiss();
        if (!Utilities.adsIsRemoved(activity)) {
            showInterstitialAd();
            AdsUtilities.createDownloadInterstitialAd(activity);
        } else {
            if (isFromAnime) {
                Log.i("ab_do", "send to Anime");
                sendDataToAnime();
            }
        }
        // start download here ..
    }

    private void sendDataToAnime() {
        Intent intent = new Intent();
        intent.setAction(Constant.ANIME_SAVE_DATA_ACTION);
        intent.putExtra(Constant.ANIME_SAVE_DATA_PATH_EXTRA, location.getText().toString() + "/" + _fileName);
        intent.putExtra(Constant.ANIME_SAVE_DATA_NAME_EXTRA, animeIntent.getStringExtra(Constant.ANIME_SAVE_DATA_NAME_EXTRA));
        sendBroadcast(intent);
        finishAffinity();
    }

    private void showInterstitialAd() {
        if (AdsUtilities.downloadInterstitialAd != null) {
            AdsUtilities.downloadInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    if (isFromAnime) {
                        sendDataToAnime();
                    }
                }
            });
            AdsUtilities.downloadInterstitialAd.show(activity);
        } else if (isFromAnime) {
            sendDataToAnime();
        }
    }

    private boolean dataIsInvalid() {
        if (downloadUrl.getText().toString().trim().length() == 0) {
            downloadBottomSheetDialog.dismiss();
            Snackbar.make(binding.getRoot(), getString(R.string.error2), Snackbar.LENGTH_SHORT).show();
            return true;
        }
        if (fileName.getText().toString().trim().length() == 0) {
            downloadBottomSheetDialog.dismiss();
            Snackbar.make(binding.getRoot(), getString(R.string.error3), Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void selectDownloadLocation() {
        if (!permissionsIsValid()) return;
        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, 1);
    }

    private void showDateDialog() {
        DatePickerFragment datePickerFragment = DatePickerFragment.getInstance(Calendar.getInstance());
        datePickerFragment.show(getSupportFragmentManager(), "date");

    }

    private void showTimeDialog() {
        TimePickerFragment timePickerFragment = TimePickerFragment.getInstance(Calendar.getInstance());
        timePickerFragment.show(getSupportFragmentManager(), "time");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateSet = true;
        date.setText(Utilities.FormatToDate(calendar));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        timeSet = true;
        time.setText(Utilities.FormatToTime(calendar, this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            String folderLocation = intent.getExtras().getString("data");
            try {
                location.setText(folderLocation);
            } catch (Exception e) {
                Log.i("ab_do", "onActivityResult: " + e.getMessage());
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUEST_STORAGE_PERMISSION_CODE) {
            if (grantResults.length == 0) return;
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(binding.getRoot(), getString(R.string.give_permission), Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(ActiveDownload activeDownload) {
        if (activeDownload.getDownloadId() == -1) return; // this action can`t be handled right now
        boolean isPaused;
        isPaused = activeDownload.isPaused();
        if (isPaused) {
            // resume it
            downloader.resumeDownload(activeDownload.getDownloadId());
            showResumeAd();

        } else {
            // pause it
            downloader.pauseDownload(activeDownload.getDownloadId());
        }
        activeDownload.setPaused(!isPaused);
        viewModel.updateDownload(activeDownload);
    }

    private void showResumeAd() {
        if (Utilities.adsIsRemoved(activity)) return;
        if (AdsUtilities.play_Pause_downloadInterstitialAd != null) {
            AdsUtilities.play_Pause_downloadInterstitialAd.show(activity);
        }
        AdsUtilities.createPlayPauseDownloadInterstitialAd(activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finishAffinity();
    }

    @Override
    public void onItemClick(ActiveDownload activeDownload) {
        // clicked on the active download ..
        if (IsMoreBtnClicked) {
            CollapseMoreBtn();
        }
        clickedActiveDownload = activeDownload;
        showOptionsDialog();
    }

    private void showOptionsDialog() {
        if (optionsBottomSheetDialog == null || optionsBinding == null) {
            optionsBinding = ActiveDownloadOptionsBinding.inflate(getLayoutInflater());
            optionsBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
            View view = optionsBinding.getRoot();
//            optionsBinding.info.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showFileInfo(clickedActiveDownload);
//                    optionsBottomSheetDialog.dismiss();
//                }
//            });
            optionsBinding.urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyUrl(clickedActiveDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBinding.cancelDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelDownload(clickedActiveDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
            optionsBottomSheetDialog.setContentView(view);
            checkIfShouldHidePlayPauseBtn();
        }
        optionsBinding.FileName.setText(clickedActiveDownload.getFileName());
        if (clickedActiveDownload.isPaused()) {
            optionsBinding.playPause.setText(getString(R.string.resume));
            optionsBinding.playPause.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_play__3_), null, null, null);
            optionsBinding.playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // resume download
                    showResumeAd();
                    if (clickedActiveDownload.getDownloadId() == -1) return;
                    downloader.resumeDownload(clickedActiveDownload.getDownloadId());
                    clickedActiveDownload.setPaused(!clickedActiveDownload.isPaused());
                    viewModel.updateDownload(clickedActiveDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
        } else {
            optionsBinding.playPause.setText(getString(R.string.pause));
            optionsBinding.playPause.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_pause), null, null, null);
            optionsBinding.playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickedActiveDownload.getDownloadId() == -1) return;
                    // pause download
                    downloader.pauseDownload(clickedActiveDownload.getDownloadId());
                    clickedActiveDownload.setPaused(!clickedActiveDownload.isPaused());
                    viewModel.updateDownload(clickedActiveDownload);
                    optionsBottomSheetDialog.dismiss();
                }
            });
        }
        checkIfShouldHidePlayPauseBtn();
        optionsBottomSheetDialog.show();
    }

    private void checkIfShouldHidePlayPauseBtn() {
        if (clickedActiveDownload.getDownloadId() == -1)
            optionsBinding.playPause.setVisibility(View.GONE);
        else optionsBinding.playPause.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        super.onStop();
    }

    private void cancelDownload(ActiveDownload activeDownload) {
        Utilities.PrintLog("cancel " + activeDownload.getFileName());
        downloader.cancelDownload(activeDownload);
        Snackbar.make(binding.getRoot(), getString(R.string.cancel_done), Snackbar.LENGTH_SHORT).show();
    }

    private void copyUrl(ActiveDownload activeDownload) {
        Utilities.copyStringToClipboard(this, activeDownload.getUrl()
                , binding.recycleView);
        Utilities.PrintLog("copyUrl " + activeDownload.getFileName());
    }

    private void showFileInfo(ActiveDownload activeDownload) {
        Utilities.PrintLog("showFileInfo " + activeDownload.getFileName());
    }

    public ActiveDownloadsViewModel getActiveViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(ActiveDownloadsViewModel.class);
        }
        return viewModel;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constant.ORDER_BY_KEY)) {
            Utilities.PrintLog("Order has changed");
            RemoveOldObserve(orderValue);
            String NewOrder = sharedPreferences.getString(Constant.ORDER_BY_KEY, Constant.ORDER_BY_DATE_DESC);
            if (NewOrder != null) {
                initViewModel(NewOrder);
                orderValue = NewOrder;
            }
        }
    }


    private void RemoveOldObserve(String orderValue) {
        switch (orderValue) {
            case Constant.ORDER_BY_DATE_ASC:
                viewModel.getActiveDownloadsByTimeAsc().removeObservers(this);
                break;
            case Constant.ORDER_BY_NAME_ASC:
                viewModel.getActiveDownloadsByNameAsc().removeObservers(this);
                break;
            case Constant.ORDER_BY_NAME_DESC:
                viewModel.getActiveDownloadsByNameDesc().removeObservers(this);
                break;
            case Constant.ORDER_BY_SIZE_ASC:
                viewModel.getActiveDownloadsBySizeAsc().removeObservers(this);
                break;
            case Constant.ORDER_BY_SIZE_DESC:
                viewModel.getActiveDownloadsBySizeDesc().removeObservers(this);
                break;
            default:
                viewModel.getActiveDownloads().removeObservers(this);
                break;
        }
    }


}
