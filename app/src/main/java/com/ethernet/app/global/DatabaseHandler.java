package com.ethernet.app.global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ethernet.app.mainscreen.model.ContentDataModel;
import com.ethernet.app.mainscreen.model.DateTimeModel;
import com.ethernet.app.mainscreen.model.DevicePingModel;
import com.ethernet.app.utility.Constant;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "ContentDatabase";
    private static final String TABLE_CONTENT = "contentTable";
    private static final String TABLE_OFFLINE_LOOP_CONTENT = "offLinLoopContent";
    private static final String TABLE_SAVE_OFFLINE_DATE_TIME = "offlineDateTime";

    // field for content
    private static final String KEY_ID = "id";
    private static final String KEY_CLICK_URL = "clickUrl";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_IS_DAILY = "isDaily";
    private static final String KEY_IS_SCHEDULE = "isSchedule";
    private static final String KEY_LOOP_CONTENT = "loopContent";
    private static final String KEY_LOOP_START_DATE = "loopStartDate";
    private static final String KEY_LOOP_START_TIME = "loopStartTime";
    private static final String KEY_LOOP_END_DATE = "loopEndDate";
    private static final String KEY_LOOP_END_TIME = "loopEndTime";
    private static final String KEY_TXT_LOOP_NAME = "txtLoopName";
    private static final String KEY_TYPE = "type";
    private static final String KEY_URL = "url";
    private static final String KEY_DELETE_FLAG = "deleteFlag";
    private static final String KEY_SCREEN_TYPE = "screenType";

    // filed for saving offline ping
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_IP = "device_ip";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_DATE = "date";
    private static final String KEY_NOW_PLAYING = "now_playing";
    private static final String KEY_VIEW_CONTENT_COUNT = "viewcontentcount";
    private static final String KEY_VIEW_LOOP_COUNT = "viewloopcount";
    private static final String KEY_IS_OFFLINE = "is_offline";
    // save offline time for every day
    private static final String KEY_OFFLINE_DATE = "offline_date";
    private static final String KEY_OFFLINE_TIME = "offline_time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tab for vertical content
        String CREATE_CONTENT_TABLE = "CREATE TABLE " + TABLE_CONTENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CLICK_URL + " TEXT,"
                + KEY_DURATION + " TEXT," + KEY_IS_DAILY + " TEXT,"
                + KEY_IS_SCHEDULE + " TEXT," + KEY_LOOP_CONTENT + " TEXT,"
                + KEY_LOOP_END_DATE + " TEXT," + KEY_LOOP_END_TIME + " TEXT,"
                + KEY_LOOP_START_DATE + " TEXT," + KEY_LOOP_START_TIME + " TEXT,"
                + KEY_TXT_LOOP_NAME + " TEXT," + KEY_TYPE + " TEXT,"
                + KEY_URL + " TEXT," + KEY_DELETE_FLAG + " TEXT,"
                + KEY_SCREEN_TYPE + " TEXT" + ")";

        //create tab for vertical offline loop
        String CREATE_OFFLINE_LOOP_TABLE = "CREATE TABLE " + TABLE_OFFLINE_LOOP_CONTENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICE_ID + " TEXT,"
                + KEY_DEVICE_IP + " TEXT," + KEY_UUID + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_NOW_PLAYING + " TEXT,"
                + KEY_VIEW_CONTENT_COUNT + " TEXT," + KEY_VIEW_LOOP_COUNT + " TEXT,"
                + KEY_IS_OFFLINE + " TEXT" + ")";

        String CREATE_OFFLINE_DATE_AND_TIME_TABLE = "CREATE TABLE " + TABLE_SAVE_OFFLINE_DATE_TIME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_OFFLINE_DATE + " TEXT,"
                + KEY_OFFLINE_TIME + " TEXT" + ")";


        db.execSQL(CREATE_CONTENT_TABLE);
        db.execSQL(CREATE_OFFLINE_LOOP_TABLE);
        db.execSQL(CREATE_OFFLINE_DATE_AND_TIME_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_LOOP_CONTENT);

        // Create tables again
        onCreate(db);
    }

    // TODO insert content
    public void insertContent(ContentDataModel contentModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLICK_URL, contentModel.clickUrl);
        values.put(KEY_DURATION, contentModel.duration);
        values.put(KEY_IS_DAILY, contentModel.isDaily);
        values.put(KEY_IS_SCHEDULE, contentModel.isSchedule);
        values.put(KEY_LOOP_CONTENT, contentModel.loopContent);
        values.put(KEY_LOOP_END_DATE, contentModel.loopEndDate);
        values.put(KEY_LOOP_END_TIME, contentModel.loopEndTime);
        values.put(KEY_LOOP_START_DATE, contentModel.loopStartDate);
        values.put(KEY_LOOP_START_TIME, contentModel.loopStartTime);
        values.put(KEY_TXT_LOOP_NAME, contentModel.txtLoopName);
        values.put(KEY_TYPE, contentModel.type);
        values.put(KEY_URL, contentModel.url);
        values.put(KEY_DELETE_FLAG, contentModel.deleteFlag);
        values.put(KEY_SCREEN_TYPE, contentModel.screenType);

        // Inserting Row
        db.insert(TABLE_CONTENT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // TODO insert all offline loop content

    public void insertAllOffLineLoopContent(DevicePingModel model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_ID, model.deviceId);
        values.put(KEY_DEVICE_IP, model.deviceIp);
        values.put(KEY_UUID, model.uuId);
        values.put(KEY_DATE, model.date);
        values.put(KEY_NOW_PLAYING, model.nowPlaying);
        values.put(KEY_VIEW_CONTENT_COUNT, model.viewContentCount);
        values.put(KEY_VIEW_LOOP_COUNT, model.viewLoopCount);
        values.put(KEY_IS_OFFLINE, model.isOffline);
        // Inserting Row
        db.insert(TABLE_OFFLINE_LOOP_CONTENT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    /**********************************************************************************************
     ****************************** GET VERTICAL CONTENT************************************************
     * *******************************************************************************************/

    // TODO code to get all vertical content
    public ArrayList<ContentDataModel> getAllContent() {

        ArrayList<ContentDataModel> contentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContentDataModel model = new ContentDataModel();
                model.clickUrl = cursor.getString(1);
                model.duration = cursor.getString(2);
                model.isDaily = cursor.getString(3);
                model.isSchedule = cursor.getString(4);
                model.loopContent = cursor.getString(5);
                model.loopEndDate = cursor.getString(6);
                model.loopEndTime = cursor.getString(7);
                model.loopStartDate = cursor.getString(8);
                model.loopStartTime = cursor.getString(9);
                model.txtLoopName = cursor.getString(10);
                model.type = cursor.getString(11);
                model.url = cursor.getString(12);
                model.deleteFlag = cursor.getString(13);
                model.screenType = cursor.getString(14);
                // Adding contact to list
                contentList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contentList;
    }


    // TODO code to get all vertical content
    public ArrayList<ContentDataModel> getAllVerticalContent() {

        ArrayList<ContentDataModel> contentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT+" where "+KEY_SCREEN_TYPE+" ='" + Constant.ScreenType.VERTICAL_ADD+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContentDataModel model = new ContentDataModel();
                model.clickUrl = cursor.getString(1);
                model.duration = cursor.getString(2);
                model.isDaily = cursor.getString(3);
                model.isSchedule = cursor.getString(4);
                model.loopContent = cursor.getString(5);
                model.loopEndDate = cursor.getString(6);
                model.loopEndTime = cursor.getString(7);
                model.loopStartDate = cursor.getString(8);
                model.loopStartTime = cursor.getString(9);
                model.txtLoopName = cursor.getString(10);
                model.type = cursor.getString(11);
                model.url = cursor.getString(12);
                // Adding contact to list
                contentList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contentList;
    }

    // TODO code to get all All Vertical OffLineLoopContent
    public ArrayList<DevicePingModel> getAllOffLineLoopContent() {

        ArrayList<DevicePingModel> pingList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OFFLINE_LOOP_CONTENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DevicePingModel model = new DevicePingModel();
                model.id = cursor.getString(0);
                model.deviceId = cursor.getString(1);
                model.deviceIp = cursor.getString(2);
                model.uuId = cursor.getString(3);
                model.date = cursor.getString(4);
                model.nowPlaying = cursor.getString(5);
                model.viewContentCount = cursor.getInt(6);
                model.viewLoopCount = cursor.getInt(7);
                model.isOffline = cursor.getInt(8);
                // Adding contact to list
                pingList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return pingList;
    }
    /**********************************************************************************************
     ******************************HORIZONTAL CONTENT************************************************
     * *******************************************************************************************/
    // TODO code to get all horizontal content
    public ArrayList<ContentDataModel> getAllHorizontalContent() {

        ArrayList<ContentDataModel> contentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT+" where "+KEY_SCREEN_TYPE+" ='" + Constant.ScreenType.HORIZONTAL_ADD+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContentDataModel model = new ContentDataModel();
                model.clickUrl = cursor.getString(1);
                model.duration = cursor.getString(2);
                model.isDaily = cursor.getString(3);
                model.isSchedule = cursor.getString(4);
                model.loopContent = cursor.getString(5);
                model.loopEndDate = cursor.getString(6);
                model.loopEndTime = cursor.getString(7);
                model.loopStartDate = cursor.getString(8);
                model.loopStartTime = cursor.getString(9);
                model.txtLoopName = cursor.getString(10);
                model.type = cursor.getString(11);
                model.url = cursor.getString(12);
                // Adding contact to list
                contentList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contentList;
    }

    /**********************************************************************************************
     ******************************Payment CONTENT************************************************
     * *******************************************************************************************/
    // TODO code to get all payment content
    public ArrayList<ContentDataModel> getAllPaymentContent() {

        ArrayList<ContentDataModel> contentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENT+" where "+KEY_SCREEN_TYPE+" ='" + Constant.ScreenType.PAYMENT_ADD+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContentDataModel model = new ContentDataModel();
                model.clickUrl = cursor.getString(1);
                model.duration = cursor.getString(2);
                model.isDaily = cursor.getString(3);
                model.isSchedule = cursor.getString(4);
                model.loopContent = cursor.getString(5);
                model.loopEndDate = cursor.getString(6);
                model.loopEndTime = cursor.getString(7);
                model.loopStartDate = cursor.getString(8);
                model.loopStartTime = cursor.getString(9);
                model.txtLoopName = cursor.getString(10);
                model.type = cursor.getString(11);
                model.url = cursor.getString(12);
                // Adding contact to list
                contentList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contentList;
    }

    // TODO check table isEmpty
    public boolean isTableEmpty(){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TABLE_CONTENT);
        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }
    //TODO update table  flag with 1
    public boolean updateDeleteFlagContent(String flag) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query = "UPDATE  "+TABLE_CONTENT +" SET "+KEY_DELETE_FLAG+" = " +flag;
            db.execSQL(query);

        }catch (SQLException e){
            e.printStackTrace();
            Log.e("DB",e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return true;
    }
    /** Delete content which has value 1 */
    public boolean deleteContent(String flag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTENT, KEY_DELETE_FLAG + "=?", new String[]{flag}) > 0;
    }

    // TODO delete all vertical loop content
    public void deleteLoopContentById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_LOOP_CONTENT, KEY_ID + "="+id,null);
        db.close();
    }
    // TODO insert offline date and time
    public void insertOfflineDateAndTime(DateTimeModel model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OFFLINE_DATE, model.dateValue);
        values.put(KEY_OFFLINE_TIME, model.timeValue);
        // Inserting Row
        db.insert(TABLE_SAVE_OFFLINE_DATE_TIME, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    public boolean isDateAvailable(String currentDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_SAVE_OFFLINE_DATE_TIME + " where " + KEY_OFFLINE_DATE + " = '" + currentDate+"'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public ArrayList<DateTimeModel> getAllOfflineDateAndTime() {

        ArrayList<DateTimeModel> dateTimeList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SAVE_OFFLINE_DATE_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DateTimeModel model = new DateTimeModel();
                model.id = cursor.getString(0);
                model.dateValue = cursor.getString(1);
                model.timeValue = cursor.getString(2);
                // Adding contact to list
                dateTimeList.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return dateTimeList;
    }
    public void deleteDateTimeById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SAVE_OFFLINE_DATE_TIME, KEY_ID + "="+id,null);
        db.close();
    }


}