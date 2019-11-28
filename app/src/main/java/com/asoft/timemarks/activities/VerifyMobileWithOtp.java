package com.asoft.timemarks.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.asoft.timemarks.R;
import com.asoft.timemarks.models.ResSendOtp;
import com.asoft.timemarks.receivers.SMSReceiver;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyMobileWithOtp extends BaseActivity implements SMSReceiver.OTPReceiveListener {
    TextView tvText;
    TextView txtMessage;
    TextView tvResendTimer;
    String mobileNo = "";
    public static final String TAG = MainActivity.class.getSimpleName();

    private SMSReceiver smsReceiver;
    PinEntryEditText pinEntry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        //    AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);

        // This code requires one time to get Hash keys do comment and share key
        //   Log.d(TAG, "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));
        startSMSListener();
        mobileNo = getIntent().getExtras().getString("mobileNo");

        tvText = (TextView) findViewById(R.id.txtStatement);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        tvText.setText("OTP sent to your registered Mobile number   "+mobileNo);
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        //  sendOtp(mobileNo);

        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (pinEntry.getText().toString().trim().length() > 0) {
                        checkOtp(mobileNo,pinEntry.getText().toString());
                    } else {
                        Toast.makeText(VerifyMobileWithOtp.this, "Please enter Otp!", Toast.LENGTH_SHORT).show();
                        pinEntry.setText(null);
                    }
                }
            });
        }
        tvResendTimer = (TextView) findViewById(R.id.tvResendTimer);
        tvResendTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                pinEntry.setText(null);
                txtMessage.setText("");
                reSendOtp(mobileNo);
            }
        });
        startTimer();
    }
    private void startTimer() {
        tvResendTimer.setClickable(false);
        tvResendTimer.setTextColor(ContextCompat.getColor(VerifyMobileWithOtp.this, R.color.grayfm));
        new CountDownTimer(30000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    tvResendTimer.setText("Resend OTP ( " + secondsLeft + " )");
                }
            }

            public void onFinish() {
                tvResendTimer.setClickable(true);
                tvResendTimer.setText("Resend OTP");
                tvResendTimer.setTextColor(ContextCompat.getColor(VerifyMobileWithOtp.this, R.color.colorPrimary));
            }
        }.start();
    }
    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        }
        String[] splitStr = otp.split("\\s+");
        Log.d("TextCode",splitStr[5]);
        pinEntry.setText(splitStr[5]);

       /* if (pinEntry.getText().toString().trim().length() > 0) {
            checkOtp(mobileNo,pinEntry.getText().toString());
        } else {
            Toast.makeText(VerifyMobileWithOtp.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onOTPTimeOut() {
        txtMessage.setText("Time out");
        //   showToast("Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        txtMessage.setText(error);
        //   showToast(error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        }
    }
    private void reSendOtp(String mobile) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResSendOtp> call = apiService.reSendOtpRegister(mobile);
        call.enqueue(new Callback<ResSendOtp>() {
            @Override
            public void onResponse(Call<ResSendOtp> call, Response<ResSendOtp> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        tvText.setText("OTP sent to your registered Mobile number   "+mobileNo);
                    }else {
                        txtMessage.setText(response.body().getMessage());
                        finish();
                        tvText.setText("Unable to send OTP on Mobile number "+mobileNo);
                    }
                } else {
                    // Show the Error Message
                    Toast.makeText(VerifyMobileWithOtp.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResSendOtp> call, Throwable t) {
                hideLoading();
                Toast.makeText(VerifyMobileWithOtp.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Results",requestCode+" - "+resultCode+" - "+data);
        if (requestCode == REQ_CODE_FINISH) {
            if (resultCode == RESULT_OK) {
                if (smsReceiver != null) {
                    LocalBroadcastManager.getInstance(VerifyMobileWithOtp.this).unregisterReceiver(smsReceiver);
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","SUCCESS");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private void checkOtp(final String mobile, final String otp) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResSendOtp> call = apiService.checkOtpRegister(mobile,otp);
        call.enqueue(new Callback<ResSendOtp>() {
            @Override
            public void onResponse(Call<ResSendOtp> call, Response<ResSendOtp> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","SUCCESS");
                        returnIntent.putExtra("otp",otp);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }else {
                        showToast(response.body().getMessage());
                        tvText.setText("Invalid Otp");
                    }
                } else {
                    // Show the Error Message
                    Toast.makeText(VerifyMobileWithOtp.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResSendOtp> call, Throwable t) {
                hideLoading();
                Toast.makeText(VerifyMobileWithOtp.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}