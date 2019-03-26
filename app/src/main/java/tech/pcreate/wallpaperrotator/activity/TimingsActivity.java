package tech.pcreate.wallpaperrotator.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import tech.pcreate.wallpaperrotator.R;
import tech.pcreate.wallpaperrotator.service.WallpaperJobService;
import tech.pcreate.wallpaperrotator.utils.AppConstants;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TimingsActivity extends AppCompatActivity implements View.OnClickListener {

    private int user_choice = 6;
    private Button doneBtn;
    private RadioGroup radioGroup;
    private RadioButton checkedRadioBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        doneBtn = findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(this);

        radioGroup = findViewById(R.id.choicesRadioGroup);
    }

    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.timing_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.timing_key), user_choice);
        editor.apply();

        setUpJobService();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.doneBtn){
            checkedRadioBtn = findViewById(radioGroup.getCheckedRadioButtonId());
            changePreferenceValues();
        }

    }

    private void changePreferenceValues() {
        if (checkedRadioBtn.getId() == R.id.six_hours){
            user_choice = AppConstants.SIX_HOURS;
        }else if (checkedRadioBtn.getId() == R.id.twelve_hours){
            user_choice = AppConstants.TWELVE_HOURS;
        }else if (checkedRadioBtn.getId() == R.id.twenty_four_hours){
            user_choice = AppConstants.TWENTY_FOUR_HOURS;
        }else if (checkedRadioBtn.getId() == R.id.two_days){
            user_choice = AppConstants.TWO_DAYS;
        }else if (checkedRadioBtn.getId() == R.id.three_days){
            user_choice = AppConstants.THREE_DAYS;
        }else if (checkedRadioBtn.getId() == R.id.six_days){
            user_choice = AppConstants.SIX_DAYS;
        }else if (checkedRadioBtn.getId() == R.id.fortnightly){
            user_choice = AppConstants.FORNIGHTLY;
        }else{
            user_choice = AppConstants.MONTHLY;
        }
        setUpSharedPreferences();
    }

    private void setUpJobService() {
        JobInfo.Builder builder = new JobInfo.Builder(AppConstants.JOB_ID, new ComponentName(this, WallpaperJobService.class));
        builder.setPersisted(true);
        builder.setPeriodic(user_choice * 60 * 60 * 1000);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);

        JobInfo jobInfo = builder.build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);

//        Log.e("Job", "scheduled!");

        finish();
    }

}
