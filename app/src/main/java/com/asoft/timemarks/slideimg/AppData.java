package com.asoft.timemarks.slideimg;

/**
 * Created by HP on 12-03-2016.
 */
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;


public class AppData extends Application {
    public static final boolean DEVELOPER_MODE = false;
    View footerView;

    @SuppressWarnings("unused")
    public void onCreate() {
        super.onCreate();
        if (DEVELOPER_MODE
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll().penaltyDeath().build());
        }

    }

}
