package com.asoft.timemarks.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterQuizSummary;
import com.asoft.timemarks.models.quiz.ItemQuestion;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResGetQuizAnswers;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;

public class ActivityQuizQuestions extends AppCompatActivity {
    Quiz mQuiz;
    ArrayList<ItemQuestion> mListQuestions = new ArrayList<ItemQuestion>();
    RecyclerView recycler_view;
    AdapterQuizSummary obj_adapter;
    AVLoadingIndicatorView progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQuiz = (Quiz) getIntent().getSerializableExtra("mQuiz");

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.progressBar);
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuizSummary(mListQuestions,ActivityQuizQuestions.this);
        recycler_view.setLayoutManager(new LinearLayoutManager(ActivityQuizQuestions.this));
        recycler_view.setAdapter(obj_adapter);

        getData(mQuiz.getId());
    }
    private void getData(String quizId) {
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("getData","quizId : "+quizId);
        Call<ResGetQuizAnswers> call = apiService.getQuizAnswers(quizId, Util.getUserId(ActivityQuizQuestions.this));
        call.enqueue(new Callback<ResGetQuizAnswers>() {
            @Override
            public void onResponse(Call<ResGetQuizAnswers> call, retrofit2.Response<ResGetQuizAnswers> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("res",statusCode+", Qndata : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getList()!=null){
                        mListQuestions.clear();
                        mListQuestions.addAll(response.body().getList());
                        obj_adapter.notifyDataSetChanged();
                    }else {
                        Log.d("res",statusCode+"List is Null");
                    }
                }else {
                    Log.d("res",statusCode+"Error in response");
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizAnswers> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
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
