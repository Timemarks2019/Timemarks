package com.asoft.timemarks.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.models.ItemTransaction;

import java.util.ArrayList;

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.MyViewHolder>{
    ArrayList<ItemTransaction> mList;
    Context ctx;
    
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvAmount;
        public TextView tvTxnType;
        public TextView tvRemainingBalance;
        public TextView tvRemarks;
        public TextView tvDate;
        public TextView tvId;
        public ImageView imgCircle;
        public View view4;

        public MyViewHolder (View view){
            super(view);
            tvAmount = (TextView)view.findViewById(R.id.tvAmount);
            tvTxnType = (TextView)view.findViewById(R.id.tvTxnType);
            tvRemainingBalance = (TextView)view.findViewById(R.id.tvRemainingBalance);
            tvRemarks = (TextView)view.findViewById(R.id.tvRemarks);
            tvDate = (TextView)view.findViewById(R.id.tvDate);
            tvId = (TextView)view.findViewById(R.id.tvId);
        }
    }
    public AdapterTransactions(ArrayList<ItemTransaction> mList, Context ctx) {
        this.mList = mList;
        this.ctx = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvId.setText("Sr No. "+mList.get(position).getId());
        holder.tvDate.setText(mList.get(position).getInserted_at());
        holder.tvAmount.setText("\u20B9 "+mList.get(position).getAmount());
        holder.tvTxnType.setText(mList.get(position).getTran_type());
        holder.tvRemainingBalance.setText(mList.get(position).getRemained_balance());
        holder.tvRemarks.setText(mList.get(position).getRemarks());

    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
    public void filterList(ArrayList<ItemTransaction> filterdNames) {
        this.mList = filterdNames;
        notifyDataSetChanged();
    }
    public void updateData(ArrayList<ItemTransaction> filterdNames) {
        this.mList = filterdNames;
        notifyDataSetChanged();
    }
}