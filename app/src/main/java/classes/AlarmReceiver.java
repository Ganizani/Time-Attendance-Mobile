package classes;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import emplogtech.com.mytimesheet.activities.MainActivity;

/**
 * Created by Abdoul on 20-04-2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";
    String YES_ACTION = "YES_ACTION";
    String NO_ACTION = "NO_ACTION";
    public static final int DAILY_REMINDER_REQUEST_CODE=100;
    public static final int DAILY_OUT_REMINDER_REQUEST_CODE=101;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                LocalData localData = new LocalData(context);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min(),DAILY_REMINDER_REQUEST_CODE);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_outHour(), localData.get_outMin(),DAILY_OUT_REMINDER_REQUEST_CODE);
                return;
            }
        }

        Log.d(TAG, "onReceive: ");

        //Trigger the notification

        Bundle extras= intent.getExtras();
        if(extras !=null) {
            int code = extras.getInt("requestCode");
            Log.d(TAG, String.valueOf(code));

            if(code == DAILY_REMINDER_REQUEST_CODE){

                NotificationScheduler.showNotification(context, MainActivity.class,
                         "Reminder to clock IN", "Please Click to Clock IN",DAILY_REMINDER_REQUEST_CODE);
            }
            else if(code == DAILY_OUT_REMINDER_REQUEST_CODE){

                NotificationScheduler.showNotification(context, MainActivity.class,
                        "Reminder to clock OUT", "Please Click to clock OUT",DAILY_OUT_REMINDER_REQUEST_CODE);
            }
        }

        //NotificationScheduler.showNotification(context, MainActivity.class,
               // "Reminder to clock", "Please Remember to clock");


       /* String action = intent.getAction();

        if(YES_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed YES");

        } else if(NO_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed NO");

        }*/


    }
}

