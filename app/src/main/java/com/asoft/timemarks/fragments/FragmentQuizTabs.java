package com.asoft.timemarks.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asoft.timemarks.R;


public class FragmentQuizTabs extends Fragment {

    private Activity mainActivity;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int pos = 0;
    public FragmentQuizTabs() {
        fragments = new Fragment[2];
    }
    private Fragment[] fragments;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (Activity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_quiz_tabs, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.tbl_pages);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
     //   viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected( int i) {
                // here you will get the position of selected page
                Log.d("index","index : "+i);

            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(pos);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setScrollPosition(pos, 0, true);
            }
        });

        return v;
    }
    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:
                    fragment=new FragmentQuizFeed();
                    bundle.putString("type", "");
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment=new FragmentQuiz();
                    bundle.putString("type", "");
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment=new FragmentLeaderboard();
                    bundle.putString("type", "");
                    fragment.setArguments(bundle);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Feed";
                case 1:
                    return "Tests";
                case 2:
                    return "Leaderboard";
            }
            return null;
        }
    }
}