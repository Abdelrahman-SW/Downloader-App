package com.yremhl.ystgdh.Utilites;

import android.os.Environment;

public class Constant {
    public static final String ANIME_SAVE_DATA_ACTION = "anime.saveData";
    public static final String ANIME_SAVE_DATA_PATH_EXTRA = "animePath";
    public static final String ANIME_SAVE_DATA_NAME_EXTRA = "animeName";
    public static final String BASE_URL = "https://apps-player.com/litedownloader/API/";
    public static final String REMOVE_ADS_ACTION = "remove_ads" ;
    public static final String PRODUCT_ID = "product_id" ;
    public static final String DEVICE_LANGUAGE = "device_language";
    public static int REMOVE_ADS_MONTHLY_INDEX = 0 ;
    public static int REMOVE_ADS_YEARLY_INDEX = 1 ;
    public static final String ORDER_BY_KEY = "order_by";
    public static final String ORDER_BY_NAME_ASC = "ORDER_BY_NAME_ASC";
    public static final String ORDER_BY_NAME_DESC = "ORDER_BY_NAME_DESC";
    public static final String ORDER_BY_SIZE_ASC = "ORDER_BY_SIZE_ASC";
    public static final String ORDER_BY_SIZE_DESC = "ORDER_BY_SIZE_DESC";
    public static final String ORDER_BY_DATE_ASC = "ORDER_BY_DATE_ASC";
    public static final String ORDER_BY_DATE_DESC = "ORDER_BY_DATE_DESC";
    public static final String STOP_SERVICE_VALUE = "stop_service_value";
    public static int VIBRATION_DURATION_IN_MILLI_SECOND = 500 ;
    public static final String DOWNLOAD_ANIME_ACTION = "download.anime.action";
    public static final String DOWNLOAD_ID_IN_SERVICE = "download_service_id";
    public static final String STOP_SERVICE = "stop_service" ;
    public static final String PLAY_ALL_ACTION = "play_all_action";
    public static final String PAUSE_ALL_ACTION = "pause_all_action";
    public static final String ANIME_EXTRA_URL = "anime_url";
    public static final String ANIME_EXTRA_FILE_NAME = "anime_file_name";
    public static String NOTIFICATION_PROGRESS  = "notification_progress" ;
    public static String NOTIFICATION_ACTIVE_DOWNLOADS = "notification_active_downloads";
    public static String NOTIFICATION_CONTENT_TEXT = "notification_content_text";
    public static final int DOWNLOAD_FOREGROUND_SERVICE_ID  = 1 ;
    public static final String SCHEDULE_DOWNLOAD_ID = "schedule_download_id" ;
    public static final String SCHEDULE_DOWNLOAD_ACTION = "schedule_download_action" ;
    public static int MAX_NUMBERS_OF_RETRY = 6 ;
    public static String RE_DOWNLOAD_EXTRA = "reDownloadExtra";
    public static String RE_DOWNLOAD_ACTION = "reDownload";
    public static final int DOWNLOAD_CONCURRENT_LIMIT = 5 ;
    public static final int NUMBER_OF_DOWNLOAD_THREADS_MAX = 80 ;
    public static final int NUMBER_OF_DOWNLOAD_THREADS_MIN = 1 ;
    public static final int NUMBER_OF_DOWNLOAD_THREADS_MEDIUM = 20 ;
    public static final int REQUEST_STORAGE_PERMISSION_CODE = 1 ;
    public static final String CALENDER = "Calender";
    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + "Downloads";
}
