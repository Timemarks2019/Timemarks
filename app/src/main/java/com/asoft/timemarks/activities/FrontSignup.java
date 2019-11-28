package com.asoft.timemarks.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.PasswordValidator;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontSignup extends AppCompatActivity {
    EditText etName,etMobile,etEmail,etPassword,etConfirmPassword,etReferral;
    Button signup;
    private View mProgressView;
    private View mLoginFormView;

    String countryId = "";
    ArrayList<String> countryIdList;
    ArrayList<String> countryNameList;
    int countryPos = 0;
    ArrayAdapter<String> sadapterCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_front_signup);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        etName = (EditText) findViewById(R.id.etName);

        etMobile = (EditText) findViewById(R.id.etMobile);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etReferral = (EditText) findViewById(R.id.etReferral);

        signup = (Button) findViewById(R.id.newjoin);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String mobileNo = etMobile.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmpassword = etConfirmPassword.getText().toString();
                String refferal_id = etReferral.getText().toString();
                if (Util.isConnected(FrontSignup.this)) {
                    if (name.trim().length() == 0) {
                        Toast.makeText(FrontSignup.this, "Please enter Name!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (email.trim().length() == 0) {
                        Toast.makeText(FrontSignup.this, "Please enter a Email Id!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!isEmailValid(email)) {
                        Toast.makeText(FrontSignup.this, "Please enter a valid Email Id!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mobileNo.trim().length() == 0) {
                        Toast.makeText(FrontSignup.this, "Please enter Mobile No!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mobileNo.trim().length() < 10) {
                        Toast.makeText(FrontSignup.this, "Mobile number must be of minimum 10 digits!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mobileNo.trim().length() > 12) {
                        Toast.makeText(FrontSignup.this, "Please enter a valid Mobile No!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.trim().length() == 0) {
                        Toast.makeText(FrontSignup.this, "Please enter a Password!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    /*if (password.trim().length() < 8) {
                        Toast.makeText(FrontSignup.this, "Password must be minimum 8 characters!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    PasswordValidator passwordValidator = new PasswordValidator();
                    if(!passwordValidator.validate(password)){
                        Toast.makeText(FrontSignup.this, "Password must be 8 characters, with 1 upper case, 1 lower case and 1 special character!", Toast.LENGTH_LONG).show();
                        return;
                    }*/
                    if (!password.equals(confirmpassword)) {
                        Toast.makeText(FrontSignup.this, "Password and Confirm Password do not match!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    registerUser(name,email,mobileNo,password,refferal_id);
                } else {
                    Toast.makeText(FrontSignup.this, "No Internet Connection!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView txt_sign_up = (TextView)findViewById(R.id.txtsign_up);
        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(FrontSignup.this, FrontLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                FrontSignup.this.startActivity(intent);
            }
        });


        countryIdList = new ArrayList<String>();
        countryNameList = new ArrayList<String>();
        countryNameList.add("Country");
        countryIdList.add("");
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("test1", "requestCode "+requestCode+"----"+resultCode+"-----"+data);
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {

                String result = data.getStringExtra("result");
                Log.d("result", "result : "+result);
                if(result.equals("SUCCESS")){
                    finish();
                    Intent intent = new Intent(FrontSignup.this, FrontLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    FrontSignup.this.startActivity(intent);
                }else {
                    Toast.makeText(FrontSignup.this,"Something went wrong. Please try again!",Toast.LENGTH_LONG).show();
                    recreate();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private void registerUser(final String name, final String email, final String phone, final String password, final String refferal_id) {
        showProgress(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("PostData",""+name+","+countryId+","+phone+","+password+","+email);
        Call<Result> call = apiService.userSignUp(name,password,password,email,phone,"",refferal_id);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                showProgress(false);
                if(response.body() == null){
                    Log.d("Res","Body is Null");
                    return;
                }
                int statusCode = response.code();
                String res = response.message();

                Log.d("res",statusCode+" , "+res+" , "+response.body().getStatus());
                if(response.body().getStatus()){
                    User user = response.body().getUser();
                    user.setPassword(password);
                    Util.saveUserDetailFull(FrontSignup.this,user);
                    finish();
                    Toast.makeText(FrontSignup.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FrontSignup.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(FrontSignup.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                showProgress(false);
                Log.e("CallApi", t.toString());
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
