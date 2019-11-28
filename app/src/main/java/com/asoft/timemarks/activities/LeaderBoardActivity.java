package com.asoft.timemarks.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterLeaderBoard;
import com.asoft.timemarks.models.ItemLeaderBoard;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResGetLeaderboard;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class LeaderBoardActivity extends AppCompatActivity {
    AdapterLeaderBoard obj_adapter;
    public static ArrayList<ItemLeaderBoard> mList;
    View viewNoInternet;
    AVLoadingIndicatorView progressBar;

    TextView tvMsg;
    Quiz mQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQuiz = (Quiz) getIntent().getSerializableExtra("mQuiz");

        mList = new ArrayList<ItemLeaderBoard>();

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        RecyclerView recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        obj_adapter = new AdapterLeaderBoard(mList,LeaderBoardActivity.this);
        recycler_view.setLayoutManager(new LinearLayoutManager(LeaderBoardActivity.this, LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(obj_adapter);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(LeaderBoardActivity.this, recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        progressBar = (AVLoadingIndicatorView) findViewById(R.id.progressBar);
        viewNoInternet = (View) findViewById(R.id.viewNoInternet);
        Button btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Util.isConnected(LeaderBoardActivity.this)){
                    viewNoInternet.setVisibility(VISIBLE);
                }else {
                    viewNoInternet.setVisibility(View.GONE);
                }
                getData();
            }
        });
        if(!Util.isConnected(LeaderBoardActivity.this)){
            viewNoInternet.setVisibility(VISIBLE);
        }else {
            viewNoInternet.setVisibility(View.GONE);
            getData();
        }
    }
    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResGetLeaderboard> call = apiService.getQuizLeaderboard(Util.getUserId(LeaderBoardActivity.this),mQuiz.getId());
        call.enqueue(new Callback<ResGetLeaderboard>() {
            @Override
            public void onResponse(Call<ResGetLeaderboard> call, Response<ResGetLeaderboard> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("res",statusCode+", data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        if(response.body().getList()!=null){
                            mList.clear();
                            mList.addAll(response.body().getList());
                            obj_adapter.notifyDataSetChanged();
                            tvMsg.setText("");
                        }
                    }else {
                        tvMsg.setText("Please Login to view Leaderboard");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetLeaderboard> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                Log.e(LeaderBoardActivity.this.getClass().getName(), t.toString());
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
