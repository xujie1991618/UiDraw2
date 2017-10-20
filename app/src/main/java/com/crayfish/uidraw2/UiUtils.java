package com.crayfish.uidraw2;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/10/16.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class UiUtils{

    static public int getScreenWidthPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }

    static public int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    static public float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}
