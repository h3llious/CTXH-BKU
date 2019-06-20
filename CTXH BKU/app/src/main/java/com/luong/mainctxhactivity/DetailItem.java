package com.luong.mainctxhactivity;

public class DetailItem {
    private String mssv;
    private String name;

    public DetailItem(String mssv, String name) {
        this.mssv = mssv;
        this.name = name;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
