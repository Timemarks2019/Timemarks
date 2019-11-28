package com.asoft.timemarks.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.models.quiz.ItemQuestion;

import java.util.ArrayList;

public class AdapterQuizSummary extends RecyclerView.Adapter<AdapterQuizSummary.MyViewHolder>{
    ArrayList<ItemQuestion> mList;
    Context ctx;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvQNo;
        public TextView tvQuestion;
        TextView tvOption1;
        TextView tvOption2;
        TextView tvOption3;
        TextView tvOption4;
        ImageView imgOption1;
        ImageView imgOption2;
        ImageView imgOption3;
        ImageView imgOption4;


        public MyViewHolder (View view){
            super(view);
            tvQNo = (TextView)view.findViewById(R.id.tvQNo);
            tvQuestion = (TextView)view.findViewById(R.id.tvQuestion);
            tvOption1 = (TextView) view.findViewById(R.id.tvOption1);
            tvOption2 = (TextView) view.findViewById(R.id.tvOption2);
            tvOption3 = (TextView) view.findViewById(R.id.tvOption3);
            tvOption4 = (TextView) view.findViewById(R.id.tvOption4);
            imgOption1 = (ImageView) view.findViewById(R.id.imgOption1);
            imgOption2 = (ImageView) view.findViewById(R.id.imgOption2);
            imgOption3 = (ImageView) view.findViewById(R.id.imgOption3);
            imgOption4 = (ImageView) view.findViewById(R.id.imgOption4);

        }
    }
    public AdapterQuizSummary(ArrayList<ItemQuestion> mList,Context ctx) {
        this.mList = mList;
        this.ctx = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_summary,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ItemQuestion itemQuestion = mList.get(position);
        int id = position+1;
        holder.tvQNo.setText(id+"");
        holder.tvQuestion.setText(itemQuestion.getQuestion());
        holder.tvOption1.setText(itemQuestion.getOption1());
        holder.tvOption2.setText(itemQuestion.getOption2());
        holder.tvOption3.setText(itemQuestion.getOption3());
        holder.tvOption4.setText(itemQuestion.getOption4());

        Log.d("QnAns","MyAns : "+itemQuestion.getMy_answer()+", Act Ans : "+itemQuestion.getQuestion_ans());

        if(itemQuestion.getQuestion_ans().equalsIgnoreCase("option1")){
            switch (itemQuestion.getMy_answer()) {
                case "option1":
                    holder.imgOption1.setImageResource(R.drawable.sw_green);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option2":
                    holder.imgOption1.setImageResource(R.drawable.sw_green);
                    holder.imgOption2.setImageResource(R.drawable.sw_red);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option3":
                    holder.imgOption1.setImageResource(R.drawable.sw_green);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_red);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                case "option4":
                    holder.imgOption1.setImageResource(R.drawable.sw_green);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_red);
                    break;
            }
        }else if(itemQuestion.getQuestion_ans().equalsIgnoreCase("option2")){
            switch (itemQuestion.getMy_answer()) {
                case "option1":
                    holder.imgOption1.setImageResource(R.drawable.sw_red);
                    holder.imgOption2.setImageResource(R.drawable.sw_green);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option2":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_green);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option3":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_green);
                    holder.imgOption3.setImageResource(R.drawable.sw_red);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                case "option4":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_green);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_red);
                    break;
            }
        }else if(itemQuestion.getQuestion_ans().equalsIgnoreCase("option3")){
            switch (itemQuestion.getMy_answer()) {
                case "option1":
                    holder.imgOption1.setImageResource(R.drawable.sw_red);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_green);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option2":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_red);
                    holder.imgOption3.setImageResource(R.drawable.sw_green);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                    break;
                case "option3":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_green);
                    holder.imgOption4.setImageResource(R.drawable.sw_gray);
                case "option4":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_green);
                    holder.imgOption4.setImageResource(R.drawable.sw_red);
                    break;
            }
        }else if(itemQuestion.getQuestion_ans().equalsIgnoreCase("option4")){
            switch (itemQuestion.getMy_answer()) {
                case "option1":
                    holder.imgOption1.setImageResource(R.drawable.sw_red);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_green);
                    break;
                case "option2":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_red);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_green);
                    break;
                case "option3":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_red);
                    holder.imgOption4.setImageResource(R.drawable.sw_green);
                case "option4":
                    holder.imgOption1.setImageResource(R.drawable.sw_gray);
                    holder.imgOption2.setImageResource(R.drawable.sw_gray);
                    holder.imgOption3.setImageResource(R.drawable.sw_gray);
                    holder.imgOption4.setImageResource(R.drawable.sw_green);
                    break;
            }
        }
    }
    private void switchColor(Switch mSwitch, boolean checked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSwitch.getThumbDrawable().setColorFilter(checked ? Color.BLACK : Color.WHITE, PorterDuff.Mode.MULTIPLY);
            mSwitch.getTrackDrawable().setColorFilter(!checked ? Color.BLACK : Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}