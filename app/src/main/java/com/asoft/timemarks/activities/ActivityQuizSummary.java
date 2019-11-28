package com.asoft.timemarks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterQuizSummary;
import com.asoft.timemarks.models.quiz.ItemQuestion;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResGetQuizAnswers;
import com.asoft.timemarks.models.response.ResSubmitQuiz;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;

public class ActivityQuizSummary extends BaseActivity {
    ArrayList<ItemQuestion> mListQuestions = new ArrayList<ItemQuestion>();
    RecyclerView recycler_view;
    AdapterQuizSummary obj_adapter;
    String time;
    TextView tvQnCounts;
    TextView tvMarks;
    TextView tvTime;
    TextView tvRight;
    TextView tvWrong;
    TextView tvScore;
    ImageView imgPassFail;
    Quiz mQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQuiz = (Quiz) getIntent().getSerializableExtra("mQuiz");

        TextView tvSubject = (TextView)findViewById(R.id.tvSubject);
        TextView tvChapter = (TextView)findViewById(R.id.tvChapter);
        tvQnCounts = (TextView)findViewById(R.id.tvQnCounts);
        tvMarks = (TextView)findViewById(R.id.tvMarks);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvRight = (TextView)findViewById(R.id.tvRight);
        tvWrong = (TextView)findViewById(R.id.tvWrong);
        tvScore = (TextView)findViewById(R.id.tvScore);
        imgPassFail = (ImageView) findViewById(R.id.imgPassFail);

        tvSubject.setText(mQuiz.getSubject_name());
        tvChapter.setText(mQuiz.getChapter_name());

        View viewSubmit = (View)findViewById(R.id.viewSubmit);
        viewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityQuizSummary.this, ActivityQuizQuestions.class);
                intent.putExtra("mQuiz",mQuiz);
                intent.putExtra("chapter",mQuiz.getId());
                intent.putExtra("chapter_name",mQuiz.getChapter_name());
                startActivity(intent);
            }
        });
        View viewLeaderboard = (View)findViewById(R.id.viewLeaderboard);
        viewLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityQuizSummary.this, LeaderBoardActivity.class);
                intent.putExtra("mQuiz",mQuiz);
                startActivity(intent);
            }
        });

        getData(mQuiz.getId());
    }
    private void getData(String quizId) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("getData","quizId : "+quizId);
        Call<ResGetQuizAnswers> call = apiService.getQuizAnswers(quizId, Util.getUserId(ActivityQuizSummary.this));
        call.enqueue(new Callback<ResGetQuizAnswers>() {
            @Override
            public void onResponse(Call<ResGetQuizAnswers> call, retrofit2.Response<ResGetQuizAnswers> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("res",statusCode+", Qndata : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getList()!=null){
                       if(response.body().getPass_status() == 1){
                           imgPassFail.setImageResource(R.drawable.img_pass);
                       }else {
                           imgPassFail.setImageResource(R.drawable.ig_fail);
                       }
                       mListQuestions.clear();
                       mListQuestions.addAll(response.body().getList());
                       tvScore.setText(response.body().getTotal_points()+"/"+response.body().getTotalMarks());
                       tvRight.setText(response.body().getRight_count()+"");
                       tvWrong.setText(response.body().getWrong_count()+"");
                       tvMarks.setText(response.body().getTotal_points()+"");
                       tvTime.setText(response.body().getTime()+"");
                       tvQnCounts.setText(mListQuestions.size()+"");
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
                hideLoading();
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
