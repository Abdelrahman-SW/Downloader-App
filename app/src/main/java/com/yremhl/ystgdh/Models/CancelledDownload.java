package com.yremhl.ystgdh.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CancelledDownload implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id ;
    private long size ;
    private String type ;
    private String url ;
    private String path ;
    private String actualPath ;
    private String fileName ;

    @Ignore
    public CancelledDownload() {

    }

    @Ignore
    protected CancelledDownload(Parcel in) {
        id = in.readLong();
        size = in.readLong();
        type = in.readString();
        url = in.readString();
        path = in.readString();
        actualPath = in.readString();
        fileName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(size);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(path);
        dest.writeString(actualPath);
        dest.writeString(fileName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CancelledDownload> CREATOR = new Creator<CancelledDownload>() {
        @Override
        public CancelledDownload createFromParcel(Parcel in) {
            return new CancelledDownload(in);
        }

        @Override
        public CancelledDownload[] newArray(int size) {
            return new CancelledDownload[size];
        }
    };

    public String getActualPath() {
        return actualPath;
    }

    public void setActualPath(String actualPath) {
        this.actualPath = actualPath;
    }



    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public CancelledDownload(String type, String url , String actualPath ,  String path, String fileName , long size) {
        this.type = type;
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.size = size ;
        this.actualPath = actualPath ;
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


}
