package com.example.googlefit.livongoproj.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlefit.livongoproj.DailyWalkDiffUtil;
import com.example.googlefit.livongoproj.R;
import com.example.googlefit.livongoproj.model.DailyWalk;

import java.util.ArrayList;
import java.util.Collections;

public class DailyWalkListAdapter extends RecyclerView.Adapter<DailyWalkListAdapter.DailyWalkHolder>{
    private LayoutInflater inflater;
    private ArrayList<DailyWalk> dailyWalks;
    private Context mContext;

    public DailyWalkListAdapter(Context context, ArrayList<DailyWalk> dailyWalks){
        this.inflater = LayoutInflater.from(context);
        this.dailyWalks = dailyWalks;
        mContext = context;
    }

    @NonNull
    @Override
    public DailyWalkListAdapter.DailyWalkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_item_dailywalk, parent, false);
        DailyWalkHolder holder = new DailyWalkHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWalkListAdapter.DailyWalkHolder holder, int position) {
        int stepCount = dailyWalks.get(position).getStepCounter();
        String startTime = dailyWalks.get(position).getStartTimeStr();
        String endTime = dailyWalks.get(position).getEndTimeStr();
        String date = dailyWalks.get(position).getDate();
        holder.stepTextView.setText(stepCount + " Steps");
        holder.dateTextView.setText(date + "   "+ startTime + " to " + endTime);
    }

    @Override
    public int getItemCount() {
        return dailyWalks.size();
    }

    public class DailyWalkHolder extends RecyclerView.ViewHolder {
        TextView stepTextView;
        TextView dateTextView;
        public DailyWalkHolder(@NonNull View itemView) {
            super(itemView);
            stepTextView = itemView.findViewById(R.id.stepText);
            dateTextView = itemView.findViewById(R.id.dateText);
        }
    }

    public void updateDailyWalkList(ArrayList<DailyWalk> dailyWalks) {
        final DailyWalkDiffUtil diffCallback = new DailyWalkDiffUtil(this.dailyWalks, dailyWalks);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.dailyWalks.clear();
        this.dailyWalks.addAll(dailyWalks);
        diffResult.dispatchUpdatesTo(this);
    }
}
