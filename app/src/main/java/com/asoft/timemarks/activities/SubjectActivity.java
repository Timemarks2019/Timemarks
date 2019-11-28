package com.asoft.timemarks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.ItemOffsetDecoration;
import com.asoft.timemarks.Utils.RecyclerTouchListener;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.adapters.AdapterQuizSubjects;
import com.asoft.timemarks.models.quiz.ItemSubject;
import com.asoft.timemarks.models.response.ResGetSubjects;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class SubjectActivity extends AppCompatActivity implements AdapterQuizSubjects.ItemClickListener{
    AdapterQuizSubjects obj_adapter;
    ArrayList<ItemSubject> mList = new ArrayList<ItemSubject>();
    AVLoadingIndicatorView progressBar;
    ArrayList<ItemSubject> filterdList = new ArrayList<ItemSubject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        progressBar = (AVLoadingIndicatorView) findViewById(R.id.progressBar);

        RecyclerView recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        obj_adapter = new AdapterQuizSubjects(mList,SubjectActivity.this);
        recycler_view.setLayoutManager(new GridLayoutManager(SubjectActivity.this, 2));

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(SubjectActivity.this, R.dimen.item_offset);
        recycler_view.addItemDecoration(itemDecoration);
        obj_adapter.setClickListener(this);
        recycler_view.setAdapter(obj_adapter);
       /* recycler_view.addOnItemTouchListener(new RecyclerTouchListener(SubjectActivity.this, recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Util.saveSubject(SubjectActivity.this,filterdList.get(position));
                Intent intent = new Intent(SubjectActivity.this, FrontLogin.class);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        EditText editTextSearch = (EditText)findViewById(R.id.etSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
        getData();
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
                Log.d("resgetData",statusCode+" data : "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        ArrayList<ItemSubject> mListRaw = response.body().getList();
                        if (mListRaw != null) {
                            mList.clear();
                            filterdList.clear();
                            filterdList.addAll(mListRaw);
                            mList.addAll(mListRaw);
                            obj_adapter.notifyDataSetChanged();
                        }
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

    private void filter(String text) {
        //new array list that will hold the filtered data
        filterdList.clear();
        ArrayList<ItemSubject> filterdNames = new ArrayList<>();
        for (ItemSubject itemSubject : mList) {
            if (itemSubject.getSubject_name().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(itemSubject);
            }
        }
        filterdList.addAll(filterdNames);
        obj_adapter.filterList(filterdNames);
    }


    @Override
    public void onItemClick(View view, int position, ItemSubject item) {
        Util.saveSubject(SubjectActivity.this,item);
        Intent intent = new Intent(SubjectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
