package com.mamp.qrscanner.setting;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarSet {

    private Window window;
    private View view;

    public StatusBarSet(Window window) {
        this.window = window;
        this.view = window.getDecorView();
    }

    public void changeIconColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(view != null) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                window.setStatusBarColor(Color.TRANSPARENT);
//            }
//        }
    }

}
