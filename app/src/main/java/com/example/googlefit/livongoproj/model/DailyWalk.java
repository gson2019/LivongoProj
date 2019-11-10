package com.example.googlefit.livongoproj.model;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class DailyWalk implements Comparable{
    private int stepCounter;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public DailyWalk(long startTime, long endTime, int stepCounter) {
        this.stepCounter = stepCounter;
        this.startTime = startTime;
        this.endTime = endTime;
        String[] startTimeInfo = sdf.format(startTime).split(" ");
        String[] endTimeInfo = sdf.format(endTime).split(" ");
        date = startTimeInfo[0];
        startTimeStr = startTimeInfo[1];
        endTimeStr = endTimeInfo[1];
    }

    public String getDate() {
        return date;
    }

    public int getStepCounter() {
        return stepCounter;
    }


    public long getStartTime() {
        return startTime;
    }


    public long getEndTime() {
        return endTime;
    }

    private String date;
    private long startTime;
    private String startTimeStr;
    private String endTimeStr;
    private long endTime;

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    @Override
    public int compareTo(Object o) {
        DailyWalk dailyWalk = (DailyWalk)o;
        if(dailyWalk.stepCounter == this.stepCounter && dailyWalk.startTime == this.startTime && dailyWalk.endTime == this.endTime){
            return 0;
        }
        return 1;
    }
}
