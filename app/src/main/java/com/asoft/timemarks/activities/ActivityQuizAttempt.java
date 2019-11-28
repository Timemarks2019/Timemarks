package com.asoft.timemarks.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterQnNumber;
import com.asoft.timemarks.models.ItemQnNumber;
import com.asoft.timemarks.models.quiz.ItemQuestion;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResGetQuizQuestions;
import com.asoft.timemarks.models.response.ResSubmitQuiz;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
//Twew
public class ActivityQuizAttempt extends BaseActivity {
    TextView tvTimer;
    AdapterQnNumber obj_adapter;
    public static ArrayList<ItemQnNumber> mListQnNo = new ArrayList<ItemQnNumber>();

    private ViewPager mViewPager;
    public static ArrayList<ItemQuestion> mListQuestions = new ArrayList<ItemQuestion>();
    AdapterViewPagerQuestion adapterViewPagerQuestion;

    AVLoadingIndicatorView progressBar;

    Chronometer cmTimer;
    TextView tvQnCounts;
    TextView tvMarks;

    Quiz mQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_attempt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mQuiz = (Quiz) getIntent().getSerializableExtra("mQuiz");

        if(mQuiz.getPlay_status()){
            Toast.makeText(ActivityQuizAttempt.this,"You already played this contest!",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.progressBar);

        cmTimer = (Chronometer) findViewById(R.id.cmTimer);
        tvTimer = (TextView)findViewById(R.id.tvTime);
        tvQnCounts = (TextView)findViewById(R.id.tvQnCounts);
        tvMarks = (TextView)findViewById(R.id.tvMarks);
        TextView tvSubject = (TextView)findViewById(R.id.tvSubject);
        TextView tvChapter = (TextView)findViewById(R.id.tvChapter);
        tvSubject.setText(mQuiz.getChapter_name());
        tvChapter.setText(mQuiz.getSubject_name());
        tvMarks.setText(mQuiz.getTotal_points()+"");

        RecyclerView recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQnNumber(mListQnNo,0,ActivityQuizAttempt.this);
        recycler_view.setLayoutManager(new LinearLayoutManager(ActivityQuizAttempt.this, LinearLayoutManager.HORIZONTAL,false));
        recycler_view.setAdapter(obj_adapter);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(ActivityQuizAttempt.this, recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mViewPager.setCurrentItem(position);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapterViewPagerQuestion = new AdapterViewPagerQuestion(ActivityQuizAttempt.this, mListQuestions);
        mViewPager.setAdapter(adapterViewPagerQuestion);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }
            @Override
            public void onPageSelected( int i) {
                // here you will get the position of selected page
                Log.d("index","index : "+i);
                obj_adapter.selectedPos = i;
                obj_adapter.notifyDataSetChanged();
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        View viewSubmit = (View)findViewById(R.id.viewSubmit);
        viewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ItemQuestion> mListQnsAdapter = adapterViewPagerQuestion.getQuestionList();
                final ArrayList<ItemQuestion> mListAttempted = new ArrayList<ItemQuestion>();
                Log.d("ItemQuestion",""+mListQnsAdapter.size());
                if(mListQnsAdapter.size()>0){
                    for (ItemQuestion itemQuestion : mListQnsAdapter){
                        if(itemQuestion.getMy_answer()!=null){
                            if(!itemQuestion.getMy_answer().equalsIgnoreCase("")){
                                mListAttempted.add(itemQuestion);
                            }
                        }
                    }
                }

                if(mListAttempted.size()>0){
                    Log.d("mListQnsAdapter"," "+new Gson().toJson(mListQnsAdapter));
                    cmTimer.stop();
                    Log.d("cmTimer"," "+cmTimer.getText().toString());
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityQuizAttempt.this);
                    alertDialogBuilder.setMessage("Are you sure, You want to submit this contest?");
                            alertDialogBuilder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            submitQuiz(mListAttempted,cmTimer.getText().toString(),mQuiz.getId(),"");
                                        }
                                    });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    Toast.makeText(ActivityQuizAttempt.this,"Please attempt atleast one question",Toast.LENGTH_LONG).show();
                }
            }
        });
        getData(mQuiz.getId());
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        ArrayList<ItemQuestion> mListQnsAdapter = adapterViewPagerQuestion.getQuestionList();
        final ArrayList<ItemQuestion> mListAttempted = new ArrayList<ItemQuestion>();
        Log.d("ItemQuestion",""+mListQnsAdapter.size());
        if(mListQnsAdapter.size()>0){
            for (ItemQuestion itemQuestion : mListQnsAdapter){
                if(itemQuestion.getMy_answer()!=null){
                    if(!itemQuestion.getMy_answer().equalsIgnoreCase("")){
                        mListAttempted.add(itemQuestion);
                    }
                }
            }
        }
        if(mListAttempted.size()>0){
            Log.d("mListQnsAdapter"," "+new Gson().toJson(mListQnsAdapter));
            cmTimer.stop();
            Log.d("cmTimer"," "+cmTimer.getText().toString());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityQuizAttempt.this);
            alertDialogBuilder.setMessage("You need to submit contest before closing.\nAre you sure, You want to submit it?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            submitQuiz(mListAttempted,cmTimer.getText().toString(),mQuiz.getId(),"");
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else {
            Toast.makeText(ActivityQuizAttempt.this,"Please attempt atleast one question",Toast.LENGTH_LONG).show();
        }
    }

    private void getData(String quizId) {
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("getData","quizId : "+quizId);
        Call<ResGetQuizQuestions> call = apiService.getQuizQuestions(quizId,"1");
        call.enqueue(new Callback<ResGetQuizQuestions>() {
            @Override
            public void onResponse(Call<ResGetQuizQuestions> call, retrofit2.Response<ResGetQuizQuestions> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("res",statusCode+", Qndata : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getList()!=null){
                        mListQnNo.clear();
                        mListQuestions.clear();
                        for(int i=0 ; i < response.body().getList().size(); i++){
                            int qnNo = i+1;
                            mListQnNo.add(new ItemQnNumber(""+qnNo,false));
                            mListQuestions.add(response.body().getList().get(i));
                            Log.d("New Qn", " "+response.body().getList().get(i).getQuestion());
                        }

                        tvQnCounts.setText(""+mListQuestions.size());
                        cmTimer.start();
                        obj_adapter.notifyDataSetChanged();
                        adapterViewPagerQuestion.notifyDataSetChanged();
                    }else {
                        Log.d("res",statusCode+"List is Null");
                    }
                }else {
                    Log.d("res",statusCode+"Error in response");
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizQuestions> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                Log.e("CallApi", t.toString());
            }
        });
    }
    private void submitQuiz(final ArrayList<ItemQuestion> mListQnsAdapter, final String time, final String chapter, final String level) {
        showLoading();
        String answerList = new Gson().toJson(mListQnsAdapter);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("submitQuizData","user_id : "+Util.getUserId(ActivityQuizAttempt.this)+", response_dec : "+answerList+", attempt_time : "+time+", chapter : "+chapter);
        Call<ResSubmitQuiz> call = apiService.submitQuiz(Util.getUserId(ActivityQuizAttempt.this),answerList,time,chapter,level);
        call.enqueue(new Callback<ResSubmitQuiz>() {
            @Override
            public void onResponse(Call<ResSubmitQuiz> call, retrofit2.Response<ResSubmitQuiz> response) {
                hideLoading();
                int statusCode = response.code();
                Log.d("res",statusCode+", submitQuiz : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        Intent intent = new Intent(ActivityQuizAttempt.this,ActivityQuizSummary.class);
                        intent.putExtra("mQuiz",mQuiz);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(ActivityQuizAttempt.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Log.d("res",statusCode+"Error in response");
                }
            }
            @Override
            public void onFailure(Call<ResSubmitQuiz> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }

    private void startTimer(String minutes){
        if(minutes.trim().length()>0){
            int mins = Integer.parseInt(minutes);
            int milliseconds = mins*60*1000;
            new CountDownTimer(milliseconds, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(""+String.format("%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }
                public void onFinish() {
                    tvTimer.setText("0:0");
                }
            }.start();
        }else {
            tvTimer.setText("0:0");
        }
    }
    public class AdapterViewPagerQuestion extends PagerAdapter {
        FragmentActivity activity;
        ArrayList<ItemQuestion> mList;

        public AdapterViewPagerQuestion(FragmentActivity activity, ArrayList<ItemQuestion> mList) {
            this.activity = activity;
            this.mList = mList;
        }
        @Override
        public int getCount() {
            return mList.size();
        }


        public ArrayList<ItemQuestion> getQuestionList() {
            return mList;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View inflateview = inflater.inflate(R.layout.item_question, container, false);

            TextView tvQNo = (TextView) inflateview.findViewById(R.id.tvQNo);
            TextView tvQuestion = (TextView) inflateview.findViewById(R.id.tvQuestion);
            TextView tvOption1 = (TextView) inflateview.findViewById(R.id.tvOption1);
            TextView tvOption2 = (TextView) inflateview.findViewById(R.id.tvOption2);
            TextView tvOption3 = (TextView) inflateview.findViewById(R.id.tvOption3);
            TextView tvOption4 = (TextView) inflateview.findViewById(R.id.tvOption4);
            View viewOption1 = (View) inflateview.findViewById(R.id.viewOption1);
            View viewOption2 = (View) inflateview.findViewById(R.id.viewOption2);
            View viewOption3 = (View) inflateview.findViewById(R.id.viewOption3);
            View viewOption4 = (View) inflateview.findViewById(R.id.viewOption4);
            Switch swOption1 = (Switch) inflateview.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) inflateview.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) inflateview.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) inflateview.findViewById(R.id.swOption4);

            final ItemQuestion itemQuestion = mList.get(position);
            int id = position+1;
            tvQNo.setText(id+".");
            tvQuestion.setText(itemQuestion.getQuestion());
            tvOption1.setText(itemQuestion.getOption1());
            tvOption2.setText(itemQuestion.getOption2());
            tvOption3.setText(itemQuestion.getOption3());
            tvOption4.setText(itemQuestion.getOption4());
            viewOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttempted(position,"option1")){
                        obj_adapter.setAttampted(position,false);
                        mList.get(position).setMy_answer("");
                        setUnChecked(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }else {
                        obj_adapter.setAttampted(position,true);
                        mList.get(position).setMy_answer("option1");
                        setCheckedOption1(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }
                }
            });

            viewOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttempted(position,"option2")){
                        obj_adapter.setAttampted(position,false);
                        mList.get(position).setMy_answer("");
                        setUnChecked(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }else {
                        obj_adapter.setAttampted(position,true);
                        mList.get(position).setMy_answer("option2");
                        setCheckedOption2(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }

                }
            });
            viewOption3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttempted(position,"option3")){
                        obj_adapter.setAttampted(position,false);
                        mList.get(position).setMy_answer("");
                        setUnChecked(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }else {
                        obj_adapter.setAttampted(position,true);
                        mList.get(position).setMy_answer("option3");
                        setCheckedOption3(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }

                }
            });
            viewOption4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttempted(position,"option4")){
                        obj_adapter.setAttampted(position,false);
                        mList.get(position).setMy_answer("");
                        setUnChecked(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }else {
                        obj_adapter.setAttampted(position,true);
                        mList.get(position).setMy_answer("option4");
                        setCheckedOption4(inflateview);
                        notifyDataSetChanged();
                        updateViewPagerPos(position);
                    }

                }
            });
            if(itemQuestion.getMy_answer()!=null){
                if(itemQuestion.getMy_answer().equalsIgnoreCase("option1")){
                    setCheckedOption2(inflateview);
                }else if(itemQuestion.getMy_answer().equalsIgnoreCase("option2")){
                    setCheckedOption2(inflateview);
                }else if(itemQuestion.getMy_answer().equalsIgnoreCase("option3")){
                    setCheckedOption3(inflateview);
                }else if(itemQuestion.getMy_answer().equalsIgnoreCase("option4")){
                    setCheckedOption4(inflateview);
                }
            }
            container.addView(inflateview);
            return inflateview;
        }
        public void setCheckedOption1(View view){
            Switch swOption1 = (Switch) view.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) view.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) view.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) view.findViewById(R.id.swOption4);
            swOption1.setChecked(true);
            swOption2.setChecked(false);
            swOption3.setChecked(false);
            swOption4.setChecked(false);
        }
        public void setCheckedOption2(View view){
            Switch swOption1 = (Switch) view.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) view.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) view.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) view.findViewById(R.id.swOption4);
            swOption1.setChecked(false);
            swOption2.setChecked(true);
            swOption3.setChecked(false);
            swOption4.setChecked(false);
        }public void setCheckedOption3(View view){
            Switch swOption1 = (Switch) view.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) view.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) view.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) view.findViewById(R.id.swOption4);
            swOption1.setChecked(false);
            swOption2.setChecked(false);
            swOption3.setChecked(true);
            swOption4.setChecked(false);
        }
        public void setCheckedOption4(View view){
            Switch swOption1 = (Switch) view.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) view.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) view.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) view.findViewById(R.id.swOption4);
            swOption1.setChecked(false);
            swOption2.setChecked(false);
            swOption3.setChecked(false);
            swOption4.setChecked(true);
        }
        public void setUnChecked(View view){
            Switch swOption1 = (Switch) view.findViewById(R.id.swOption1);
            Switch swOption2 = (Switch) view.findViewById(R.id.swOption2);
            Switch swOption3 = (Switch) view.findViewById(R.id.swOption3);
            Switch swOption4 = (Switch) view.findViewById(R.id.swOption4);
            swOption1.setChecked(false);
            swOption2.setChecked(false);
            swOption3.setChecked(false);
            swOption4.setChecked(false);
        }
        public boolean isAttempted(int pos, String option){
            if(obj_adapter.isAttempted(pos)){
                ItemQuestion itemQuestion = mList.get(pos);
                if(itemQuestion!=null){
                    if(itemQuestion.getMy_answer().equalsIgnoreCase(option)){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }

            }else {
                return false;
            }
        }
        public void updateViewPagerPos(final int position){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    int pos = position+1;
                    mViewPager.setCurrentItem(pos,true);
                }
            }, 200);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
