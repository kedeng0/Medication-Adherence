package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

import java.util.ArrayList;

/**
 * Created by Appzoro_ 4 on 10/3/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    Context context;
    private String[] questionArray;
    private ArrayList<String> answerList;
    private RecyclerView recyclerView;
    private int mExpandedPosition = -1;
    private boolean isExpanded;


    public RecyclerViewAdapter(Context context, String[] questionArray, ArrayList<String> answerList, RecyclerView recyclerView) {
        this.context = context;
        this.questionArray = questionArray;
        this.answerList = answerList;
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        final ViewGroup mainGroup = (ViewGroup) layoutInflater.inflate(R.layout.recyclerview_item_row, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(mainGroup);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.question.setText(questionArray[position]);
        holder.answer.setText(answerList.get(position));

        isExpanded = position == mExpandedPosition;
        holder.img_arrow.setImageResource(isExpanded ? R.drawable.down_arrow30 : R.drawable.next_arrow30 );
        holder.answer.setMaxLines(isExpanded ? Integer.MAX_VALUE : 0);
        holder.img_arrow.setActivated(isExpanded);

        if (isExpanded){
            holder.answer.setPadding(0,20,0,20);
            holder.question.setTextColor(Color.parseColor("#2c3693"));
        } else {
            holder.answer.setPadding(0,0,0,0);
            holder.question.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public int getItemCount() {
        return (null != questionArray ? questionArray.length : 0);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        ImageView img_arrow;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            question = (TextView) itemView.findViewById(R.id.question);
            answer = (TextView) itemView.findViewById(R.id.answer);
            img_arrow = (ImageView) itemView.findViewById(R.id.iv_arrow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExpandedPosition = isExpanded ? -1 : getAdapterPosition();
                    //TransitionManager.beginDelayedTransition(recyclerView);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
