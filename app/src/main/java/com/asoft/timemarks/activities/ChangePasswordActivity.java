package com.asoft.timemarks.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etCurrPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCurrPassword = (EditText) findViewById(R.id.etCurrPassword);
        final EditText etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        final Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPass = etCurrPassword.getText().toString();
                String newPass = etNewPassword.getText().toString();
                String confirmPass = etConfirmPassword.getText().toString();
                if (Util.isConnected(ChangePasswordActivity.this)) {
                    if (currentPass.trim().length() == 0) {
                        etCurrPassword.setError("Please enter Current Password");
                        return;
                    }
                    if (newPass.trim().length() == 0) {
                        etConfirmPassword.setError("Please enter New Password");
                        return;
                    }
                    if (confirmPass.trim().length() == 0) {
                        etConfirmPassword.setError("Please enter Confirm Password");
                        return;
                    }
                    if(!confirmPass.equalsIgnoreCase(newPass)){
                        etNewPassword.setError("New Password and Confirm Password do not match");
                        etConfirmPassword.setError("New Password and Confirm Password do not match");
                        return;
                    }
                    updatePassword(currentPass,newPass);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void updatePassword(String currentPass, final String newPass) {
        final ProgressDialog loading = ProgressDialog.show(ChangePasswordActivity.this,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.updatePassword(Util.getLoginUser(ChangePasswordActivity.this).getUserId(),currentPass,newPass);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                loading.dismiss();
                int statusCode = response.code();
                Log.d("res",statusCode+"");
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        User user = Util.getLoginUser(ChangePasswordActivity.this);
                        user.setPassword(newPass);
                        Util.saveUserDetailFull(ChangePasswordActivity.this,user);
                        finish();
                    }else {
                        etCurrPassword.setText(response.body().getMsg());
                   //     Toast.makeText(ChangePasswordActivity.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
