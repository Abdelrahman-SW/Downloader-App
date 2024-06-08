package com.yremhl.ystgdh.Utilites;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;

import com.google.android.material.snackbar.Snackbar;
import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Downloader.Downloader;
import com.yremhl.ystgdh.Downloader.DownloaderHelper;
import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Services.ForegroundService;
import com.yremhl.ystgdh.UI.CancelledDownloads.CancelledDownloadsActivity;
import com.yremhl.ystgdh.UI.CompletedDownloads.CompletedDownloadsActivity;
import com.yremhl.ystgdh.UI.ScheduleDownloads.ScheduleDownloadsActivity;
import com.yremhl.ystgdh.UI.Settings.SettingsActivity;
import com.yremhl.ystgdh.databinding.SortByDialogViewBinding;
import com.tonyodev.fetch2.NetworkType;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utilities {

    // init toolbar for each activity
    public static void initToolbar(AppCompatActivity activity, Toolbar toolbar, boolean displayHomeBtn) {
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(displayHomeBtn);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // for debug purpose
    public static void PrintLog(String msg) {
        if (msg == null) return;
        Log.i("ab_do", msg);
    }

    public static boolean handleNavigationItemSelected(DrawerLayout drawerLayout, AppCompatActivity activity, int item_id) {
        if (item_id == R.id.completed_downloads) {
            activity.startActivity(new Intent(activity, CompletedDownloadsActivity.class));
            return true;
        } else if (item_id == R.id.cancelled_downloads) {
            activity.startActivity(new Intent(activity, CancelledDownloadsActivity.class));
            return true;
        }
//        else if (item_id == R.id.pending_downloads) {
//            activity.startActivity(new Intent(activity , PendingDownloadsActivity.class));
//            return true ;
//        }
        else if (item_id == R.id.schedule_downloads) {
            activity.startActivity(new Intent(activity, ScheduleDownloadsActivity.class));
            return true;
        } else if (item_id == R.id.settings) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
            activity.finish();
            return true;
        } else if (item_id == R.id.Share) {
            drawerLayout.closeDrawer(GravityCompat.START);
            shareApp(activity);
            return true;
        } else if (item_id == R.id.Rate) {
            drawerLayout.closeDrawer(GravityCompat.START);
            rateApp(activity);
            return true;
        } else if (item_id == R.id.feedback) {
            drawerLayout.closeDrawer(GravityCompat.START);
            sendFeedback(activity);
            return true;
        } else if (item_id == R.id.privacyPolicyKey) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showPrivacyPolicy(activity);
            return true;
        } else if (item_id == R.id.termsOfUseKey) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showTermsOfUse(activity);
            return true;
        } else if (item_id == R.id.websiteKey) {
            drawerLayout.closeDrawer(GravityCompat.START);
            goToWebsite(activity);
            return true;
        }
        return false;
    }

    private static void showTermsOfUse(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://apps-player.com/TermsOfUse.html")));
    }

    private static void showPrivacyPolicy(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://apps-player.com/PrivacyPolicy.html")));
    }

    private static void goToWebsite(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://apps-player.com")));
    }

    public static void shareApp(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = context.getString(R.string.download) +
                "\n\n" +
                "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "share with"));
    }

    public static void rateApp(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
    }

    public static void sendFeedback(Context context) {
        // implicit intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@apps-player.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "My feedback for (Lite Downloader APP)");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    public static String FormatToDate(Calendar calendar) {
        return java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(calendar.getTime());
    }

    public static String FormatToTime(Calendar calendar, AppCompatActivity activity) {
        SimpleDateFormat format;
        if (!DateFormat.is24HourFormat(activity))
            format = new SimpleDateFormat("h:mm  a", Locale.getDefault());
        else
            format = new SimpleDateFormat("HH:mm  ", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public static boolean checkIfStoragePermissionGranted(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ;
            if (minSdk29) {
                return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
            else {
                return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    public static void requestStoragePermission(AppCompatActivity activity) {
        ArrayList<String> perms = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // still need the write storage perms before android 10
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.READ_MEDIA_VIDEO);
            perms.add(Manifest.permission.READ_MEDIA_IMAGES);
            perms.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        String[] perms_arr = new String[perms.size()];
        perms_arr = perms.toArray(perms_arr);
        ActivityCompat.requestPermissions(activity,
                perms_arr, Constant.REQUEST_STORAGE_PERMISSION_CODE);
    }

    @NonNull
    public static String updateFileNameExt(String url, String fileName) {
        String normalName = URLUtil.guessFileName(url, null, null);
        String ext = normalName.substring(normalName.lastIndexOf("."), normalName.length());
        Utilities.PrintLog("Ext = " + ext);
        if (ext.equals(".bin")) ext = ".mp4";
        if (!fileName.contains(".")) fileName = fileName + ext;
        Utilities.PrintLog("New File Name " + fileName);
        return fileName;
    }


    public static int getImageResource(String type) {
        Utilities.PrintLog("Type " + type);
        if (type == null) return R.drawable.download_files_img_ar_black;

        if (type.contains("image")) {
            return R.drawable.ic_picture__1_;
        } else if (type.contains("video")) {
            return R.drawable.ic_download__1_;
        } else if (type.contains("audio")) {
            return R.drawable.ic_music_downloads__1_;
        } else if (type.contains("pdf")) {
            return R.drawable.ic_pdf__1_;
        } else if (type.equals("application/vnd.android.package-archive")) {
            return R.drawable.ic__876343_apk_executable_extension_file_icon;
        } else {
            return R.drawable.download_files_img_en_white;
        }
    }

    public static String getDownloadSpeedString(long downloadedBytesPerSecond, Context context) {
        if (downloadedBytesPerSecond < 0) {
            return "";
        }
        double kb = (double) downloadedBytesPerSecond / (double) 1000;
        double mb = kb / (double) 1000;
        final DecimalFormat decimalFormat = new DecimalFormat(".##");
        if (mb >= 1) {
            return context.getString(R.string.download_speed_mb, decimalFormat.format(mb));
        } else if (kb >= 1) {
            return context.getString(R.string.download_speed_kb, decimalFormat.format(kb));
        } else {
            return context.getString(R.string.download_speed_bytes, downloadedBytesPerSecond);
        }
    }

    public static String getDownloadProcessString(long downloadedBytes, Context context) {
        if (downloadedBytes < 0) {
            return "";
        }
        double kb = (double) downloadedBytes / (double) 1000;
        double mb = kb / (double) 1000;
        final DecimalFormat decimalFormat = new DecimalFormat(".##");
        if (mb >= 1) {
            return context.getString(R.string.download_mb, decimalFormat.format(mb));
        } else if (kb >= 1) {
            return context.getString(R.string.download_kb, decimalFormat.format(kb));
        } else {
            return context.getString(R.string.download_bytes, downloadedBytes);
        }
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url.trim());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        Utilities.PrintLog("type mime " + type);
        return type;
    }

    public static CompletedDownload getCompletedDownloadFromActive(Context context, ActiveDownload activeDownload) {
        return new CompletedDownload(activeDownload.getType(), activeDownload.getUrl(), activeDownload.getActualPath(),
                activeDownload.getPath(), activeDownload.getFileName(), getTimeString(context, activeDownload), activeDownload.getSize());
    }

    private static String getTimeString(Context context, ActiveDownload activeDownload) {
        String totalTime = "";
        long totalTimeInMilliSecond = activeDownload.getTimeFinished() - activeDownload.getTimeStart();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeInMilliSecond);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalTimeInMilliSecond);
        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }
        totalTime = context.getString(R.string.downloadedTime, minutes, seconds);
        return totalTime;
    }

    public static String getSpeedTextFromStatue(Context context, ActiveDownload activeDownload, TextView textView) {
        String statues = activeDownload.getStatues();
        if (activeDownload.isWaiting()) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
            return context.getString(R.string.wait);
        }
        if (statues.equals(DownloaderHelper.DownloadStatues.ACTIVE.name())) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.main));
            return getDownloadSpeedString(activeDownload.getSpeed(), context);
        } else if (statues.equals(DownloaderHelper.DownloadStatues.WAITING_NETWORK.name())) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
            return context.getString(R.string.waiting);
        } else if (statues.equals(DownloaderHelper.DownloadStatues.ERROR.name())) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
            return context.getString(R.string.error);
        } else if (statues.equals(DownloaderHelper.DownloadStatues.PAUSED.name())) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
            return context.getString(R.string.paused);
        }
        return " ";
    }

    public static CancelledDownload getCancelledDownloadFromActive(ActiveDownload activeDownload) {
        return new CancelledDownload(activeDownload.getType(), activeDownload.getUrl(), activeDownload.getActualPath()
                , activeDownload.getPath(), activeDownload.getFileName(), activeDownload.getSize());
    }

    public static boolean deleteFile(Context context, String parent, String child) {
        File file = new File(parent, child);
        try {
            return file.delete();
        } catch (Exception exception) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public static void copyStringToClipboard(Context context, String string, View view) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", string);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(view, R.string.url_copied, Snackbar.LENGTH_SHORT).show();
    }

    public static String getStringFromClipboard(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            return (String) clipboard.getPrimaryClip().getItemAt(0).getText();
        } catch (Exception exception) {
            return "";
        }

    }

    public static void cancelActiveDownload(Context context, ActiveDownload activeDownload) {
        Repo repo = new Repo((Application) context.getApplicationContext());
        Utilities.deleteFile(context, activeDownload.getActualPath(), activeDownload.getFileName());
        repo.deleteDownload(activeDownload);
        CancelledDownload cancelledDownload = Utilities.getCancelledDownloadFromActive(activeDownload);
        repo.insertNewCancelledDownload(cancelledDownload);
        Utilities.checkIfShouldStopService(context);
        if (Utilities.getDownloadShouldBeShown(context) == activeDownload.getDownloadId()) {
            // this is the current download shown in the service
            Utilities.updateDownloadShouldBeShown(context, -1);
        }
    }

    public static void cancelAllDownloads(Context context) {
        Repo repo = new Repo((Application) context.getApplicationContext());
        repo.deleteAllActiveDownloads();
        Utilities.checkIfShouldStopService(context);
    }

    public static void startDownloadService(Context context, int progress, String contentText) {
        Repo repo = new Repo((Application) context.getApplicationContext());
        int activeDownloads = repo.getAllActiveDownloadsWithGivenStatue(false);
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra(Constant.NOTIFICATION_PROGRESS, progress);
        intent.putExtra(Constant.NOTIFICATION_ACTIVE_DOWNLOADS, activeDownloads);
        intent.putExtra(Constant.NOTIFICATION_CONTENT_TEXT, contentText);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BackupWorker.class).addTag("BACKUP_WORKER_TAG").build();
//            WorkManager.getInstance(context).enqueue(request);
//        }
        try {
            ContextCompat.startForegroundService(context, intent);
        } catch (Exception exception) {
            String msg = "?????????????? ???????? " + progress + "%";
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private static void stopService(Context context) {
        Intent intent = new Intent(context, ForegroundService.class);
        context.stopService(intent);
    }

    public static void updateStopServiceValue(Context context, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(Constant.STOP_SERVICE_VALUE, value).apply();
    }

    public static boolean iFStopService(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(Constant.STOP_SERVICE_VALUE, false);
    }

    public static long getDownloadShouldBeShown(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(Constant.DOWNLOAD_ID_IN_SERVICE, -1);
    }

    public static void updateDownloadShouldBeShown(Context context, long id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(Constant.DOWNLOAD_ID_IN_SERVICE, id).apply();
    }

    public static void checkIfShouldStopService(Context context) {
        Repo repo = new Repo((Application) context.getApplicationContext());
        if (repo.getAllActiveDownloadsWithGivenStatue(false) == 0) // if there is no active download stop the service
            Utilities.stopService(context);
    }

    public static String getSpeedSummary(Context context, int value) {
        switch (value) {
            case 1:
                return context.getString(R.string.low);
            case 2:
                return context.getString(R.string.medium);
            case 3:
                return context.getString(R.string.max);
            default:
                return " ";
        }
    }

    public static NetworkType getNetworkType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean wifi_only = sharedPreferences.getBoolean(context.getString(R.string.wifi_key), false);
        if (wifi_only) {
            return NetworkType.WIFI_ONLY;
        } else return NetworkType.ALL;
    }

    public static int getConcurrentLimitNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.concurrent_download_key), Constant.DOWNLOAD_CONCURRENT_LIMIT);
    }

    public static boolean enableRetryOnNetworkGain(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.auto_key), true);
    }

    public static void updateOrderByValue(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Constant.ORDER_BY_KEY, value).apply();
    }

    public static void updateOrderBySummary(Context context, Preference preference) {
        String order_value = getOrderByValue(context);
        switch (order_value) {
            case Constant.ORDER_BY_DATE_ASC:
                preference.setSummary(context.getString(R.string.order_asc));
                break;
            case Constant.ORDER_BY_NAME_ASC:
                preference.setSummary(context.getString(R.string.order_desc));
                break;
            case Constant.ORDER_BY_NAME_DESC:
                preference.setSummary(context.getString(R.string.alpha_desc));
                break;
            case Constant.ORDER_BY_SIZE_ASC:
                preference.setSummary(context.getString(R.string.alpha_asc));
                break;
            case Constant.ORDER_BY_SIZE_DESC:
                preference.setSummary(context.getString(R.string.size_desc));
                break;
            default:
                preference.setSummary(context.getString(R.string.time_desc));
                break;
        }
    }

    public static ImageView getSelectedImage(Context context, SortByDialogViewBinding binding) {
        String order_value = getOrderByValue(context);
        switch (order_value) {
            case Constant.ORDER_BY_DATE_ASC:
                return binding.imgDateAsc;
            case Constant.ORDER_BY_NAME_ASC:
                return binding.imgNameAsc;
            case Constant.ORDER_BY_NAME_DESC:
                return binding.imgNameDesc;
            case Constant.ORDER_BY_SIZE_ASC:
                return binding.imgSizeAsc;
            case Constant.ORDER_BY_SIZE_DESC:
                return binding.imgSizeDesc;
            default:
                return binding.imgDateDesc;
        }
    }

    private static String getOrderByValue(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constant.ORDER_BY_KEY, Constant.ORDER_BY_DATE_DESC);
    }

    public static void vibrate(Context context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.vibrate_key), true))
            return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(Constant.VIBRATION_DURATION_IN_MILLI_SECOND, VibrationEffect.DEFAULT_AMPLITUDE));
        else vibrator.vibrate(Constant.VIBRATION_DURATION_IN_MILLI_SECOND);
    }

    public static int getFileSlicingCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int speed = sharedPreferences.getInt(context.getString(R.string.download_speed_key), 3);
        switch (speed) {
            case 1:
                return Constant.NUMBER_OF_DOWNLOAD_THREADS_MIN;
            case 2:
                return Constant.NUMBER_OF_DOWNLOAD_THREADS_MEDIUM;
            default:
                return Constant.NUMBER_OF_DOWNLOAD_THREADS_MAX;
        }
    }

    public static boolean shouldWaiting(Context context, Repo repo) {
        int numOfActiveDownloads = repo.getActiveDownloadsWithGivenStatues(false, false); // current downloading files
        int concurrentLimit = Utilities.getConcurrentLimitNumber(context);
        return numOfActiveDownloads >= concurrentLimit;
    }

    //
    private static ActiveDownload pullOutWaitingDownloads(Repo repo) {
        List<ActiveDownload> activeDownloads = repo.getWaitingDownloads();
        if (activeDownloads == null || activeDownloads.size() == 0) return null;
        ActiveDownload activeDownload = activeDownloads.get(0);
        activeDownloads.remove(0);
        activeDownload.setWaiting(false);
        repo.updateDownload(activeDownload);
        return activeDownload;
    }

    public static void checkIfShouldStartWaitingDownload(Context context, Repo repo) {
        if (shouldWaiting(context, repo)) return;
        ActiveDownload activeDownload = pullOutWaitingDownloads(repo);
        if (activeDownload == null) return;
        Downloader downloader = new Downloader(context);
        if (activeDownload != null)
            downloader.startDownloadWaitingDownload(activeDownload.getId(), activeDownload.getUrl(), activeDownload.getPath(), activeDownload.getFileName());
    }

    public static boolean adsIsRemoved(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.RemoveAdKey), false);
    }


//
//    public static void checkIfShouldStartWaitingDownload (Context context , Repo repo) {

    public static void saveDeviceLanguage(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Constant.DEVICE_LANGUAGE, Locale.getDefault().getLanguage()).apply();
    }

    public static void updateLocale(Context context) {
        String lang = getAppLanguage(context);
        Log.i("ab_do", "App language " + lang);
        if (lang.equals(context.getString(R.string.default_language))) {
            // user set language to the default local device
            // and the default locale now is what the user was select before so
            // we need to set the locale to the default device language
            Log.i("ab_do", "default language");
            setLocaleToDefaultDeviceLanguage(context);
        } else {
            String localeStr = lang.equals(context.getString(R.string.arabic)) ? "ar" : "en";
            setLocale(context, localeStr);
        }
    }

    private static String getAppLanguage(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.language_key), context.getString(R.string.default_language));
    }

    private static void setLocaleToDefaultDeviceLanguage(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(Constant.DEVICE_LANGUAGE, Locale.getDefault().getLanguage());
        setLocale(context, lang);
    }

    private static void setLocale(Context context, String localeStr) {
        Locale locale = new Locale(localeStr);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        //config.setLayoutDirection(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void updateTheme(Activity activity) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity);
        int theme_id = sharedPreferences.getInt(activity.getString(R.string.THEME_KEY), activity.getResources().getInteger(R.integer.default_theme));
        Log.i("ab_do", "Theme Id " + theme_id);

        if (theme_id == activity.getResources().getInteger(R.integer.default_theme)) {
            activity.setTheme(R.style.DownloaderAppProjectDefaultTheme);
        } else if (theme_id == activity.getResources().getInteger(R.integer.black_theme)) {
            activity.setTheme(R.style.black_theme);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_brown)) {
            activity.setTheme(R.style.theme_brown);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_green)) {
            activity.setTheme(R.style.theme_green);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_grey)) {
            activity.setTheme(R.style.theme_grey);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_Pink)) {
            activity.setTheme(R.style.theme_Pink);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_red)) {
            activity.setTheme(R.style.theme_red);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_Deep_Purple)) {
            activity.setTheme(R.style.theme_Deep_Purple);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_light_green)) {
            activity.setTheme(R.style.theme_light_green);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_orange)) {
            activity.setTheme(R.style.theme_orange);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_Purple)) {
            activity.setTheme(R.style.theme_Purple);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_purple_200)) {
            activity.setTheme(R.style.theme_purple_200);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_Teal)) {
            activity.setTheme(R.style.theme_Teal);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_yellow)) {
            activity.setTheme(R.style.theme_yellow);
        } else if (theme_id == activity.getResources().getInteger(R.integer.theme_red_dark)) {
            activity.setTheme(R.style.theme_red_dark);
        }
    }

    public static int getCurrentColor(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.CURRENT_COLOR_KEY), context.getResources().getColor(R.color.white));
    }

    public static Button getDefaultButtonStyle(Context context, int color) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(context);
        button.setLayoutParams(params);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        button.setBackgroundResource(outValue.resourceId);
        button.setTextColor(color);
        return button;
    }

    public static void setDefaultColor(Context context) {
        // set the default theme of the app
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.default_theme)).apply();
        sharedPreferences.edit().putInt(context.getString(R.string.CURRENT_COLOR_KEY), context.getResources().getColor(R.color.white)).apply();
    }

    public static void setThemeColor(Context context, int color) {
        // the user has choose his color so update the theme
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        if (color == context.getResources().getColor(R.color.black)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.black_theme)).apply();
        } else if (color == context.getResources().getColor(R.color.white)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.default_theme)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_green)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_green)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_brown)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_brown)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_grey)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_grey)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_Pink)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_Pink)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_red)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_red)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_Deep_Purple)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_Deep_Purple)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_light_green)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_light_green)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_Purple)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_Purple)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_purple_200)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_purple_200)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_Teal)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_Teal)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_yellow)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_yellow)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_red_dark)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_red_dark)).apply();
        } else if (color == context.getResources().getColor(R.color.theme_orange)) {
            sharedPreferences.edit().putInt(context.getString(R.string.THEME_KEY), context.getResources().getInteger(R.integer.theme_orange)).apply();
        }
        sharedPreferences.edit().putInt(context.getString(R.string.CURRENT_COLOR_KEY), color).apply();
    }
}
