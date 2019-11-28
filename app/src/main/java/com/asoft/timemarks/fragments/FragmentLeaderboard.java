package com.asoft.timemarks.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterLeaderBoard;
import com.asoft.timemarks.models.ItemLeaderBoard;
import com.asoft.timemarks.models.response.ResGetLeaderboard;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class FragmentLeaderboard extends BaseFragment  {
    AdapterLeaderBoard obj_adapter;
    public static ArrayList<ItemLeaderBoard> mList;
    View viewNoInternet;
    AVLoadingIndicatorView progressBar;

    TextView tvMsg;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_test_series, container, false);

        mList = new ArrayList<ItemLeaderBoard>();

        tvMsg = (TextView) fragmentView.findViewById(R.id.tvMsg);

        RecyclerView recycler_view = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapter = new AdapterLeaderBoard(mList,getActivity());
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(obj_adapter);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

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
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResGetLeaderboard> call = apiService.getLeaderboard(Util.getUserId(getActivity()));
        call.enqueue(new Callback<ResGetLeaderboard>() {
            @Override
            public void onResponse(Call<ResGetLeaderboard> call, Response<ResGetLeaderboard> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("res",statusCode+"");
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
                Log.e(getActivity().getClass().getName(), t.toString());
            }
        });
    }
}