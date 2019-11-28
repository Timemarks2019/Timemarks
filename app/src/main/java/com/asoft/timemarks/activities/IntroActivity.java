package com.asoft.timemarks.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.adapters.ImageSlideAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class IntroActivity extends AppCompatActivity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
    private ViewPager mViewPager;
    List<String> mListSlides;
    List<String> mListLogo;
    private static final long ANIM_VIEWPAGER_DELAY = 15000;
    private Runnable animateViewPager;
    private Handler handler;
    boolean stopSliding = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mListSlides = new ArrayList<String>();
        mListSlides.add("ic_app_trans");
        mListSlides.add("ic_app_trans");
        mListSlides.add("ic_app_trans");
        mListSlides.add("ic_app_trans");

        mListLogo = new ArrayList<String>();
        mListLogo.add("logo_red_orange");
        mListLogo.add("logo_red_white");
        mListLogo.add("logo_red_orange");
        mListLogo.add("logo_red_white");
        showSlide();
        Button btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(arePermissionsEnabled()){
                    startActivityForResult(new Intent(IntroActivity.this, RegisterActivity.class),REQ_CODE_FINISH);
                }else {
                    requestMultiplePermissions();
                }

            }
        });
        Button btnExistingMembers = (Button)findViewById(R.id.btnExistingMembers);
        btnExistingMembers.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(arePermissionsEnabled()){
                    startActivityForResult(new Intent(IntroActivity.this, FrontLogin.class),REQ_CODE_FINISH);
                }else {
                    requestMultiplePermissions();
                }
            }
        });
        /*TextView tvLogin = (TextView)findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(arePermissionsEnabled()){
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                }else {
                    requestMultiplePermissions();
                }
            }
        });*/
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("test1", "requestCode "+requestCode+"----"+resultCode+"-----"+data);
        if (requestCode == REQ_CODE_FINISH) {
            if (resultCode == RESULT_OK) {
                onBackPressed();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        Toast.makeText(IntroActivity.this,"The App must require the Read/Write storage permissions!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    return;
                }
            }
            /*Intent intent = new Intent(IntroActivity.this, FrontLogin.class);
            startActivity(intent);
            finish();*/
            //all is good, continue flow
        }
    }
    private static final int SLEEP_TIME = 30;

    public void showSlide() {
        mViewPager.setAdapter(new ImageSlideAdapter(IntroActivity.this, mListSlides,mListLogo));
        /*runnable(mListSlides.size());
        //Re-run callback
        handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);*/
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }
    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }
    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            IntroActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mViewPager.getCurrentItem() < mListSlides.size() - 1) {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    } else {
                        mViewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
    @Override
    public void onResume() {
     /*   if (mListSlides != null && mListLogo != null) {
            mViewPager.setAdapter(new ImageSlideAdapter(IntroActivity.this, mListSlides,mListLogo));
            runnable(mListSlides.size());
            //Re-run callback
            handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
        }*/

        super.onResume();
    }
    private class MainLauncher extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            doSomeTasks();
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        protected final void onPostExecute(Boolean paramBoolean) {
            // Start main activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(arePermissionsEnabled()){
                    //                    permissions granted, continue flow normally
                    Intent intent = new Intent(IntroActivity.this, FrontLogin.class);
                    startActivity(intent);
                    finish();
                }else{
                    requestMultiplePermissions();
                }
            }else {
                Intent intent = new Intent(IntroActivity.this, FrontLogin.class);
                startActivity(intent);
                finish();
            }
        }
        private void doSomeTasks() {
            try {
                Thread.sleep(SLEEP_TIME * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        View localView = findViewById(R.id.splash_root_view);
        if (localView != null) {
            localView.setBackgroundDrawable(null);
        }
    }
}
