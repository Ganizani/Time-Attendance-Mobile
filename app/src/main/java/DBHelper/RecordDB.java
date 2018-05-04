package DBHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Created by Abdoul on 22-03-2018.
 */

public class RecordDB extends SQLiteOpenHelper {

    public RecordDB(Context applicationcontext) {
        super(applicationcontext, "androidsqlite.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query,leav;
        query="CREATE TABLE record ( recID INTEGER PRIMARY KEY, user_id TEXT, user_name TEXT, " +
                "update_status TEXT, dat TEXT, lat TEXT, lng TEXT, id INTEGER, status TEXT,imei TEXT," +
                "department_id INTEGER)";
        leav = "CREATE TABLE leav (leave_id INTEGER PRIMARY KEY,leave_name TEXT)";
        database.execSQL(query);
        database.execSQL(leav);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query,leav;
        query="DROP TABLE IF EXISTS record";
        leav="DROP TABLE IF EXISTS leav";
        database.execSQL(query);
        database.execSQL(leav);
        onCreate(database);
    }

    public void insertRecord(String id, String name,String dat, String lat, String lng,int uid,String status,String imei,int shifts_id,int shift_type,int department_id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", id);
        values.put("user_name", name);
        values.put("update_status", "no");
        values.put("dat",dat);
        values.put("lat",lat);
        values.put("lng",lng);
        values.put("id",uid);
        values.put("status", status);
        values.put("imei",imei);
        values.put("department_id",department_id);
        database.insert("record", null, values);
        database.close();
    }
    public void insertLeave(int leave_id,String leave_name){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("leave_id",leave_id);
        values.put("leave_name",leave_name);
        database.insert("leav", null, values);
        database.close();
    }

    public void deleteLeave(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("leav",null,null);
        db.close();
    }

    public void deleteRecord(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("record","recID = ?",new String[]{id});
        db.close();
    }

    public ArrayList<HashMap<String, String>> getAllrecord() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT  * FROM record";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from record", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("recID",cursor.getString(0));
                // map.put("userId", cursor.getString(1));
                map.put("user_name", cursor.getString(2));
                map.put("department_id",cursor.getString(10));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getAllLeave(){

        ArrayList<HashMap<String, String>> leaveList;
        leaveList = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from leav", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("leave_id",cursor.getString(0));
                map.put("leave_name", cursor.getString(1));
                leaveList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return leaveList;

    }

    public List<String> getLeave(){

        List<String> leaveList = new ArrayList<String>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from leav", null);

        if (cursor.moveToFirst()) {
            do {
                leaveList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        database.close();
        return leaveList;
    }

    public int getleaveId(String leaveName){

        String selectQuery = "SELECT  * FROM leav where leave_name ='"+leaveName+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        int leaveId = 0;

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                leaveId = cursor.getInt((cursor.getColumnIndex("leave_id")));

            } while (cursor.moveToNext());
        }
        database.close();
        return leaveId;

    }

    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM record";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("dat", cursor.getString(4));
                map.put("lat", cursor.getString(5));
                map.put("lon", cursor.getString(6));
                map.put("id", cursor.getString(7));
                map.put("status", cursor.getString(8));
                map.put("imei",cursor.getString(9));
                map.put("department_id",cursor.getString(10));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCoun() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed\n";
        }
        return msg;
    }

    public int dbSyncCoun(){
        int count = 0;
        String selectQuery = "SELECT  * FROM record";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }


    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update record set udpateStatus = '"+ status +"' where userId="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}
