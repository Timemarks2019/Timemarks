package com.asoft.timemarks.fragments;

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
import com.asoft.timemarks.adapters.AdapterQuizFeed;
import com.asoft.timemarks.models.ItemQuizFeed;
import com.asoft.timemarks.models.response.ResGetQuizFeed;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;

public class FragmentQuizFeed extends BaseFragment {
    AdapterQuizFeed obj_adapter;
    public static ArrayList<ItemQuizFeed> mList;

    View viewNoInternet;
    AVLoadingIndicatorView progressBar;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_test_series, container, false);

        mList= new ArrayList<ItemQuizFeed>();

        RecyclerView recycler_view = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuizFeed(mList,getActivity(),recycler_view,false);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("user_id",""+Util.getUserId(getActivity()));
        Call<ResGetQuizFeed> call = apiService.getQuizFeed(Util.getUserId(getActivity()),"1");
        call.enqueue(new Callback<ResGetQuizFeed>() {
            @Override
            public void onResponse(Call<ResGetQuizFeed> call, retrofit2.Response<ResGetQuizFeed> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("resgetData",statusCode+"");
                if(response.isSuccessful()){
                    ArrayList<ItemQuizFeed> mListRaw = response.body().getList();
                    Log.d("mListRaw"," - "+mListRaw.size());
                    if(mListRaw!=null){
                        mList.clear();
                        mList.addAll(mListRaw);
                        obj_adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizFeed> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                Log.e("CallApi", t.toString());
            }
        });
    }
}
