package com.bleyl.recurrence.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "RECURRENCE_DB";
    private static final String NOTIFICATION_TABLE = "NOTIFICATIONS";
    private static final String COL_ID = "ID";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_CONTENT = "CONTENT";
    private static final String COL_DATE_AND_TIME = "DATE_AND_TIME";
    private static final String COL_REPEAT_TYPE = "REPEAT_TYPE";
    private static final String COL_FOREVER = "FOREVER";
    private static final String COL_NUMBER_TO_SHOW = "NUMBER_TO_SHOW";
    private static final String COL_NUMBER_SHOWN = "NUMBER_SHOWN";

    public Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + NOTIFICATION_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_DATE_AND_TIME + " INTEGER, "
                + COL_REPEAT_TYPE + " INTEGER, "
                + COL_FOREVER + " BOOLEAN, "
                + COL_NUMBER_TO_SHOW + " INTEGER, "
                + COL_NUMBER_SHOWN + " INTEGER) ");
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
        onCreate(database);
    }

    public void add(Notification notification) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ID, notification.getId());
        values.put(COL_TITLE, notification.getTitle());
        values.put(COL_CONTENT, notification.getContent());
        values.put(COL_DATE_AND_TIME, notification.getDateAndTime());
        values.put(COL_REPEAT_TYPE, notification.getRepeatType());
        values.put(COL_FOREVER, notification.getForeverState());
        values.put(COL_NUMBER_TO_SHOW, notification.getNumberToShow());
        values.put(COL_NUMBER_SHOWN, notification.getNumberShown());

        database.insert(NOTIFICATION_TABLE, null, values);
    }

    public int getLastId() {
        int data = 0;
        String query = "SELECT " + COL_ID + " FROM " + NOTIFICATION_TABLE + " ORDER BY " + COL_ID + " DESC LIMIT 1";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            data = cursor.getInt(0);
            cursor.close();
        }
        return data;
    }

    public List<Notification> getActiveNotifications() {
        List<Notification> notificationList = new ArrayList<>();

        String query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_DATE_AND_TIME + " > "
                + DateAndTimeUtil.toLongDateAndTime(Calendar.getInstance()) + " ORDER BY " + COL_DATE_AND_TIME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(0));
                notification.setTitle(cursor.getString(1));
                notification.setContent(cursor.getString(2));
                notification.setDateAndTime(cursor.getString(3));
                notification.setRepeatType(cursor.getInt(4));
                notification.setForeverState(cursor.getString(5));
                notification.setNumberToShow(cursor.getInt(6));
                notification.setNumberShown(cursor.getInt(7));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notificationList;
    }

    public List<Notification> getInactiveNotifications() {
        List<Notification> notificationList = new ArrayList<>();

        String query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_DATE_AND_TIME + " <= "
                + DateAndTimeUtil.toLongDateAndTime(Calendar.getInstance()) + " ORDER BY " + COL_DATE_AND_TIME + " DESC";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(0));
                notification.setTitle(cursor.getString(1));
                notification.setContent(cursor.getString(2));
                notification.setDateAndTime(cursor.getString(3));
                notification.setRepeatType(cursor.getInt(4));
                notification.setForeverState(cursor.getString(5));
                notification.setNumberToShow(cursor.getInt(6));
                notification.setNumberShown(cursor.getInt(7));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notificationList;
    }

    public Notification getNotification(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setTitle(cursor.getString(1));
        notification.setContent(cursor.getString(2));
        notification.setDateAndTime(cursor.getString(3));
        notification.setRepeatType(cursor.getInt(4));
        notification.setForeverState(cursor.getString(5));
        notification.setNumberToShow(cursor.getInt(6));
        notification.setNumberShown(cursor.getInt(7));
        cursor.close();
        return notification;
    }

    public void update(Notification notification) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, notification.getTitle());
        values.put(COL_CONTENT, notification.getContent());
        values.put(COL_DATE_AND_TIME, notification.getDateAndTime());
        values.put(COL_REPEAT_TYPE, notification.getRepeatType());
        values.put(COL_FOREVER, notification.getForeverState());
        values.put(COL_NUMBER_TO_SHOW, notification.getNumberToShow());
        values.put(COL_NUMBER_SHOWN, notification.getNumberShown());
        database.update(NOTIFICATION_TABLE, values, COL_ID + " = " + notification.getId(), null);
    }

    public void delete(Notification notification) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.delete(NOTIFICATION_TABLE, COL_ID + "=" + notification.getId(), null);
    }

    public boolean isPresent(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }
}