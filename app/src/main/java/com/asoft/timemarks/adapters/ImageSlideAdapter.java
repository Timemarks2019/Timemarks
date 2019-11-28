package com.asoft.timemarks.adapters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asoft.timemarks.R;

public class ImageSlideAdapter extends PagerAdapter {
    Context ctx;
    List<String> mListSlides;
    List<String> mListLogo;

    public ImageSlideAdapter(Context ctx, List<String> mListSlides, List<String> mListLogo) {
        this.ctx = ctx;
        this.mListSlides = mListSlides;
        this.mListLogo = mListLogo;
    }

    @Override
    public int getCount() {
        return mListSlides.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vp_image, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.image_display);
        ImageView img_logo = (ImageView) view.findViewById(R.id.img_logo);
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        int input = ctx.getResources().getIdentifier(((String) mListSlides.get(position)), "drawable", ctx.getPackageName());
        mImageView.setImageResource(input);

      //  int input_logo = ctx.getResources().getIdentifier(((String) mListLogo.get(position)), "drawable", ctx.getPackageName());
      //  img_logo.setImageResource(input_logo);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
