package com.example.googlefit.livongoproj.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlefit.livongoproj.R;
import com.example.googlefit.livongoproj.model.DailyWalk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private GoogleSignInAccount gsa;
    private boolean toggle_chronological = false;
    private FitnessOptions fitnessOptions;
    private RecyclerView mRecyclerView;
    private ArrayList<DailyWalk> mDailyWalks;
    private DailyWalkListAdapter dailyWalkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDailyWalks = new ArrayList<>();
        fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        gsa = GoogleSignIn.getLastSignedInAccount(this);
        if (!GoogleSignIn.hasPermissions(gsa, fitnessOptions)) {
            requestPermission();
        } else {
            subscribe();
        }
    }

    private void requestPermission(){
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    readData();
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        Log.d(TAG, sdf.format(endTime));
        cal.add(Calendar.DAY_OF_YEAR, -13);
        final long startTime = cal.getTimeInMillis();
        Log.d(TAG, sdf.format(startTime));
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this,  GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                Log.d(TAG, "Got days");
                mDailyWalks.clear();
                List<Bucket> buckets = dataReadResponse.getBuckets();
                for(Bucket bucket : buckets){
                    List<DataSet> bucketDataSet = bucket.getDataSets();
                    for(DataSet dataSet : bucketDataSet){
                        for(int i = dataSet.getDataPoints().size()-1; i>= 0; i--){
                            DataPoint dp = dataSet.getDataPoints().get(i);
                            for (Field field : dp.getDataType().getFields()) {
                                mDailyWalks.add(new DailyWalk(dp.getStartTime(TimeUnit.MILLISECONDS), dp.getEndTime(TimeUnit.MILLISECONDS), dp.getValue(field).asInt()));
                            }
                        }
                    }
                }
                if(mDailyWalks.size()== 0){
                    Toast.makeText(MainActivity.this, "No Daily Walk data record", Toast.LENGTH_LONG).show();
                }else {
                    dailyWalkListAdapter = new DailyWalkListAdapter(MainActivity.this, mDailyWalks);
                    mRecyclerView.setAdapter(dailyWalkListAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read_data:
                if (GoogleSignIn.getLastSignedInAccount(this) == null) {
                    requestPermission();
                } else {
                    readData();
                    if(toggle_chronological) {
                        toggle_chronological = !toggle_chronological;
                    }
                }
                return true;
            case R.id.action_update_data:
                if (GoogleSignIn.getLastSignedInAccount(this) == null) {
                    requestPermission();
                } else {
                    if(dailyWalkListAdapter != null && !this.mDailyWalks.isEmpty()) {
                        ArrayList<DailyWalk> newDailyWalks = new ArrayList<>(this.mDailyWalks);
                        // Conditionally sort the dailywalk list based on toggle_chronological value
                        Collections.sort(newDailyWalks, new Comparator<DailyWalk>() {
                            @Override
                            public int compare(DailyWalk dailywalk1, DailyWalk dailywalk2) {
                                Calendar cal1 = Calendar.getInstance();
                                Calendar cal2 = Calendar.getInstance();
                                cal1.setTimeInMillis(dailywalk1.getStartTime());
                                cal2.setTimeInMillis(dailywalk2.getStartTime());
                                if (toggle_chronological) {
                                    return cal1.compareTo(cal2);
                                } else {
                                    return cal2.compareTo(cal1);
                                }
                            }
                        });
                        toggle_chronological = !toggle_chronological;
                        dailyWalkListAdapter.updateDailyWalkList(newDailyWalks);
                    }else{
                        Toast.makeText(MainActivity.this, "No Daily Walk data record", Toast.LENGTH_LONG).show();
                    }
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
