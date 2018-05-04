package classes;

/**
 * Created by Abdoul on 08-03-2018.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import emplogtech.com.mytimesheet.activities.Login;

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "myTimeSheet";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // FullName address (make variable public to access from outside)
    public static final String KEY_FULLNAME = "fullName";

    public static final String KEY_TOKEN = "token";
    public static final String KEY_DEPTID = "deptID";
    public static final String KEY_UID = "id";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String fullName,String token, String uID,String deptID){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing user id in pref
        editor.putString(KEY_UID, uID);
        // Storing name in pref
        editor.putString(KEY_FULLNAME, fullName);
        //storing company ID in pref
        editor.putString(KEY_TOKEN,token);
        editor.putString(KEY_DEPTID,deptID);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // user email id
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, null));

        //user id

        user.put(KEY_UID,pref.getString(KEY_UID, null));


        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}


