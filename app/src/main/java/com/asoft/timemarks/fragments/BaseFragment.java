package com.asoft.timemarks.fragments;

import android.support.v4.app.Fragment;

import com.asoft.timemarks.application.BaseApplication;


public class BaseFragment extends Fragment {
    public void showLoading() {
        BaseApplication.getInstance().showLoading(getActivity());
    }

    public void hideLoading() {
        BaseApplication.getInstance().hideLoading();
    }

    public void showToast(String message) {
        BaseApplication.getInstance().showToast(getActivity(),message);
    }
}
