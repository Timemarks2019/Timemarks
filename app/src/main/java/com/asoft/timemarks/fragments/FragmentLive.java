package com.asoft.timemarks.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.asoft.timemarks.activities.FrontLogin;
import com.asoft.timemarks.activities.QuizDetailActivity;
import com.asoft.timemarks.adapters.AdapterQuiz;
import com.asoft.timemarks.models.quiz.Quiz;
import com.asoft.timemarks.models.response.ResGetQuizes;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class FragmentLive extends BaseFragment {
    AdapterQuiz obj_adapter;
    ArrayList<Quiz> mList = new ArrayList<Quiz>();
    AVLoadingIndicatorView progressBar;

    TextView tvMsg;
    SwipyRefreshLayout mSwipeRefreshLayout;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_live, container, false);

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
        Log.d("subject"," "+Util.getSelectedSubject(getActivity()).getId());
        Call<ResGetQuizes> call = apiService.getQuizes(Util.getUserId(getActivity()),Util.getSelectedSubject(getActivity()).getId(),"Live");
        call.enqueue(new Callback<ResGetQuizes>() {
            @Override
            public void onResponse(Call<ResGetQuizes> call, retrofit2.Response<ResGetQuizes> response) {
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
                    }
                }
                if(mList.size()==0){
                    tvMsg.setText("There is no Live contest available at the moment.");
                }else {
                    tvMsg.setText("");
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizes> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e("CallApi", t.toString());
            }
        });
    }
}
