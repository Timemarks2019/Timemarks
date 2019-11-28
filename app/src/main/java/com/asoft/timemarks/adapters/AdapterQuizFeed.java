package com.asoft.timemarks.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.activities.FrontLogin;
import com.asoft.timemarks.activities.QuizComparisionActivity;
import com.asoft.timemarks.models.ItemQuizFeed;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vaibhavlakhera.circularprogressview.CircularProgressView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterQuizFeed extends RecyclerView.Adapter<AdapterQuizFeed.MyViewHolder>{
    ArrayList<ItemQuizFeed> mList;
    Context ctx;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private int visibleThreshold = 3;
    private OnLoadMoreListener onLoadMoreListener;
    Boolean isUserFeed;
    public AdapterQuizFeed(ArrayList<ItemQuizFeed> mList,Context ctx,RecyclerView recyclerView, Boolean isUserFeed) {
        this.mList = mList;
        this.ctx = ctx;
        this.isUserFeed = isUserFeed;
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            Log.d("onScroll","Applied");
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    Log.d("onScroll","loading : "+loading+",totalItemCount : "+totalItemCount+" , lastVisibleItem : "+lastVisibleItem);
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            Log.d("onScroll","Applied");
            final GridLayoutManager linearLayoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    Log.d("onScroll","loading : "+loading+",totalItemCount : "+totalItemCount+" , lastVisibleItem : "+lastVisibleItem);
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvCity;
        public TextView tvSubject;
        public TextView tvChapter;
        public TextView tvLikes;
        public TextView tvLevel;
        public TextView tvTime;
        public TextView tvScore;
        public TextView tvPercentage;
        public ImageView imgProfile;
        public CircularProgressView progressBar;
        public View viewLike;
        public ImageView btnLike;

        public MyViewHolder (View view){
            super(view);
            tvName = (TextView)view.findViewById(R.id.tvName);
            tvCity = (TextView)view.findViewById(R.id.tvCity);
            tvSubject = (TextView)view.findViewById(R.id.tvSubject);
            tvChapter = (TextView)view.findViewById(R.id.tvChapter);
            tvLikes = (TextView)view.findViewById(R.id.tvLikes);
            tvLevel = (TextView)view.findViewById(R.id.tvLevel);
            tvTime = (TextView)view.findViewById(R.id.tvTime);
            tvScore = (TextView)view.findViewById(R.id.tvScore);
            tvPercentage = (TextView)view.findViewById(R.id.tvPercentage);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            progressBar = (CircularProgressView) view.findViewById(R.id.progressView);
            viewLike = (View) view.findViewById(R.id.viewLike);
            btnLike = (ImageView) view.findViewById(R.id.btnLike);
        }
    }
    public class ProgressViewHolder extends MyViewHolder{
        public ProgressBar progressBar;
        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return mList.get(position) != null ? 1 : 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = null;
        if(viewType == 1){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_feed, parent, false);
            viewHolder = new MyViewHolder(layoutView);
        }else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ProgressViewHolder(layoutView);
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(holder instanceof MyViewHolder){
            if (mList.get(position) == null) {
                return;
            }
            holder.tvName.setText(mList.get(position).getDisplay_name());
            holder.tvSubject.setText(mList.get(position).getSubject_name());
            holder.tvScore.setText(mList.get(position).getTotal_right_questions()+"/"+mList.get(position).getTotal_questions());
            holder.tvChapter.setText(mList.get(position).getChapter_name());
            holder.tvLikes.setText(mList.get(position).getLike_count()+"");

            if(mList.get(position).getLike_status()){
                holder.btnLike.setImageResource(R.drawable.like_active);
            }else {
                holder.btnLike.setImageResource(R.drawable.ic_like);
            }
            holder.tvLevel.setText(mList.get(position).getLevel());
            holder.tvTime.setText(mList.get(position).getInserted_at()+" ago");

            String totalRight = mList.get(position).getTotal_right_questions();
            String totalQuestions = mList.get(position).getTotal_questions();
            if(totalRight!=null && totalQuestions!=null){
                if(totalRight.trim().length()>0 && totalQuestions.trim().length() >0 ){
                    int rightAns = Integer.parseInt(totalRight);
                    int totalQns = Integer.parseInt(totalQuestions);
                    Double cCom = ((double)rightAns/totalQns) * 100;
                    int pervalue = (int)Math.round(cCom);
                    holder.progressBar.setProgress(pervalue,true);
                }
            }
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
            requestOptions.error(R.drawable.profile_image_dummy_nw);
            Glide.with(ctx)
                    .setDefaultRequestOptions(requestOptions)
                    .load(EndPoints.BASE_IMAGE_URL+mList.get(position).getImage_user()).into(holder.imgProfile);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isUserFeed){
                        return;
                    }
                    if(Util.isUserLoggedIn(ctx)){
                        Intent intent = new Intent(ctx, QuizComparisionActivity.class);
                        intent.putExtra("itemQuizFeed",mList.get(position));
                        ctx.startActivity(intent);
                    }else {
                        Toast.makeText(ctx,"Please Login first to continue!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ctx, FrontLogin.class);
                        ctx.startActivity(intent);
                    }
                }
            });

            holder.viewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!Util.isUserLoggedIn(ctx)){
                        Toast.makeText(ctx,"Please Login first to continue!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ctx, FrontLogin.class);
                        ctx.startActivity(intent);
                        return;
                    }
                    if(mList.get(position).getLike_status()){
                        holder.btnLike.setImageResource(R.drawable.ic_like);
                    }else {
                        holder.btnLike.setImageResource(R.drawable.like_active);
                    }
                    String examCode = mList.get(position).getUnique_exam_code();
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<Result> call = apiService.likePost(Util.getUserId(ctx),examCode);
                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            int statusCode = response.code();
                            Log.d("res",statusCode+"");
                            if(response.isSuccessful()){
                                if(response.body().getStatus()){
                                    Toast.makeText(ctx,response.body().getMsg(),Toast.LENGTH_LONG).show();
                                    if(mList.get(position).getLike_status()){
                                        mList.get(position).setLike_status(false);
                                        int counts = mList.get(position).getLike_count();
                                        counts--;
                                        mList.get(position).setLike_count(counts);
                                    }else {
                                        mList.get(position).setLike_status(true);
                                        int counts = mList.get(position).getLike_count();
                                        counts++;
                                        mList.get(position).setLike_count(counts);
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(ctx.getClass().getName(), t.toString());
                        }
                    });
                }
            });
        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public void setLoaded() {
        loading = false;
    }
}