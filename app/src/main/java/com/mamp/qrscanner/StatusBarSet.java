package com.mamp.qrscanner;

import android.os.Build;
import android.view.View;

public class StatusBarSet {

    private View view;

    public StatusBarSet(View view) {
        this.view = view;
    }

    public void changeIconColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

}
