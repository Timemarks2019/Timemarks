package com.asoft.timemarks.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.activities.ActivityQuizChapters;
import com.asoft.timemarks.adapters.AdapterQuizSubjects;
import com.asoft.timemarks.models.quiz.ItemSubject;
import com.asoft.timemarks.models.response.ResGetSubjects;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;

public class FragmentQuiz extends BaseFragment {
    AdapterQuizSubjects obj_adapter;
    public static ArrayList<ItemSubject> mList;

    View viewNoInternet;
    AVLoadingIndicatorView progressBar;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_test_series, container, false);

        mList= new ArrayList<ItemSubject>();

        RecyclerView recycler_view = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuizSubjects(mList,getActivity());
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setAdapter(obj_adapter);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ActivityQuizChapters.class);
                intent.putExtra("subject",mList.get(position).getId());
                intent.putExtra("subject_name",mList.get(position).getSubject_name());
                intent.putExtra("title",mList.get(position).getSubject_name());
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        progressBar = (AVLoadingIndicatorView) fragmentView.findViewById(R.id.progressBar);
        viewNoInternet = (View) fragmentView.findViewById(R.id.viewNoInternet);
        Button btnRetry = (Button) fragmentView.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Util.isConnected(getActivity())){
                    viewNoInternet.setVisibility(VISIBLE);
                }else {
                    viewNoInternet.setVisibility(View.GONE);
                }
                getData();
            }
        });
        if(!Util.isConnected(getActivity())){
            viewNoInternet.setVisibility(VISIBLE);
        }else {
            viewNoInternet.setVisibility(View.GONE);
            getData();
        }
        return fragmentView;
    }
    private void getData() {
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResGetSubjects> call = apiService.getQuizSubjects();
        call.enqueue(new Callback<ResGetSubjects>() {
            @Override
            public void onResponse(Call<ResGetSubjects> call, retrofit2.Response<ResGetSubjects> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("resgetData",statusCode+"");
                if(response.isSuccessful()){
                    ArrayList<ItemSubject> mListRaw = response.body().getList();
                    Log.d("mListRaw"," - "+mListRaw.size());
                    if(mListRaw!=null){
                        mList.clear();
                        mList.addAll(mListRaw);
                        obj_adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetSubjects> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                Log.e("CallApi", t.toString());
            }
        });
    }
}
