package com.asoft.timemarks.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.quiz.ItemSubject;

import java.util.ArrayList;
import java.util.List;

public class Splash extends AppCompatActivity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        MainLauncher launcher = new MainLauncher();
        launcher.execute(new Void[0]);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(arePermissionsEnabled()){
                //                    permissions granted, continue flow normally
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                requestMultiplePermissions();
            }
        }*/
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
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
                        Toast.makeText(Splash.this,"The App must require the Read/Write storage permissions!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    return;
                }
            }
            openActivity();
            //all is good, continue flow
        }
    }
    private static final int SLEEP_TIME = 30;

    private class MainLauncher extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            doSomeTasks();
            return true;
        }

        protected final void onPostExecute(Boolean paramBoolean) {
            // Start main activity
            openActivity();
        }
        private void doSomeTasks() {
            try {
                Thread.sleep(SLEEP_TIME * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openActivity(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(arePermissionsEnabled()){
                // permissions granted, continue flow normally
                goToNext();
            }else{
                requestMultiplePermissions();
            }
        }else {
            goToNext();
        }
    }
    private void goToNext(){
        if(Util.isUserLoggedIn(Splash.this)){
            ItemSubject subject = Util.getSelectedSubject(Splash.this);
            if(subject!=null){
                goToMainActivity();
            }else {
                goToSubjectActivity();
            }
        }else {
            goToLoginActivity();
        }
    }
    private void goToMainActivity(){
        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToLoginActivity(){
        Intent intent = new Intent(Splash.this, FrontLogin.class);
        startActivity(intent);
        finish();
    }
    private void goToSubjectActivity(){
        Intent intent = new Intent(Splash.this, SubjectActivity.class);
        startActivity(intent);
        finish();
    }
    public void onDestroy() {
        super.onDestroy();
        View localView = findViewById(R.id.splash_root_view);
        if (localView != null) {
            localView.setBackgroundDrawable(null);
        }
    }
}
