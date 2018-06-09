package com.fmtech.app.entity;

import com.fmtech.fmlite.annotation.DatabaseField;
import com.fmtech.fmlite.annotation.DatabaseTable;

/**
 * Created by Administrator on 2017/1/9 0009.
 */
@DatabaseTable("tb_down")
public class DownFile {
    public DownFile(String time, String path) {
        this.time = time;
        this.path = path;
    }

    public DownFile( ) {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     *
     */
    @DatabaseField("tb_time")
    public String time;

    @DatabaseField("tb_path")
    public String path;
}
