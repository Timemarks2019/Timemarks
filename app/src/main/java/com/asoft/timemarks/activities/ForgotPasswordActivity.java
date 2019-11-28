package com.asoft.timemarks.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.ResSendOtp;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {
    EditText etMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etMobile = (EditText) findViewById(R.id.etEmail);
        final Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNo = etMobile.getText().toString();
                if (mobileNo.trim().length() == 0) {
                    etMobile.setError("Please enter Mobile No");
                    return;
                }
                if (mobileNo.trim().length() < 10) {
                    etMobile.setError("Please enter a valid Mobile No");
                    return;
                }
                if (mobileNo.trim().length() > 10) {
                    etMobile.setError("Please enter a valid Mobile No");
                    return;
                }
                if (!mobileNo.matches("[0-9]*")) {
                    etMobile.setError("Please enter a valid Mobile No");
                    return;
                }
                if (Util.isConnected(ForgotPasswordActivity.this)) {
                    sendOtp(mobileNo);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void forgotPassword(String email) {
        final ProgressDialog loading = ProgressDialog.show(ForgotPasswordActivity.this,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.forgotPassword(email);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                loading.dismiss();
                int statusCode = response.code();
                Log.d("res",statusCode+"");
                if(response.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                    if(response.body().getStatus()){
                        startActivity(new Intent(ForgotPasswordActivity.this,OtpVerification.class));
                    }else {

                    }
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                loading.dismiss();
                Log.e("CallApi", t.toString());
            }
        });
    }
    private void sendOtp(final String mobile) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResSendOtp> call = apiService.sendOtp(mobile);
        call.enqueue(new Callback<ResSendOtp>() {
            @Override
            public void onResponse(Call<ResSendOtp> call, Response<ResSendOtp> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        Intent intent = new Intent(ForgotPasswordActivity.this,OtpVerification.class);
                        intent.putExtra("mobileNo",mobile);
                        startActivity(intent);
                        finish();
                    }else {
                        etMobile.setError(response.body().getMessage());
                    }
                } else {
                    // Show the Error Message
                    Toast.makeText(ForgotPasswordActivity.this, "Unable to send Otp", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResSendOtp> call, Throwable t) {
                hideLoading();
                Toast.makeText(ForgotPasswordActivity.this, "Unable to connect to server!", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
