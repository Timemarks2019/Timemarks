package com.asoft.timemarks.application;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.support.v7.app.AppCompatDialog;
import android.widget.Toast;

import com.asoft.timemarks.payumoney.AppEnvironment;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

public class BaseApplication extends Application {

    private static BaseApplication baseApplication;
    AppCompatDialog progressDialog;
    private SimpleArcDialog mDialog;
    public static BaseApplication getInstance() {
        return baseApplication;
    }
    AppEnvironment appEnvironment;
    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        mDialog = null;
        appEnvironment = AppEnvironment.SANDBOX;
    }

    public void showLoading(Activity activity) {
        hideLoading();

        int colors[] = {Color.parseColor("#EA8300"), Color.parseColor("#EA8300")};
        ArcConfiguration configuration = new ArcConfiguration(activity);
        configuration.setColors(colors);
        mDialog = new SimpleArcDialog(activity);
        mDialog.setConfiguration(configuration);
        mDialog.dismiss();
        mDialog.show();
    }

    public void hideLoading() {
        if(mDialog == null)
            return;
        mDialog.dismiss();
    }

    public void showToast(Activity activity,String message) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}
