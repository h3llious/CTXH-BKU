package com.luong.mainctxhactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.firebase.Timestamp;
import com.google.type.Date;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CtxhItem {
    String id;
    String imgURL;
    String title;
    Timestamp deadline_register;
    Timestamp time_start;
    Timestamp time_end;
    Double day_of_ctxh;

    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, dd-MM-yyyy");

    public CtxhItem(String id, String imgURL, String title, Timestamp deadline_register, Timestamp time_start, Timestamp time_end, Double day_of_ctxh) {
        this.id = id;
        this.imgURL = imgURL;
        this.title = title;
        this.deadline_register = deadline_register;
        this.time_start = time_start;
        this.time_end = time_end;
        this.day_of_ctxh = day_of_ctxh;
    }

    public CtxhItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline_register() {
        return dateFormat.format(deadline_register.toDate());
    }

    public void setDeadline_register(Timestamp deadline_register) {
        this.deadline_register = deadline_register;
    }

    public String getTime_start() {
        return dateFormat.format(time_start.toDate());
    }

    public void setTime_start(Timestamp time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return dateFormat.format(time_end.toDate());
    }

    public void setTime_end(Timestamp time_end) {
        this.time_end = time_end;
    }

    public Double getDay_of_ctxh() {
        return day_of_ctxh;
    }

    public void setDay_of_ctxh(Double day_of_ctxh) {
        this.day_of_ctxh = day_of_ctxh;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((CtxhItem)obj).id);
    }

    public void update(CtxhItem other) {
        this.imgURL = other.imgURL;
        this.title = other.title;
        this.deadline_register = other.deadline_register;
        this.time_start = other.time_start;
        this.time_end = other.time_end;
        this.day_of_ctxh = other.day_of_ctxh;
    }
}
