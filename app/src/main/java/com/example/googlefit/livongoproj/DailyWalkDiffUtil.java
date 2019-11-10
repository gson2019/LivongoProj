package com.example.googlefit.livongoproj;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.googlefit.livongoproj.model.DailyWalk;

import java.util.ArrayList;

public class DailyWalkDiffUtil extends DiffUtil.Callback {
    ArrayList<DailyWalk> oldDailyWalks;
    ArrayList<DailyWalk> newDailyWalks;

    public DailyWalkDiffUtil(ArrayList<DailyWalk> oldDailyWalks, ArrayList<DailyWalk> newDailyWalks){
        this.oldDailyWalks = oldDailyWalks;
        this.newDailyWalks = newDailyWalks;
    }
    @Override
    public int getOldListSize() {
        return oldDailyWalks != null ? oldDailyWalks.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newDailyWalks != null ? newDailyWalks.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        DailyWalk oldDailyWalk = oldDailyWalks.get(oldItemPosition);
        DailyWalk newDailyWalk = newDailyWalks.get(newItemPosition);
        return oldDailyWalk.getDate().equals(newDailyWalk.getDate());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDailyWalks.get(oldItemPosition).compareTo(newDailyWalks.get(newItemPosition)) == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        DailyWalk newWalk = newDailyWalks.get(newItemPosition);
        DailyWalk oldWalk = oldDailyWalks.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (!newWalk.getDate().equals(oldWalk.getDate())) {
            diff.putString("date", newWalk.getDate());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
    }
}
