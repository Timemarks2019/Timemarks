package com.asoft.timemarks.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.EndPoints;
import com.asoft.timemarks.models.quiz.ItemSubject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdapterQuizSubjects extends RecyclerView.Adapter<AdapterQuizSubjects.MyViewHolder>{
    ArrayList<ItemSubject> mList;
    Context ctx;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private ItemClickListener mClickListener;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvDesc;
        public ImageView imgPreview;

        public MyViewHolder (View view){
            super(view);
            tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            tvDesc = (TextView)view.findViewById(R.id.tvDesc);
            imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
        }
    }
    public AdapterQuizSubjects(ArrayList<ItemSubject> mList, Context ctx) {
        this.mList = mList;
        this.ctx = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_new,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ItemSubject quizSubject = mList.get(position);
        String title = quizSubject.getSubject_name();
        holder.tvTitle.setText(quizSubject.getSubject_name());
        holder.tvDesc.setText(quizSubject.getTotal_chapters()+" Quizes");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
        requestOptions.error(R.drawable.profile_image_dummy_nw);
        Glide.with(ctx)
                .setDefaultRequestOptions(requestOptions)
                .load(EndPoints.BASE_IMAGE_URL+mList.get(position).getImage()).into(holder.imgPreview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, position, mList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void filterList(ArrayList<ItemSubject> filterdNames) {
        this.mList = filterdNames;
        notifyDataSetChanged();
    }
    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, ItemSubject item);
    }

}