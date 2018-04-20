package emplogtech.com.mytimesheet.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Classes.AlarmReceiver;
import Classes.LocalData;
import Classes.NotificationScheduler;
import emplogtech.com.mytimesheet.R;

public class Reminder extends AppCompatActivity {

    String TAG = "RemindMe";
    LocalData localData;

    SwitchCompat reminderSwitch,outReminderSwitch;
    TextView tvTime,tvOutTime;

    LinearLayout ll_set_time, ll_set_outTime;
    public static final int DAILY_REMINDER_REQUEST_CODE=100;
    public static final int DAILY_OUT_REMINDER_REQUEST_CODE=101;

    int hour, min, outHour, outMin;

    ClipboardManager myClipboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);


        localData = new LocalData(getApplicationContext());

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ll_set_time = (LinearLayout) findViewById(R.id.ll_set_time);
        ll_set_outTime = (LinearLayout)findViewById(R.id.ll_set_out_time);



        tvTime = (TextView) findViewById(R.id.tv_reminder_time_desc);
        tvOutTime = (TextView) findViewById(R.id.tv_outReminder_time_desc);

        reminderSwitch = (SwitchCompat) findViewById(R.id.timerSwitch);
        outReminderSwitch = (SwitchCompat)findViewById(R.id.timerSwitchOut);

        hour = localData.get_hour();
        min = localData.get_min();

        outHour = localData.get_outHour();
        outMin = localData.get_outMin();

        tvTime.setText(getFormatedTime(hour, min));
        tvOutTime.setText(getFormatedTime(outHour,outMin));

        reminderSwitch.setChecked(localData.getReminderStatus());
        outReminderSwitch.setChecked(localData.getOutReminderStatus());



        if (!localData.getReminderStatus()){
            ll_set_time.setAlpha(0.4f);
        }

            if(!localData.getOutReminderStatus()){
                ll_set_outTime.setAlpha(0.4f);
            }

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localData.setReminderStatus(isChecked);
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: true");
                    NotificationScheduler.setReminder(Reminder.this, AlarmReceiver.class, localData.get_hour(), localData.get_min(),DAILY_REMINDER_REQUEST_CODE);
                    ll_set_time.setAlpha(1f);
                } else {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(Reminder.this, AlarmReceiver.class,DAILY_REMINDER_REQUEST_CODE);
                    ll_set_time.setAlpha(0.4f);
                }

            }
        });


        outReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localData.setOutReminderStatus(isChecked);
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: true");
                    NotificationScheduler.setReminder(Reminder.this, AlarmReceiver.class, localData.get_hour(), localData.get_min(),DAILY_OUT_REMINDER_REQUEST_CODE);
                    ll_set_outTime.setAlpha(1f);
                } else {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(Reminder.this, AlarmReceiver.class,DAILY_OUT_REMINDER_REQUEST_CODE);
                    ll_set_outTime.setAlpha(0.4f);
                }
            }
        });

        ll_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localData.getReminderStatus())
                    showTimePickerDialog(localData.get_hour(), localData.get_min(),DAILY_REMINDER_REQUEST_CODE,"IN");
            }
        });

        ll_set_outTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localData.getOutReminderStatus())
                    showTimePickerDialog(localData.get_outHour(), localData.get_outMin(),DAILY_OUT_REMINDER_REQUEST_CODE,"OUT");

            }
        });


    }

    private void showTimePickerDialog(int h, int m, final int reqCode, final String status) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        Log.d(TAG, "onTimeSet: hour " + hour);
                        Log.d(TAG, "onTimeSet: min " + min);
                        localData.set_hour(hour);
                        localData.set_min(min);
                        if(status == "IN"){tvTime.setText(getFormatedTime(hour, min));}
                        if(status =="OUT"){tvOutTime.setText(getFormatedTime(hour, min));}
                        NotificationScheduler.setReminder(Reminder.this, AlarmReceiver.class, localData.get_hour(), localData.get_min(),reqCode);
                        showToast("Reminder set for "+getFormatedTime(hour,min));


                    }
                }, h, m, false);

        builder.setCustomTitle(view);
        builder.show();

    }

    public String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }


    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
