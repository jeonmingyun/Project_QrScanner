package com.mamp.qrscanner.listView;

import android.graphics.drawable.Drawable;

public class QrDataListViewItem {
    private Drawable qrDataIcon;
    private String qrData;
    private String qrDataDate;

    public Drawable getQrDataIcon() {
        return qrDataIcon;
    }

    public void setQrDataIcon(Drawable qrDataIcon) {
        this.qrDataIcon = qrDataIcon;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getQrDataDate() {
        return qrDataDate;
    }

    public void setQrDataDate(String qrDataDate) {
        this.qrDataDate = qrDataDate;
    }
}
