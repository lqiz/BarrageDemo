package com.example.testdanmu;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


/**
 * @Description: è®¾å??ä¿¡æ??ç±?
 * @ClassName: DeviceUtils.java
 * @author å¤?å¤?(xiaitan01@baidu.com,http://weibo.com/feeeeeef)
 * @date 2014-3-19 ä¸????2:46:57
 */
public class DeviceUtils {

    public final static int SCREEN_BRIGHTNESS_MAX = 255;
    public final static int SCREEN_BRIGHTNESS_MINI = 5;

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenWidthPx(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeightPx(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getScreenWidthDp(Context context) {
        return (int) px2dip(context, getScreenWidthPx(context));
    }

    public static int getScreenHeightDp(Context context) {
        return (int) px2dip(context, getScreenHeightPx(context));
    }

    public static Paint paint = new Paint();

    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale);
    }

    public static Rect px2dip(Context context, Rect rect) {
        int left = (int) px2dip(context, rect.left);
        int top = (int) px2dip(context, rect.top);
        int right = (int) px2dip(context, rect.right);
        int bottom = (int) px2dip(context, rect.bottom);
        return new Rect(left, top, right, bottom);
    }

    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale);
    }

    public static Rect dip2px(Context context, Rect rect, Point offset) {
        int left = (int) dip2px(context, rect.left + offset.x);
        int top = (int) dip2px(context, rect.top + offset.y);
        int right = (int) dip2px(context, rect.right + offset.x);
        int bottom = (int) dip2px(context, rect.bottom + offset.y);
        return new Rect(left, top, right, bottom);
    }

    /**
     * dipè½?px???
     * 
     * @param context ä¸?ä¸????
     * @param pxValue dip
     * @return px
     */
    public static int dip2pxforInt(Context context, float pxValue) {
        return Math.round(pxValue * context.getResources().getDisplayMetrics().density);
    }

    /**
     * ??¹æ???????ºç?????è¾¨ç??ä»? px(???ç´?) ??????ä½? è½????ä¸? dp
     */
    public static int px2dipForInt(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Rect dip2px(Context context, Rect rect) {
        int left = (int) dip2px(context, rect.left);
        int top = (int) dip2px(context, rect.top);
        int right = (int) dip2px(context, rect.right);
        int bottom = (int) dip2px(context, rect.bottom);
        return new Rect(left, top, right, bottom);
    }

    // ??¤æ??SD??¡æ????????è¶³å??ç©ºé??
    @SuppressWarnings("deprecation")
    public static boolean isAvailableSpace(long size) {
        File path = Environment.getExternalStorageDirectory();
        try {
            StatFs statFs = new StatFs(path.getPath());
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            if (size < availableBlocks * blockSize) {
                return true;
            }
        } catch (Exception e) {
            //
        }
        return false;
    }

    public static final boolean API_11;
    static {
        API_11 = Build.VERSION.SDK_INT >= 11;
    }

    /**
     * è®¾ç½®å½????å±????äº?åº?
     * 
     * @param activity
     * @param brightness äº?åº???¼ï??0-255
     */
    public static void setScreenBrightness(Activity activity, int brightness) {
        ContentResolver cr = activity.getContentResolver();
        if (cr == null) {
            return;
        }
        // try {
        // Settings.System.putInt(cr, System.SCREEN_BRIGHTNESS, brightness);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        Window window = activity.getWindow();
        // int tmpInt = Settings.System.getInt(activity.getContentResolver(),
        // Settings.System.SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MAX / 2);
        WindowManager.LayoutParams wl = window.getAttributes();
        float tmpFloat = (float) brightness / SCREEN_BRIGHTNESS_MAX;
        if (tmpFloat > 0 && tmpFloat <= 1) {
            wl.screenBrightness = tmpFloat;
        }
        window.setAttributes(wl);
    }

    /**
     * ??¤æ????????å¼????äº?äº?åº??????¨è????????
     * 
     * @param activity
     * @return
     */
    public static boolean isAutoBrightness(final Activity activity) {
        boolean automicBrightness = false;
        ContentResolver cr = activity.getContentResolver();
        if (cr == null) {
            return false;
        }
        try {
            int mod = Settings.System.getInt(cr, System.SCREEN_BRIGHTNESS_MODE);
            if (mod == System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                automicBrightness = true;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        return automicBrightness;
    }

    /**
     * ??·å??å½????å±????äº?åº?
     * 
     * @param activity
     * @return äº?åº????
     */
    public static int getScreenBrightness(final Activity activity) {
        ContentResolver cr = activity.getContentResolver();
        try {
            return Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MAX / 2);
        } catch (Exception e) {

        }
        return SCREEN_BRIGHTNESS_MAX / 2;
    }

    /**
     * å¤????4.0?????ºé??è¯»é¡µhideNavigationBar è¯¸å????ºå??ä¸?è¡¨ç?°å?ºæ??å¼????è¡?ä¸ºï????¹æ????????è¿?ä¸?ä¿???©ï?????å±????
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void hideNavigationBar(Window window) {
        if (window == null) {
            return;
        }
        window.clearFlags(3840);
        window.addFlags(1280);
        if (Build.MODEL.equalsIgnoreCase("Kindle Fire")) {
            window.addFlags(512);
            window.getDecorView().setPadding(0, 0, 0, 20);
        }
        int i1 = 0;
        if (Build.VERSION.SDK_INT >= 14) {
            i1 = window.getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 19) {
                window.getDecorView().setSystemUiVisibility(0x1000 | (0x2 | (0x4 | (0x100 | (0x200 | (i1 | 0x400))))));
                return;
            }
            window.getDecorView().setSystemUiVisibility(i1 | 0x1);
        }
    }

    /**
     * å¤????4.0?????ºé??è¯»é¡µshowNavigationBar è¯¸å????ºå??ä¸?è¡¨ç?°å?ºæ??å¼????è¡?ä¸ºï????¹æ????????è¿?ä¸?ä¿???©ï?????å±????
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void showNavigationBar(Window window) {
        window.clearFlags(3840);
        window.addFlags(67840);
        if (Build.MODEL.equalsIgnoreCase("Kindle Fire"))
            window.getDecorView().setPadding(0, 0, 0, 0);
        int i1 = 0;
        if (Build.VERSION.SDK_INT >= 14) {
            i1 = window.getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 19) {
                window.getDecorView().setSystemUiVisibility(i1 & 0xFFFFE8F9);
                return;
            }
            window.getDecorView().setSystemUiVisibility(i1 & 0xFFFFFFFE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public static boolean copy(String content, Context context) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        // å¾???°å??è´´æ?¿ç?¡ç?????
        if (DeviceUtils.API_11) {
            android.content.ClipboardManager cmb =
                    (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
        } else {
            android.text.ClipboardManager clipboardManager =
                    (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(content.trim());
            if (clipboardManager.hasText()) {
                clipboardManager.getText();
            }
        }

        return true;
    }

    public static int getAppVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (null != pi) {
            return pi.versionCode;
        }
        return -1;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

}
