package com.asoft.timemarks.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.application.BaseApplication;
import com.asoft.timemarks.models.ResCheckJoinStatus;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResCheckTransaction;
import com.asoft.timemarks.models.response.ResGetHash;
import com.asoft.timemarks.models.response.ResGetSingleQuiz;
import com.asoft.timemarks.payumoney.AppEnvironment;
import com.asoft.timemarks.payumoney.AppPreference;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;

public class QuizDetailActivity extends BaseActivity {
    String TAG = "QuizDetailActivity";
    private AppPreference mAppPreference;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    Quiz mQuiz;

    TextView tvMoney;
    TextView tvTitle;
    TextView tvStartDate;
    TextView tvEndDate;
    TextView tvMarks;
    TextView tvMinutes;
    TextView tvAmount;
    TextView tvSubject;
    TextView tvJoined;
    TextView tvTimer;

    String paymentType = "";
    View viewTimer;
    Button btnBuy;
    Button btnScoreCard;
    View viewBuy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAppPreference = new AppPreference();
        mQuiz = (Quiz) getIntent().getSerializableExtra("mQuiz");

        viewBuy = (View)findViewById(R.id.viewBuy);
        viewTimer = (View)findViewById(R.id.viewTimer);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvStartDate = (TextView)findViewById(R.id.tvStartDate);
        tvEndDate = (TextView)findViewById(R.id.tvEndDate);
        tvMarks = (TextView)findViewById(R.id.tvMarks);
        tvMinutes = (TextView)findViewById(R.id.tvMinutes);
        tvAmount = (TextView)findViewById(R.id.tvAmount);
        tvSubject = (TextView)findViewById(R.id.tvSubject);
        tvJoined = (TextView)findViewById(R.id.tvJoined);
        tvTimer = (TextView)findViewById(R.id.tvTimer);
        tvMoney = (TextView)findViewById(R.id.tvMoney);
        tvMoney.setText("(\u20B9 "+ Util.getLoginUser(QuizDetailActivity.this).getBalance()+")");

        tvTitle.setText(mQuiz.getChapter_name());
        tvSubject.setText(mQuiz.getSubject_name());
        tvStartDate.setText("Starting "+mQuiz.getStart_date());
        tvEndDate.setText("Ending "+mQuiz.getEnd_date());
        tvAmount.setText("\u20B9 "+mQuiz.getAmount());
        tvMarks.setText("Marks : "+mQuiz.getTotal_points());
        tvMinutes.setText("Minutes : "+mQuiz.getTime());
        tvJoined.setText(mQuiz.getTotal_joining()+" users joined");
        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioPayment);
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioPayNow:
                        paymentType = "PayuMoney";
                        break;
                    case R.id.radioWallet:
                        paymentType = "Wallet";
                        break;
                    default:
                        break;
                }
            }
        });
        paymentType = "PayuMoney";

        btnBuy = (Button) findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mQuiz.getJoin_status()){
                    if(mQuiz.getStatus().equalsIgnoreCase("Live")){
                        if(mQuiz.getPlay_status()){
                            Toast.makeText(QuizDetailActivity.this,"You already played this Quiz!",Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent = new Intent(QuizDetailActivity.this, ActivityQuizAttempt.class);
                            intent.putExtra("mQuiz",mQuiz);
                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(QuizDetailActivity.this,"This contect will start at : "+mQuiz.getStart_date(),Toast.LENGTH_LONG).show();
                    }
                }else {
                    if(mQuiz.getStatus().equalsIgnoreCase("Open") || mQuiz.getStatus().equalsIgnoreCase("Live")){
                        if(paymentType.equalsIgnoreCase("PayuMoney")){
                            launchPayUMoneyFlow(mQuiz.getAmount());
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(QuizDetailActivity.this);
                            builder.setMessage("Amount will be debited from your available wallet.\nAre you sure you want to proceed?")
                                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            joinContestByWallet();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    }else {
                        Toast.makeText(QuizDetailActivity.this,"This contest is closed for Joining!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnScoreCard = (Button) findViewById(R.id.btnScoreCard);
        btnScoreCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mQuiz.getJoin_status()){
                    if(mQuiz.getPlay_status()){
                        Intent intent = new Intent(QuizDetailActivity.this, ActivityQuizSummary.class);
                        intent.putExtra("mQuiz",mQuiz);
                        startActivity(intent);
                    }
                }
            }
        });

        setData();
    }
    public void onResume() {
        super.onResume();
        if(mQuiz!=null){
            getQuiz();
        }
    }
    private void setData(){
        if(mQuiz.getJoin_status()){
            viewBuy.setVisibility(View.GONE);
            if(mQuiz.getStatus().equalsIgnoreCase("Live")){
                if(mQuiz.getPlay_status()){
                    btnBuy.setText("Played");
                }else {
                    btnBuy.setText("Play");
                }
            }else {
                btnBuy.setText("Joined");
            }
        }else {
            Boolean isAvailable = isAvailableToJoin(mQuiz.getEnd_date());
            Log.d("isAvailable",""+isAvailable);
            if(isAvailable){
                viewBuy.setVisibility(VISIBLE);
                btnBuy.setVisibility(VISIBLE);
                btnBuy.setText("Join");
            }else {
                viewBuy.setVisibility(View.GONE);
                btnBuy.setVisibility(View.GONE);
            }
        }

        if(mQuiz.getJoin_status()){
            if(mQuiz.getPlay_status()){
                btnScoreCard.setVisibility(VISIBLE);
            }else {
                btnScoreCard.setVisibility(View.GONE);
            }
            viewTimer.setVisibility(VISIBLE);
            getRemainingTime(mQuiz.getStart_date(),mQuiz.getEnd_date());
        }else {
            btnScoreCard.setVisibility(View.GONE);
            viewTimer.setVisibility(View.GONE);
        }
    }
    private void getRemainingTime(String strStartDate,String strEndDate){
        Log.d("strStartDate",""+strStartDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = dateFormat.parse(strStartDate);
            Date endDate = dateFormat.parse(strEndDate);
            System.out.println(startDate);

            Date currentDate = new Date();
            long diff = startDate.getTime() - currentDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (currentDate.before(startDate)) {
                Log.e("startDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes + " hours: " + hours + " days: " + days);
                new CountDownTimer(diff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                        long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                        long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                        long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                        tvTimer.setText(day+":"+hour+":"+minute+":"+second);
                    }
                    @Override
                    public void onFinish() {
                        // onFinish!
                        mQuiz.setStatus("Live");
                        setData();
                    }

                }.start();
            }else if(currentDate.after(startDate) && currentDate.before(endDate)){
                tvTimer.setText("Live");
            }else {
                tvTimer.setText("Closed");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private Boolean isAvailableToJoin(String strStartDate){
        Log.d("strStartDate",""+strStartDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startDate = dateFormat.parse(strStartDate);
            System.out.println(startDate);

            Date currentDate = new Date();
            long diff = startDate.getTime() - currentDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (currentDate.before(startDate)) {
                Log.e("startDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes + " hours: " + hours + " days: " + days);
              return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Abandoned : Using getQuiz() method on place of this.
    private void checkStatus() {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("subject"," "+Util.getUserId(QuizDetailActivity.this));
        Call<ResCheckJoinStatus> call = apiService.checkJoiningStatus(Util.getUserId(QuizDetailActivity.this),mQuiz.getId());
        call.enqueue(new Callback<ResCheckJoinStatus>() {
            @Override
            public void onResponse(Call<ResCheckJoinStatus> call, retrofit2.Response<ResCheckJoinStatus> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        mQuiz.setJoin_status(response.body().getJoin_status());
                        setData();
                    }
                }

            }
            @Override
            public void onFailure(Call<ResCheckJoinStatus> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }
    private void getQuiz() {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("subject"," "+Util.getUserId(QuizDetailActivity.this));
        Call<ResGetSingleQuiz> call = apiService.getSingleQuiz(Util.getUserId(QuizDetailActivity.this),mQuiz.getId());
        call.enqueue(new Callback<ResGetSingleQuiz>() {
            @Override
            public void onResponse(Call<ResGetSingleQuiz> call, retrofit2.Response<ResGetSingleQuiz> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        if(response.body().getQuiz() != null){
                            mQuiz = response.body().getQuiz();
                            setData();
                        }
                    }
                }

            }
            @Override
            public void onFailure(Call<ResGetSingleQuiz> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }
    private void joinContestByWallet() {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("subject"," "+Util.getUserId(QuizDetailActivity.this));
        Call<Result> call = apiService.joinContestByWallet(Util.getUserId(QuizDetailActivity.this),mQuiz.getId());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                   //     mQuiz.setJoin_status(response.body().getJoin_status());
                        Toast.makeText(QuizDetailActivity.this,"This contect will start at : "+mQuiz.getStart_date(),Toast.LENGTH_LONG).show();
                        getQuiz();
                    }else {
                        Toast.makeText(QuizDetailActivity.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(QuizDetailActivity.this,"This contect will start at : "+mQuiz.getStart_date(),Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }
    private void launchPayUMoneyFlow(String totalAmt) {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle(getString(R.string.app_name));

        payUmoneyConfig.disableExitConfirmation(false);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = Double.parseDouble(totalAmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        Log.d("txnId"," "+txnId);
        //String txnId = "TXNID720431525261327973";
        User user = Util.getLoginUser(QuizDetailActivity.this);
        String phone = user.getMobile();
        String productName = mAppPreference.getProductInfo();
        String firstName = user.getName();
        String email = user.getEmail();
        String udf1 = user.getUserId();
        String udf2 = mQuiz.getId();
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((BaseApplication) QuizDetailActivity.this.getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(EndPoints.SURL_JOIN_CONTEST)
                .setfUrl(EndPoints.FURL_JOIN_CONTEST)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */

            //    generateChecksum(BookingAct.this,mPaymentParams.getParams());
            //  calculateServerSideHashAndInitiatePayment1(mPaymentParams);
            HashMap<String, String> params = mPaymentParams.getParams();
            calculateHashFromServer(QuizDetailActivity.this,params.get(PayUmoneyConstants.KEY),params.get(PayUmoneyConstants.TXNID),params.get(PayUmoneyConstants.AMOUNT),params.get(PayUmoneyConstants.PRODUCT_INFO),params.get(PayUmoneyConstants.FIRSTNAME),params.get(PayUmoneyConstants.EMAIL),params.get(PayUmoneyConstants.UDF1),params.get(PayUmoneyConstants.UDF2));
          /*  mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,QuizDetailActivity.this, AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,QuizDetailActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }*/

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(QuizDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            //     payNowButton.setEnabled(true);
        }
    }
    private void calculateHashFromServer(final Context ctx, String KEY, String TXNID, String AMOUNT, String PRODUCT_INFO, String FIRSTNAME, String EMAIL, String UDF1, String UDF2) {
        final ProgressDialog loading = ProgressDialog.show(ctx,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        Call<ResGetHash> call = apiService.calculateHashForJoining(Util.getLoginUser(QuizDetailActivity.this).getUserId(),mQuiz.getId(),KEY,TXNID,AMOUNT,PRODUCT_INFO,FIRSTNAME,EMAIL,UDF1,UDF2,"check_transaction");
        call.enqueue(new Callback<ResGetHash>() {
            @Override
            public void onResponse(Call<ResGetHash> call, retrofit2.Response<ResGetHash> response) {
                loading.dismiss();

                Log.d("res",response.code()+"  "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        Log.d("ServerHash",""+response.body().getHash());
                        //     Log.d("hash_string",""+response.body().getHash_string());
                        mPaymentParams.setMerchantHash(response.body().getHash());
                        PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,QuizDetailActivity.this, R.style.AppTheme_pink, mAppPreference.isOverrideResultScreen());
                    }else {
                        Toast.makeText(ctx, response.body().getMsg(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ctx, "Something went wrong! Please try after some time!", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<ResGetHash> call, Throwable t) {
                // Log error here since request failed
                loading.dismiss();
                Log.e(getClass().getName(), t.toString());
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    // Response from Payumoney
                    String payuResponse = transactionResponse.getPayuResponse();
                    // Response from SURl and FURL
                    String merchantResponse = transactionResponse.getTransactionDetails();

                    Log.d("CartAct","Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(payuResponse);
                        JSONObject result = jsonObject.optJSONObject("result");
                        String txnId = result.optString("txnid");
                        String paymentId = result.optString("paymentId");
                        String amount = result.optString("amount");
                        Log.d("PaymtResponse","txnId : "+txnId+" , paymentId :  "+paymentId);
                        //       checkTransaction(getActivity(),txnId,paymentId,amount);
                        getQuiz();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //Failure Transaction
                    Toast.makeText(QuizDetailActivity.this, "Transaction Failed!", Toast.LENGTH_LONG).show();
                }
            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }
    private void checkTransaction(final Context ctx,String txnId, String paymentId, String amount) {
        final ProgressDialog loading = ProgressDialog.show(ctx,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        Log.d("txnIds","txnId : "+txnId+", paymentId : "+paymentId);
        Call<ResCheckTransaction> call = apiService.checkTransaction(Util.getLoginUser(QuizDetailActivity.this).getUserId(),txnId,paymentId,amount);
        call.enqueue(new Callback<ResCheckTransaction>() {
            @Override
            public void onResponse(Call<ResCheckTransaction> call, retrofit2.Response<ResCheckTransaction> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        Toast.makeText(ctx, response.body().getMsg(), Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(ctx, response.body().getMsg(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ctx, "Something went wrong! Please try after some time!", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<ResCheckTransaction> call, Throwable t) {
                // Log error here since request failed
                loading.dismiss();
                Log.e(getClass().getName(), t.toString());
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
