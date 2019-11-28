package com.asoft.timemarks.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.activities.ActivityQuizAttempt;
import com.asoft.timemarks.activities.FrontLogin;
import com.asoft.timemarks.activities.QuizDetailActivity;
import com.asoft.timemarks.adapters.AdapterQuiz;
import com.asoft.timemarks.adapters.AdapterQuizChapters;
import com.asoft.timemarks.adapters.ImageSlideAdapter;
import com.asoft.timemarks.application.BaseApplication;
import com.asoft.timemarks.models.ItemSlider;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResCheckTransaction;
import com.asoft.timemarks.models.response.ResGetHash;
import com.asoft.timemarks.models.response.ResGetHomeData;
import com.asoft.timemarks.models.response.ResGetQuizes;
import com.asoft.timemarks.payumoney.AppEnvironment;
import com.asoft.timemarks.payumoney.AppPreference;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.asoft.timemarks.slideimg.CirclePageIndicator;
import com.asoft.timemarks.slideimg.PageIndicator;
import com.asoft.timemarks.slideimg.SliderAdapter;
import com.google.gson.Gson;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class FragmenHome extends BaseFragment {
    SliderView sliderView;
    SliderAdapter imageSlideAdapter;
    private static final long ANIM_VIEWPAGER_DELAY = 20000;
 //   private ViewPager mViewPager;
    TextView imgNameTxt;
//    PageIndicator mIndicator;
    private Runnable animateViewPager;
    private Handler handler;
    List<ItemSlider> mSlides = new ArrayList<ItemSlider>();
    boolean stopSliding = false;

    AdapterQuiz obj_adapter;
    ArrayList<Quiz> mList = new ArrayList<Quiz>();
    View viewNoInternet;
    AVLoadingIndicatorView progressBar;
    TextView tvMsg;
    private AppPreference mAppPreference;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    SwipyRefreshLayout mSwipeRefreshLayout;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        sliderView = fragmentView.findViewById(R.id.imageSlider);

        imageSlideAdapter = new SliderAdapter(getActivity(), mSlides);
        sliderView.setSliderAdapter(imageSlideAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });
     /*   mViewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) fragmentView.findViewById(R.id.indicator);
        //   imgNameTxt = (TextView) fragmentView.findViewById(R.id.img_name);

        imageSlideAdapter = new SliderAdapter(getActivity(), mSlides);
        mViewPager.setAdapter(imageSlideAdapter);

        mIndicator.setViewPager(mViewPager);
        runnable(mSlides.size());
        //Re-run callback
        handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);

        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(48, 0, 48, 0);
        mViewPager.setPageMargin(24);*/

        tvMsg = (TextView) fragmentView.findViewById(R.id.tvMsg);

        progressBar = (AVLoadingIndicatorView)fragmentView.findViewById(R.id.progressBar);
        RecyclerView recycler_view = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuiz(mList,getActivity());
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(obj_adapter);


        mSwipeRefreshLayout = (SwipyRefreshLayout)fragmentView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                mSwipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
        return fragmentView;
    }
    private void getData() {
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("subject"," "+Util.getUserId(getActivity())+" - "+Util.getSelectedSubject(getActivity()).getId());
        Call<ResGetHomeData> call = apiService.getHomeData(Util.getUserId(getActivity()),Util.getSelectedSubject(getActivity()).getId(),"Open");
        call.enqueue(new Callback<ResGetHomeData>() {
            @Override
            public void onResponse(Call<ResGetHomeData> call, retrofit2.Response<ResGetHomeData> response) {
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                int statusCode = response.code();
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        ArrayList<Quiz> mListRaw = response.body().getList();
                        if (mListRaw != null) {
                            mList.clear();
                            mList.addAll(mListRaw);
                            obj_adapter.notifyDataSetChanged();
                        }

                        ArrayList<ItemSlider> mListSlidesRaw = response.body().getSlider_list();
                        if(mListSlidesRaw!=null){
                            mSlides.clear();
                            mSlides.addAll(mListSlidesRaw);
                            imageSlideAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if(mList.size()==0){
                    tvMsg.setText("There is no contest available at the moment.\nPlease visit again!");
                }else {
                    tvMsg.setText("");
                }
                Log.d("resgetData"," Size : "+mList.size());
            }
            @Override
            public void onFailure(Call<ResGetHomeData> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
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
                        if (AppPreference.selectedTheme != -1) {
                            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
                        } else {
                            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
                        }
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
}
