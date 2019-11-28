package com.asoft.timemarks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.activities.ActivityQuizAttempt;
import com.asoft.timemarks.activities.FrontLogin;
import com.asoft.timemarks.adapters.AdapterQuizChapters;
import com.asoft.timemarks.models.quiz.ItemChapter;
import com.asoft.timemarks.models.quiz.ItemLevel;
import com.asoft.timemarks.models.response.ResGetQuizChapters;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class FragmentQuizChapters extends BaseFragment  {

    AdapterQuizChapters obj_adapterFeatured;
    public static ArrayList<ItemChapter> mList;
    View viewNoInternet;
    AVLoadingIndicatorView progressBar;

    String subject;
    String subject_name;
    ItemLevel itemLevel;
    CardView viewLevel1,viewLevel2,viewLevel3,viewLevel4;
    TextView tvLevel1,tvLevel2,tvLevel3,tvLevel4;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chapters, container, false);

        mList = new ArrayList<ItemChapter>();
        subject = getArguments().getString("subject");
        subject_name = getArguments().getString("subject_name");

        RecyclerView recycler_view = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);
        obj_adapterFeatured = new AdapterQuizChapters(mList,getActivity(),subject_name);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(obj_adapterFeatured);
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(Util.isUserLoggedIn(getActivity())){
                    Intent intent = new Intent(getActivity(), ActivityQuizAttempt.class);
                    intent.putExtra("subject",subject_name);
                    intent.putExtra("chapter",mList.get(position).getChapter_id());
                    intent.putExtra("chapter_name",mList.get(position).getChapter_name());
                    intent.putExtra("level",mList.get(position).getLevel());
                    startActivityForResult(intent,REQ_CODE_FINISH);
                }else {
                    Toast.makeText(getActivity(),"Please Login first to continue!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), FrontLogin.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        tvLevel1 = (TextView) fragmentView.findViewById(R.id.tvLevel1);
        tvLevel2 = (TextView) fragmentView.findViewById(R.id.tvLevel2);
        tvLevel3 = (TextView) fragmentView.findViewById(R.id.tvLevel3);
        tvLevel4 = (TextView) fragmentView.findViewById(R.id.tvLevel4);

        viewLevel1 = (CardView) fragmentView.findViewById(R.id.viewLevel1);
        viewLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(itemLevel!=null){
                   ArrayList<ItemChapter> mListRaw = itemLevel.getLevel1();
                   if (mListRaw != null) {
                       mList.clear();
                       mList.addAll(mListRaw);
                       obj_adapterFeatured.notifyDataSetChanged();
                       selectLevel(1);
                   }
               }
            }
        });

        viewLevel2 = (CardView) fragmentView.findViewById(R.id.viewLevel2);
        viewLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(itemLevel!=null){
                   ArrayList<ItemChapter> mListRaw = itemLevel.getLevel2();
                   if (mListRaw != null) {
                       mList.clear();
                       mList.addAll(mListRaw);
                       obj_adapterFeatured.notifyDataSetChanged();
                       selectLevel(2);
                   }
               }
            }
        });

        viewLevel3 = (CardView) fragmentView.findViewById(R.id.viewLevel3);
        viewLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(itemLevel!=null){
                   ArrayList<ItemChapter> mListRaw = itemLevel.getLevel3();
                   if (mListRaw != null) {
                       mList.clear();
                       mList.addAll(mListRaw);
                       obj_adapterFeatured.notifyDataSetChanged();
                       selectLevel(3);
                   }
               }
            }
        });

        viewLevel4 = (CardView) fragmentView.findViewById(R.id.viewLevel4);
        viewLevel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(itemLevel!=null){
                   ArrayList<ItemChapter> mListRaw = itemLevel.getLevel4();
                   if (mListRaw != null) {
                       mList.clear();
                       mList.addAll(mListRaw);
                       obj_adapterFeatured.notifyDataSetChanged();
                       selectLevel(4);
                   }
               }
            }
        });
        selectLevel(1);

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
    private void selectLevel(int slectedLevel){
        switch (slectedLevel) {
            case 1:
                viewLevel1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                viewLevel2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvLevel1.setTextColor(getActivity().getResources().getColor(R.color.white));
                tvLevel2.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel3.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel4.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                break;

            case 2:
                viewLevel1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                viewLevel3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvLevel1.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel2.setTextColor(getActivity().getResources().getColor(R.color.white));
                tvLevel3.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel4.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                break;

            case 3:
                viewLevel1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                viewLevel4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvLevel1.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel2.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel3.setTextColor(getActivity().getResources().getColor(R.color.white));
                tvLevel4.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                break;

            case 4:
                viewLevel1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                viewLevel4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel1.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel2.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel3.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tvLevel4.setTextColor(getActivity().getResources().getColor(R.color.white));
                break;

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Results",requestCode+" - "+resultCode+" - "+data);
        if (requestCode == REQ_CODE_FINISH) {
            if (resultCode == getActivity().RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","SUCCESS");
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private void getData() {
        progressBar.setVisibility(VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d("subject"," "+subject);
        Call<ResGetQuizChapters> call = apiService.getQuizChapters(subject);
        call.enqueue(new Callback<ResGetQuizChapters>() {
            @Override
            public void onResponse(Call<ResGetQuizChapters> call, retrofit2.Response<ResGetQuizChapters> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        itemLevel = response.body().getList();
                        ArrayList<ItemChapter> mListRaw = response.body().getList().getLevel1();
                        if (mListRaw != null) {
                            mList.clear();
                            mList.addAll(mListRaw);
                            obj_adapterFeatured.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResGetQuizChapters> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.GONE);
                Log.e("CallApi", t.toString());
            }
        });
    }
}
