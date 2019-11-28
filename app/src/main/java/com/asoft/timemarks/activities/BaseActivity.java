package com.asoft.timemarks.activities;

import android.support.v7.app.AppCompatActivity;

import com.asoft.timemarks.application.BaseApplication;

public class BaseActivity extends AppCompatActivity {

    public int REQ_CODE_FINISH = 18;

    public void showLoading() {
        BaseApplication.getInstance().showLoading(this);
    }

    public void hideLoading() {
        BaseApplication.getInstance().hideLoading();
    }

    public void showToast(String message) {
        BaseApplication.getInstance().showToast(this,message);
    }
}