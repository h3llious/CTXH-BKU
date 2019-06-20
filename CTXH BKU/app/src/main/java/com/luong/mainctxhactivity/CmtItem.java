package com.luong.mainctxhactivity;

import com.google.firebase.Timestamp;

public class CmtItem {
    private String name;
    private String time;
    private String comment;
    private Timestamp timestamp;

    CmtItem(String name, Timestamp timestamp, String comment) {
        this.name = name;
        this.timestamp = timestamp;
        this.comment = comment;
        this.time = parseTimestampToString(this.timestamp);
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    private String parseTimestampToString(Timestamp time) {
        if (time == null) return "";
        long diff = System.currentTimeMillis() / 1000 - time.getSeconds();
        if (diff < 60) { // seconds
            return "just now";
        } else {
            diff /= 60;
            if (diff < 60) { // minutes
                return diff + " minute" + ((diff > 1) ? "s" : "");
            } else {
                diff /= 60;
                if (diff < 24) { // hours
                    return diff + " hour" + ((diff > 1) ? "s" : "");
                } else {
                    diff /= 24;  // days
                    return diff + " day" + ((diff > 1) ? "s" : "");
                }
            }
        }
    }
}
