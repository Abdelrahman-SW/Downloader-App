package com.yremhl.ystgdh.Models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ActiveDownload {
    @PrimaryKey (autoGenerate = true)
    private long id ;
    private int downloadId ;
    private String type ;
    private String url ;
    private String path ;
    private String fileName ;
    private String statues ;
    private String actualPath ;
    private long size ;
    private long downloaded ;
    private long speed ;
    private long eta ;
    private long progress ;
    private long timeStart ;
    private long timeFinished;
    private boolean isPaused ;
    private boolean isWaiting ;
    private int Num_of_retries ;


    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public String getActualPath() {
        return actualPath;
    }

    public void setActualPath(String actualPath) {
        this.actualPath = actualPath;
    }

    public int getNum_of_retries() {
        return Num_of_retries;
    }

    public void setNum_of_retries(int num_of_retries) {
        Num_of_retries = num_of_retries;
    }

    public String getStatues() {
        return statues != null ? statues : " ";
    }

    public void setStatues(String statues) {
        this.statues = statues;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public ActiveDownload() {

    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    @Ignore
    public ActiveDownload(String type, long size, long downloaded, long speed, long eta, long progress, long timeStart, long timeFinished) {
        this.type = type;
        this.size = size;
        this.downloaded = downloaded;
        this.speed = speed;
        this.eta = eta;
        this.progress = progress;
        this.timeStart = timeStart;
        this.timeFinished = timeFinished;
    }

    public ActiveDownload(long id, String type, long size, long downloaded, long speed, long eta, long progress, long timeStart, long timeFinished) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.downloaded = downloaded;
        this.speed = speed;
        this.eta = eta;
        this.progress = progress;
        this.timeStart = timeStart;
        this.timeFinished = timeFinished;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(long timeFinished) {
        this.timeFinished = timeFinished;
    }
}
