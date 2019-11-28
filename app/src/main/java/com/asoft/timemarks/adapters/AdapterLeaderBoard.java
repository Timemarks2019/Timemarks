package com.asoft.timemarks.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.asoft.timemarks.BuildConfig;
import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.models.ItemLeaderBoard;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class AdapterLeaderBoard extends RecyclerView.Adapter<AdapterLeaderBoard.MyViewHolder> {
    ArrayList<ItemLeaderBoard> mList;
    Context ctx;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvPoints;
        public TextView tvRank;
        public ImageView imgProfile;

        public MyViewHolder (View view){
            super(view);
            tvName = (TextView)view.findViewById(R.id.tvName);
            tvPoints = (TextView)view.findViewById(R.id.tvPoints);
            tvRank = (TextView)view.findViewById(R.id.tvRank);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        }
    }

    public AdapterLeaderBoard(ArrayList<ItemLeaderBoard> mList, Context ctx) {
        this.mList = mList;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leader_board,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ItemLeaderBoard itemTopSites = mList.get(position);
        holder.tvName.setText(itemTopSites.getName());
        int rank = position+1;
        holder.tvRank.setText("#"+rank);
        holder.tvPoints.setText(itemTopSites.getTotal_points()+" Marks");

        String imgurl = mList.get(position).getImage_user();
        Log.d("imgurl",""+imgurl);
        if(!isURL(imgurl)){
            imgurl = BuildConfig.Base_URL+"uploads/"+imgurl;
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
        requestOptions.error(R.drawable.profile_image_dummy_nw);
        Glide.with(ctx)
                .setDefaultRequestOptions(requestOptions)
                .load(imgurl).into(holder.imgProfile);
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static boolean isURL(String text) {
        if(text==null){
            return false;
        }

        if (text.startsWith("http") || text.startsWith("https")) {
            return true;
        }else {
            return false;
        }
    }
}
