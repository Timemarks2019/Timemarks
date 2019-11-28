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

import com.asoft.timemarks.R;
import com.asoft.timemarks.activities.ActivityQuizAttempt;
import com.asoft.timemarks.activities.QuizDetailActivity;
import com.asoft.timemarks.models.quiz.Quiz;

import java.util.ArrayList;

public class AdapterQuiz extends RecyclerView.Adapter<AdapterQuiz.MyViewHolder>{
    ArrayList<Quiz> mList;
    Context ctx;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvStartDate;
        public TextView tvEndDate;
        public TextView tvMarks;
        public TextView tvMinutes;
        public TextView tvAmount;
        public TextView tvSubject;
        public TextView tvJoined;
        public TextView tvPrice;
        public View btnJoin;

        public MyViewHolder (View view){
            super(view);
            tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            tvStartDate = (TextView)view.findViewById(R.id.tvStartDate);
            tvEndDate = (TextView)view.findViewById(R.id.tvEndDate);
            tvMarks = (TextView)view.findViewById(R.id.tvMarks);
            tvMinutes = (TextView)view.findViewById(R.id.tvMinutes);
            tvAmount = (TextView)view.findViewById(R.id.tvAmount);
            tvSubject = (TextView)view.findViewById(R.id.tvSubject);
            tvJoined = (TextView)view.findViewById(R.id.tvJoined);
            tvPrice = (TextView)view.findViewById(R.id.tvPrice);
            btnJoin = (View)view.findViewById(R.id.btnJoin);

        }
    }
    public AdapterQuiz(ArrayList<Quiz> mList, Context ctx) {
        this.mList = mList;
        this.ctx = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_list,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Quiz mQuiz = mList.get(position);
        String title = mQuiz.getSubject_name();
        holder.tvTitle.setText(mQuiz.getChapter_name());
        holder.tvStartDate.setText("Starting "+mQuiz.getCalender_start_date());
        holder.tvSubject.setText(mQuiz.getSubject_name());
        holder.tvMarks.setText("Marks : "+mQuiz.getTotal_points());
        holder.tvMinutes.setText("Minutes : "+mQuiz.getTime());
        holder.tvJoined.setText(mQuiz.getTotal_joining()+" users joined");
        holder.tvPrice.setText("\u20B9 "+mQuiz.getAmount());

        if(mQuiz.getJoin_status()){
            if(mQuiz.getStatus().equalsIgnoreCase("Live")){
                if(mQuiz.getPlay_status()){
                    holder.tvAmount.setText("Played");
                }else {
                    holder.tvAmount.setText("Play");
                }
            }else {
                holder.tvAmount.setText("Joined");
            }
        }else {
            if(mQuiz.getStatus().equalsIgnoreCase("Open") || mQuiz.getStatus().equalsIgnoreCase("Live")){
                holder.tvAmount.setText("Join");
            }else {
                holder.tvAmount.setText("Closed");
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quiz mQuiz = mList.get(position);
                Intent intent = new Intent(ctx, QuizDetailActivity.class);
                intent.putExtra("mQuiz",mList.get(position));
                ctx.startActivity(intent);
            }
        });
        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quiz mQuiz = mList.get(position);
                if(mQuiz.getJoin_status()){
                    if(mQuiz.getStatus().equalsIgnoreCase("Live")){
                        if(mQuiz.getPlay_status()){
                            Toast.makeText(ctx,"You already played this contest!",Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent = new Intent(ctx, ActivityQuizAttempt.class);
                            intent.putExtra("mQuiz",mList.get(position));
                            ctx.startActivity(intent);
                        }
                    }else {
                        Intent intent = new Intent(ctx, QuizDetailActivity.class);
                        intent.putExtra("mQuiz",mList.get(position));
                        ctx.startActivity(intent);
                    }
                }else {
                    Intent intent = new Intent(ctx, QuizDetailActivity.class);
                    intent.putExtra("mQuiz",mList.get(position));
                    ctx.startActivity(intent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}