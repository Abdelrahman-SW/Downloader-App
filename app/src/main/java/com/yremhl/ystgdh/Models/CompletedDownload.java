package com.yremhl.ystgdh.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CompletedDownload {

    @PrimaryKey(autoGenerate = true)
    private long id ;
    private String type ;
    private String url ;
    private String path ;
    private String actualPath ;
    private String fileName ;
    private String totalTime ;
    private long size ;
    private int requestID ;

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public CompletedDownload() {

    }

    public CompletedDownload(String type, String url, String actualPath , String path, String fileName, String totalTime, long size) {
        this.type = type;
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.totalTime = totalTime;
        this.size = size;
        this.actualPath = actualPath ;
    }

    public String getActualPath() {
        return actualPath;
    }

    public void setActualPath(String actualPath) {
        this.actualPath = actualPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
