package com.asoft.timemarks.slideimg;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.models.ItemSlider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    FragmentActivity activity;
    List<ItemSlider> products;

    public SliderAdapter(FragmentActivity activity, List<ItemSlider> products) {
        this.activity = activity;
        this.products = products;
    }
    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_slider, container, false);

        ImageView mImageView = (ImageView) view.findViewById(R.id.image_display);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*Intent intent = new Intent(getActivity(), WebviewActivitySlide.class);
                    intent.putExtra("title","Letest News");
                    intent.putExtra("url",products.get(position).getNews_url());
                    getActivity().startActivity(intent);*/

            }
        });
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_app_trans);
        requestOptions.error(R.drawable.ic_app_trans);
        Glide.with(activity)
                .setDefaultRequestOptions(requestOptions)
                .load(EndPoints.BASE_IMAGE_URL+"images/"+products.get(position).getImage()).into(mImageView);
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