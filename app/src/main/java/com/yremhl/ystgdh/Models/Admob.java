package com.yremhl.ystgdh.Models;


import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class Admob implements Serializable {

    @SerializedName("app_id")
    @Expose
    private String app_id = "";

    @SerializedName("banner")
    @Expose
    private String banner = "";

    @SerializedName("interstitial")
    @Expose
    private String interstitial = "";


    public Admob() {

    }

    public Admob(String app_id, String banner, String interstitial, String interstitial2, String imaAd, String nativeAd) {
        this.app_id = app_id;
        this.banner = banner;
        this.interstitial = interstitial;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(String interstitial) {
        this.interstitial = interstitial;
    }

}
