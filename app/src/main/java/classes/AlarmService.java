package classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.media.Ringtone;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import emplogtech.com.mytimesheet.R;
import emplogtech.com.mytimesheet.activities.MainActivity;

/**
 * Created by Abdoul on 25-04-2018.
 */

public class AlarmService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    Ringtone ringtoneAlarm;
    Handler requestHandler = new Handler(Looper.getMainLooper());
    public static final int DAILY_REMINDER_REQUEST_CODE=100;
    public static final int DAILY_OUT_REMINDER_REQUEST_CODE=101;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        this.context = this;
        this.isRunning = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context, "notify_001");//new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Reminder to clock")
                .setContentText("Please remember to clock")
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Clock reminder channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(100, notification);
        startForeground(100, notification);

        }


        //Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();

    }

    public  void killService(){

        if(!this.isRunning){
            Toast.makeText(this, "Service Not running", Toast.LENGTH_LONG).show();
        }else{
            stopForeground(true);
            stopSelf();

        }


    }

    private Runnable clockIN = new Runnable() {
        public void run() {
            // Do something here
            try {
                //Notification here

                NotificationScheduler.showNotification(context, MainActivity.class,
                        "clock-IN Reminder", "Please remember to Clock IN :-)",DAILY_REMINDER_REQUEST_CODE);


            } catch (Exception err) {
                err.printStackTrace();
            }
            stopSelf();
        }
    };


    private Runnable clockOut = new Runnable() {
        public void run() {
            // Do something here
            try {
                //Notification for Clock out here

                NotificationScheduler.showNotification(context, MainActivity.class,
                        "clock-OUT Reminder", "Please remember to Clock OUT :-)",DAILY_OUT_REMINDER_REQUEST_CODE);


            } catch (Exception err) {
                err.printStackTrace();
            }
            stopSelf();
        }
    };



    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {

        if(!this.isRunning){

            this.isRunning = true;

            Bundle extras= intent.getExtras();
            if(extras !=null) {
                int code = extras.getInt("requestCode");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                if(code == DAILY_REMINDER_REQUEST_CODE){

                    requestHandler.post(clockIN);

                }
                else if(code == DAILY_OUT_REMINDER_REQUEST_CODE){


                    requestHandler.post(clockOut);
                }

                }


            }
            //requestHandler.post(myTask);
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
       // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }
}
