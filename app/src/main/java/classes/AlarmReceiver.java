package classes;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


        Log.d(TAG, "onReceive: ");
        //Toast.makeText(context,"Receiver called on receive",Toast.LENGTH_SHORT).show();
        Intent background = new Intent(context, AlarmService.class);

        //Trigger the notification

        Bundle extras= intent.getExtras();
        if(extras !=null) {
            int code = extras.getInt("requestCode");
            Log.d(TAG, String.valueOf(code));



            if(code == DAILY_REMINDER_REQUEST_CODE && checkDay()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    background.putExtra("requestCode",code);
                    context.startForegroundService(background);
                }else{
                    background.putExtra("requestCode",code);
                    context.startService(background);
                }
               /* NotificationScheduler.showNotification(context, MainActivity.class,
                        "Reminder to clock IN", "Please Click to Clock IN",DAILY_REMINDER_REQUEST_CODE);*/
            }
            else if(code == DAILY_OUT_REMINDER_REQUEST_CODE && checkDay()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    background.putExtra("requestCode",code);
                    context.startForegroundService(background);
                }else{
                    background.putExtra("requestCode",code);
                    context.startService(background);
                }

                /*NotificationScheduler.showNotification(context, MainActivity.class,
                        "Reminder to clock OUT", "Please Click to clock OUT",DAILY_OUT_REMINDER_REQUEST_CODE);*/
            }
        }


        if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {

            // Set the alarm here.
            Log.d(TAG, "onReceive: BOOT_COMPLETED");
            //Toast.makeText(context,"Receiver called After Boot completed",Toast.LENGTH_SHORT).show();
            LocalData localData = new LocalData(context);
            NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min(),DAILY_REMINDER_REQUEST_CODE);
            NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_outHour(), localData.get_outMin(),DAILY_OUT_REMINDER_REQUEST_CODE);
            return;

        }

       /* if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                LocalData localData = new LocalData(context);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min(),DAILY_REMINDER_REQUEST_CODE);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_outHour(), localData.get_outMin(),DAILY_OUT_REMINDER_REQUEST_CODE);
                return;
            }
        }*/



        //NotificationScheduler.showNotification(context, MainActivity.class,
               // "Reminder to clock", "Please Remember to clock");


       /* String action = intent.getAction();

        if(YES_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed YES");

        } else if(NO_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed NO");

        }*/


    }

    public boolean checkDay(){
        boolean dayOfWeek;
        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();
        String weekday = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        if(weekday.equals("Sat")){
            dayOfWeek = false;
        }else if(weekday.equals("Sun")){
            dayOfWeek = false;
        }else{
            dayOfWeek = true;
        }
            return dayOfWeek;
    }
}

