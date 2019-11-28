package com.asoft.timemarks.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.MyClipboardManager;
import com.asoft.timemarks.Utils.Util;

public class RefferAndEarn extends AppCompatActivity {
    String TAG = "WalletDetailActivity";
    TextView tvRefferCode;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reffer_code);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvRefferCode = (TextView)findViewById(R.id.tvRefferCode);
        View imgClose = (View) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        View viewCopyRefferCode = (View) findViewById(R.id.viewCopyRefferCode);
        viewCopyRefferCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClipboardManager myClipboardManager = new MyClipboardManager();
                myClipboardManager.copyToClipboard(RefferAndEarn.this,tvRefferCode.getText().toString());
                Toast.makeText(RefferAndEarn.this,"Text coppied to clipboard!",Toast.LENGTH_LONG).show();
            }
        });

        View viewWhatsApp = (View) findViewById(R.id.viewWhatsApp);
        viewWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWhatsapp(RefferAndEarn.this,tvRefferCode.getText().toString());
            }
        });
        View viewFbMsnger = (View) findViewById(R.id.viewFbMsnger);
        viewFbMsnger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });

        View viewSMS = (View) findViewById(R.id.viewSMS);
        viewSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });
        View viewEmail = (View) findViewById(R.id.viewEmail);
        viewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });
        View viewFacebook = (View) findViewById(R.id.viewFacebook);
        viewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });
        View viewTwitter = (View) findViewById(R.id.viewTwitter);
        viewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });
        View viewShare = (View) findViewById(R.id.viewShare);
        viewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll(tvRefferCode.getText().toString());
            }
        });

        tvRefferCode.setText(Util.getLoginUser(RefferAndEarn.this).getMobile());
    }
    private void shareWhatsapp(Context mContext, String share_text){
        final String appName = mContext.getPackageName();
        String smsBody = getString(R.string.share_text) +"\n\n http://play.google.com/store/apps/details?id=" + appName+"\nUse my referral mobile no :- "+Util.getLoginUser(RefferAndEarn.this).getMobile();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, smsBody);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        mContext.startActivity(sendIntent);
    }
    public void shareAll(String share_text) {
        final String appName = getPackageName();
        String smsBody = getString(R.string.share_text) +"\n\n http://play.google.com/store/apps/details?id="  + appName+"\nUse my referral mobile no :- "+Util.getLoginUser(RefferAndEarn.this).getMobile();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, smsBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
