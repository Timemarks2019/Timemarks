package com.asoft.timemarks.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterQuizFeed;
import com.asoft.timemarks.models.ComparisionResult;
import com.asoft.timemarks.models.ItemQuizFeed;
import com.asoft.timemarks.models.response.ResGetComparision;
import com.asoft.timemarks.models.response.ResGetQuizFeed;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class QuizComparisionActivity extends BaseActivity {
    TextView tvName1,tvName2,tvRank1,tvRank2,tvBestScore1,tvBestScore2,tvName,tvRank,tvLevel,tvFromUser;

    ArrayList<ItemQuizFeed> mList = new ArrayList<ItemQuizFeed>();
    AdapterQuizFeed obj_adapter;
    String last_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_comparision);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ItemQuizFeed itemQuizFeed = (ItemQuizFeed)getIntent().getSerializableExtra("itemQuizFeed");

        tvName = (TextView)findViewById(R.id.tvName);
        tvRank = (TextView)findViewById(R.id.tvRank);
        tvLevel = (TextView)findViewById(R.id.tvLevel);
        tvName1 = (TextView)findViewById(R.id.tvName1);
        tvName2 = (TextView)findViewById(R.id.tvName2);
        tvRank1 = (TextView)findViewById(R.id.tvRank1);
        tvRank2 = (TextView)findViewById(R.id.tvRank2);
        tvBestScore1 = (TextView)findViewById(R.id.tvBestScore1);
        tvBestScore2 = (TextView)findViewById(R.id.tvBestScore2);
        tvFromUser = (TextView)findViewById(R.id.tvFromUser);
        ImageView imgProfile = (ImageView) findViewById(R.id.imgProfile);

        if(itemQuizFeed.getUserId().equals(Util.getUserId(QuizComparisionActivity.this))){
            tvName2.setVisibility(View.GONE);
            tvRank2.setVisibility(View.GONE);
            tvBestScore2.setVisibility(View.GONE);
        }
        tvName.setText(itemQuizFeed.getDisplay_name());
        tvLevel.setText("Level "+itemQuizFeed.getLevel());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
        requestOptions.error(R.drawable.profile_image_dummy_nw);
        Glide.with(QuizComparisionActivity.this)
                .setDefaultRequestOptions(requestOptions)
                .load(EndPoints.BASE_IMAGE_URL+itemQuizFeed.getImage_user()).into(imgProfile);

        getData(itemQuizFeed);

        RecyclerView recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuizFeed(mList,QuizComparisionActivity.this,recycler_view,true);
        recycler_view.setLayoutManager(new LinearLayoutManager(QuizComparisionActivity.this));
        recycler_view.setAdapter(obj_adapter);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(QuizComparisionActivity.this, recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        tvFromUser.setText("Tests by "+itemQuizFeed.getDisplay_name());
        getUserFeed(itemQuizFeed.getUserId());
    }
    private void getData(ItemQuizFeed itemQuizFeed) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("getDataComparision",""+Util.getUserId(QuizComparisionActivity.this)+", "+itemQuizFeed.getUserId()+", "+itemQuizFeed.getSubject_id()+", "+itemQuizFeed.getChapter_id()+", "+itemQuizFeed.getLevel()+", "+itemQuizFeed.getUnique_exam_code());
        Call<ResGetComparision> call = apiService.getComparision(Util.getUserId(QuizComparisionActivity.this),itemQuizFeed.getUserId(),itemQuizFeed.getSubject_id(),itemQuizFeed.getChapter_id(),itemQuizFeed.getLevel(),itemQuizFeed.getUnique_exam_code());
        call.enqueue(new Callback<ResGetComparision>() {
            @Override
            public void onResponse(Call<ResGetComparision> call, retrofit2.Response<ResGetComparision> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("resGetComparisions",statusCode+"");
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        ComparisionResult comparisionResult1 = response.body().getComapre_user_result();
                        if(comparisionResult1!=null){
                            tvName1.setText(comparisionResult1.getDisplay_name());
                            tvRank1.setText(comparisionResult1.getRank());
                            tvBestScore1.setText(comparisionResult1.getTotal_right_questions()+"/"+comparisionResult1.getTotal_questions());

                            tvRank.setText("#"+comparisionResult1.getRank());
                        }

                        ComparisionResult comparisionResult2 = response.body().getUser_result();
                        if(comparisionResult2!=null){
                            tvName2.setText(comparisionResult2.getDisplay_name());
                            tvRank2.setText(comparisionResult2.getRank());
                            tvBestScore2.setText(comparisionResult2.getTotal_right_questions()+"/"+comparisionResult2.getTotal_questions());
                        }

                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetComparision> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }

    private void getUserFeed(String userId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResGetQuizFeed> call = apiService.getUserFeed(userId,Util.getUserId(QuizComparisionActivity.this),last_id);
        call.enqueue(new Callback<ResGetQuizFeed>() {
            @Override
            public void onResponse(Call<ResGetQuizFeed> call, retrofit2.Response<ResGetQuizFeed> response) {
                int statusCode = response.code();
                Log.d("resgetData",statusCode+"");
                if(response.isSuccessful()){
                    ArrayList<ItemQuizFeed> mListRaw = response.body().getList();
                    Log.d("mListRaw"," - "+mListRaw.size());
                    if(mListRaw!=null){
                        mList.clear();
                        mList.addAll(mListRaw);
                        obj_adapter.notifyDataSetChanged();
                        last_id = mList.get(mList.size()-1).getID();
                        Log.d("last_id"," - "+last_id);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizFeed> call, Throwable t) {
                // Log error here since request failed
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
