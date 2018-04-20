package Classes;


import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by Abdoul on 20-04-2018.
 */

public class LocalData {


    private static final String APP_SHARED_PREFS = "RemindMePref";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus="reminderStatus";
    private static final String outReminderStatus="outReminderStatus";
    private static final String hour="hour";
    private static final String min="min";

    private static final String outHour="outHour";
    private static final String outMin="outMin";

    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }


    public boolean getOutReminderStatus()
    {
        return appSharedPrefs.getBoolean(outReminderStatus, false);
    }

    public void setOutReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(outReminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int get_hour()
    {
        return appSharedPrefs.getInt(hour, 20);
    }

    public void set_hour(int h)
    {
        prefsEditor.putInt(hour, h);
        prefsEditor.commit();
    }


    //Clock Out Hour

    public int get_outHour()
    {
        return appSharedPrefs.getInt(outHour, 16);
    }

    public void set_outHour(int h)
    {
        prefsEditor.putInt(outHour, h);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_min()
    {
        return appSharedPrefs.getInt(min, 0);
    }

    public void set_min(int m)
    {
        prefsEditor.putInt(min, m);
        prefsEditor.commit();
    }

    //Clock Out Min
    public int get_outMin()
    {
        return appSharedPrefs.getInt(outMin, 0);
    }

    public void set_outMin(int m)
    {
        prefsEditor.putInt(outMin, m);
        prefsEditor.commit();
    }

    public void reset()
    {
        prefsEditor.clear();
        prefsEditor.commit();

    }
}
