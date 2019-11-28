package com.asoft.timemarks.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.models.ItemQnNumber;

import java.util.ArrayList;

public class AdapterQnNumber extends RecyclerView.Adapter<AdapterQnNumber.MyViewHolder>{
    ArrayList<ItemQnNumber> mList;
    Context ctx;
    public int selectedPos;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvQnNo;
        public View viewQnNumber;

        public MyViewHolder (View view){
            super(view);
            tvQnNo = (TextView)view.findViewById(R.id.tvQnNo);
            viewQnNumber = (View) view.findViewById(R.id.viewQnNumber);
        }
    }
    public AdapterQnNumber(ArrayList<ItemQnNumber> mList, int selectedPos, Context ctx) {
        this.mList = mList;
        this.selectedPos = selectedPos;
        this.ctx = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_number,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvQnNo.setText(mList.get(position).getQnNumber());
        if(selectedPos==position){
            holder.viewQnNumber.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_selected));
            holder.tvQnNo.setTextColor(ctx.getResources().getColor(R.color.white));
        }else {
            if(mList.get(position).isAttempted()){
                holder.viewQnNumber.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_blue));
                holder.tvQnNo.setTextColor(ctx.getResources().getColor(R.color.white));
            }else {
                holder.viewQnNumber.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_white));
                holder.tvQnNo.setTextColor(ctx.getResources().getColor(R.color.black));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setAttampted(int pos, boolean isAttampted) {
        mList.get(pos).setAttempted(isAttampted);
        notifyDataSetChanged();
    }
    public Boolean isAttempted(int pos) {
        return mList.get(pos).isAttempted();
    }
}