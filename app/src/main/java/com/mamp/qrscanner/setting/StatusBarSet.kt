package com.mamp.qrscanner.setting

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window

class StatusBarSet(private val window: Window) {
    private val view: View?

    init {
        this.view = window.getDecorView()
    }

    /*icon color change & transparent*/
    fun statusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                window.setStatusBarColor(Color.TRANSPARENT)
            }
        }
    }

    /*icon color change & transparent & layout full screen*/
    fun layoutFullScreenTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                window.setStatusBarColor(Color.TRANSPARENT)
            }
        }
    }

    //status bar의 높이 계산
    fun getStatusBarHeight(resources: Resources): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) result = resources.getDimensionPixelSize(resourceId)

        return result
    }
}
