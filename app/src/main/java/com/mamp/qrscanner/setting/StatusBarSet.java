package com.mamp.qrscanner.setting;

import android.content.res.Resources;
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

    /*icon color change & transparent*/
    public void statusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    /*icon color change & transparent & layout full screen*/
    public void layoutFullScreenTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    //status bar의 높이 계산
    public int getStatusBarHeight(Resources resources)
    {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = resources.getDimensionPixelSize(resourceId);

        return result;
    }

}
