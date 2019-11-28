package com.asoft.timemarks.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterTransactions;
import com.asoft.timemarks.application.BaseApplication;
import com.asoft.timemarks.models.ItemTransaction;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.models.response.ResCheckTransaction;
import com.asoft.timemarks.models.response.ResGetBalance;
import com.asoft.timemarks.models.response.ResGetHash;
import com.asoft.timemarks.models.response.ResGetTransactions;
import com.asoft.timemarks.payumoney.AppEnvironment;
import com.asoft.timemarks.payumoney.AppPreference;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.wang.avi.AVLoadingIndicatorView;

public class FragAddMoney extends Fragment {
    private AppPreference mAppPreference;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    public static final String TAG = "FragAddMoney : ";
    TextView tvMoney;

    private RecyclerView recyclerView;
    private AdapterTransactions obj_adapter;
    ArrayList<ItemTransaction> mList;
    AVLoadingIndicatorView progressBar;
    TextView tvMsg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.frag_add_money, container, false);
        mAppPreference = new AppPreference();
        tvMoney = (TextView)fragmentView.findViewById(R.id.tvMoney);
        tvMoney.setText("\u20B9 "+ Util.getLoginUser(getActivity()).getBalance()+"");

        final EditText etAmount = (EditText) fragmentView.findViewById(R.id.etAmount);
        Button btnProceed = (Button) fragmentView.findViewById(R.id.rech_button);
        btnProceed.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String amount = etAmount.getText().toString();
                if (amount.trim().length() > 0) {
                    if (Util.isConnected(getActivity())) {
                        etAmount.setText("");
                        launchPayUMoneyFlow(amount);
                    }
                    else {
                        Toast.makeText(getActivity(), "No Internet Connection!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(),"Please enter the required fields!", Toast.LENGTH_LONG).show();
                }
            }

        });
        tvMsg = (TextView)fragmentView.findViewById(R.id.tvMsg);
        mList = new ArrayList<ItemTransaction>();

        progressBar = (AVLoadingIndicatorView)fragmentView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapter = new AdapterTransactions(mList,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(obj_adapter);

        if(Util.isConnected(getActivity())){
            getData();
        }
       // ((BaseApplication) getActivity().getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
        return fragmentView;
    }
    private void getData () {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResGetTransactions> call = apiService.getTransactions(Util.getUserId(getActivity()));
        call.enqueue(new Callback<ResGetTransactions>() {
            @Override
            public void onResponse(Call<ResGetTransactions> call, Response<ResGetTransactions> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                String res = response.message();
                Log.d("res",statusCode+" , "+res);
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        ArrayList<ItemTransaction> mListRaw = response.body().getTransactions();
                        if(mListRaw!=null){
                            mList.clear();
                            Collections.reverse(mListRaw);
                            mList.addAll(mListRaw);
                            obj_adapter.notifyDataSetChanged();
                        }
                    }
                }
                if(mList.size()>0){
                    tvMsg.setText("");
                }else {
                    tvMsg.setText("You have no any transaction!");
                }
            }
            @Override
            public void onFailure(Call<ResGetTransactions> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
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
        User user = Util.getLoginUser(getActivity());
        String phone = user.getMobile();
        String productName = mAppPreference.getProductInfo();
        String firstName = user.getName();
        String email = user.getEmail();
        String udf1 = user.getUserId();
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((BaseApplication) getActivity().getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(EndPoints.SURL_ADD_MONEY)
                .setfUrl(EndPoints.FURL_ADD_MONEY)
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
            calculateHashFromServer(getActivity(),params.get(PayUmoneyConstants.KEY),params.get(PayUmoneyConstants.TXNID),params.get(PayUmoneyConstants.AMOUNT),params.get(PayUmoneyConstants.PRODUCT_INFO),params.get(PayUmoneyConstants.FIRSTNAME),params.get(PayUmoneyConstants.EMAIL),params.get(PayUmoneyConstants.UDF1));
          /*  mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }*/

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            //     payNowButton.setEnabled(true);
        }
    }

    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        Log.d("CheckSumParams",""+params.toString());
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((BaseApplication) getActivity().getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());
        Log.d("local_hash_string",""+stringBuilder.toString());
        String hash = hashCal(stringBuilder.toString());
      //  paymentParam.setMerchantHash(hash);
        Log.d("LocalHash",""+hash);
        return paymentParam;
    }
    private void calculateHashFromServer(final Context ctx, String KEY, String TXNID, String AMOUNT, String PRODUCT_INFO, String FIRSTNAME, String EMAIL, String UDF1) {
        final ProgressDialog loading = ProgressDialog.show(ctx,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        Call<ResGetHash> call = apiService.calculateHash(Util.getLoginUser(getActivity()).getUserId(),KEY,TXNID,AMOUNT,PRODUCT_INFO,FIRSTNAME,EMAIL,UDF1,"check_transaction");
        call.enqueue(new Callback<ResGetHash>() {
            @Override
            public void onResponse(Call<ResGetHash> call, retrofit2.Response<ResGetHash> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        Log.d("ServerHash",""+response.body().getHash());
                   //     Log.d("hash_string",""+response.body().getHash_string());
                        mPaymentParams.setMerchantHash(response.body().getHash());
                        PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), R.style.AppTheme_pink, mAppPreference.isOverrideResultScreen());
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
    private void checkBalanceNew(String mobile, final String password) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.userLogin(mobile,password,"");
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                int statusCode = response.code();
                Log.d("res",statusCode+" : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        User user = response.body().getUser();
                        user.setPassword(password);
                        Util.saveUserDetailFull(getActivity(),user);
                        tvMoney.setText("\u20B9 "+ user.getBalance()+"");
                    }
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }
    public void checkBalance(final Context ctx) {
        User user = Util.getLoginUser(getActivity());
        if(user!=null){
            String email = user.getEmail();
            String password = user.getPassword();
            if(!email.equals("") && !password.equals("")){
                if(Util.isConnected(getActivity())){
                    checkBalanceNew(email,password);
                }
            }
        }
        /*ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final User mUser = Util.getLoginUser(ctx);
        Call<ResGetBalance> call = apiService.checkBalance(mUser.getUserId());
        call.enqueue(new Callback<ResGetBalance>() {
            @Override
            public void onResponse(Call<ResGetBalance> call, retrofit2.Response<ResGetBalance> response) {
                int statusCode = response.code();
                Log.d("res",statusCode+"");
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        String balance = response.body().getBalance();
                        if(balance.trim().length()>0){
                            mUser.setBalance(balance);
                            Util.saveUserDetailFull(ctx,mUser);
                            tvMoney.setText("\u20B9 "+ balance+"");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetBalance> call, Throwable t) {
                // Log error here since request failed
                Log.e("Util", t.toString());
            }
        });*/
    }
    private void checkTransaction(final Context ctx,String txnId, String paymentId, String amount) {
        final ProgressDialog loading = ProgressDialog.show(ctx,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        Log.d("txnIds","txnId : "+txnId+", paymentId : "+paymentId);
        Call<ResCheckTransaction> call = apiService.checkTransaction(Util.getLoginUser(getActivity()).getUserId(),txnId,paymentId,amount);
        call.enqueue(new Callback<ResCheckTransaction>() {
            @Override
            public void onResponse(Call<ResCheckTransaction> call, retrofit2.Response<ResCheckTransaction> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        Toast.makeText(ctx, response.body().getMsg(), Toast.LENGTH_LONG).show();
                        Util.checkBalance(getActivity());
                        tvMoney.setText("\u20B9 "+response.body().getBalance()+".00");
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
    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }
    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();
            //    payNowButton.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(getActivity(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                if (AppPreference.selectedTheme != -1) {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(), AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
                } else {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(), R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
                }
            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == getActivity().RESULT_OK && data !=
                null) {
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
                     //   checkTransaction(getActivity(),txnId,paymentId,amount);
                        checkBalance(getActivity());
                        getData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //Failure Transaction
                    Toast.makeText(getActivity(), "Transaction Failed!", Toast.LENGTH_LONG).show();
                }
            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }
}