package com.asoft.timemarks.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.asoft.timemarks.R;
import com.asoft.timemarks.models.quiz.ItemChapter;

import java.util.ArrayList;

public class AdapterQuizChapters extends RecyclerView.Adapter<AdapterQuizChapters.MyViewHolder>{
    ArrayList<ItemChapter> mList;
    Context ctx;
    String subject;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvTotalQuestions;
        public TextView tvAttempts;
        public TextView tvAvgTime;
        public TextView tvTotalAttempts;

        public MyViewHolder (View view){
            super(view);
            tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            tvTotalQuestions = (TextView)view.findViewById(R.id.tvTotalQuestions);
            tvAttempts = (TextView)view.findViewById(R.id.tvAttempts);
            tvAvgTime = (TextView)view.findViewById(R.id.tvAvgTime);
            tvTotalAttempts = (TextView)view.findViewById(R.id.tvTotalAttempts);

        }
    }
    public AdapterQuizChapters(ArrayList<ItemChapter> mList, Context ctx, String subject) {
        this.mList = mList;
        this.ctx = ctx;
        this.subject = subject;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_chapter,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ItemChapter itemChapter = mList.get(position);
        holder.tvTitle.setText(itemChapter.getChapter_name());
        holder.tvTotalQuestions.setText(itemChapter.getTotal_questions()+" Questions");
        holder.tvAttempts.setText(itemChapter.getMy_attempts()+" Attempts");
        String[] parts = itemChapter.getAvg_time().split("\\."); // escape .
        if(parts.length>1){
            String mins = parts[0];
            String seconds = parts[1];
            holder.tvAvgTime.setText(mins+" Mins "+seconds+" Sec");
        }else {
            holder.tvAvgTime.setText(itemChapter.getAvg_time()+" Mins ");
        }
        holder.tvTotalAttempts.setText(itemChapter.getTotal_attempts()+" Students Attempted");

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Util.isUserLoggedIn(ctx)){
                    Intent intent = new Intent(ctx, ActivityQuizAttempt.class);
                    intent.putExtra("subject",subject);
                    intent.putExtra("chapter",mList.get(position).getChapter_id());
                    intent.putExtra("chapter_name",mList.get(position).getChapter_name());
                    intent.putExtra("level",mList.get(position).getLevel());
                    ctx.startActivity(intent);
                }else {
                    Toast.makeText(ctx,"Please Login first to continue!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, FrontLogin.class);
                    ctx.startActivity(intent);
                }
            }
        });*/
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}