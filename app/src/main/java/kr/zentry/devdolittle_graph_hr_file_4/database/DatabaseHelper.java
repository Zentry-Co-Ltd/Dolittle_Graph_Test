package kr.zentry.devdolittle_graph_hr_file_4.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kr.zentry.devdolittle_graph_hr_file_4.thingy.Thingy;

public class DatabaseHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_THINGY_DB_COLUMN_ENTRIES = "CREATE TABLE " + DatabaseContract.ThingyDbColumns.TABLE_NAME + " (" + DatabaseContract.ThingyDbColumns._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_LAST_SELECTED + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_LONGITUDE + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_MARKER_TITLE + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_MARKER_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_TEMPERATURE + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PRESSURE + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_HUMIDITY + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_AIR_QUALITY + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_COLOR + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_BUTTON + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_EULER + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_GRAVITY_VECTOR + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_HEADING + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_ORIENTATION + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PEDOMETER + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_QUATERNION + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_RAW_DATA + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_TAP + BOOLEAN_TYPE + ")";

    private static final String SQL_CREATE_CLOUD_DB_COLUMN_ENTRIES = "CREATE TABLE " + DatabaseContract.CloudDbColumns.TABLE_NAME + " (" + DatabaseContract.CloudDbColumns._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.CloudDbColumns.COLUMN_ADDRESS + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
            DatabaseContract.CloudDbColumns.COLUMN_TEMPERATURE_UPLOAD + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.CloudDbColumns.COLUMN_PRESSURE_UPLOAD + BOOLEAN_TYPE + COMMA_SEP +
            DatabaseContract.CloudDbColumns.COLUMN_BUTTON_STATE_UPLOAD + BOOLEAN_TYPE + ")";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ThingyDbColumns.db";

    private static final String[] THINGY_DEVICES = new String[]{DatabaseContract.ThingyDbColumns._ID, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS,
            DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME, DatabaseContract.ThingyDbColumns.COLUMN_LAST_SELECTED, DatabaseContract.ThingyDbColumns.COLUMN_LOCATION,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_TEMPERATURE, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PRESSURE,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_HUMIDITY, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_AIR_QUALITY,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_COLOR, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_BUTTON,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_EULER, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_GRAVITY_VECTOR,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_HEADING, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_ORIENTATION,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PEDOMETER, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_QUATERNION,
            DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_RAW_DATA, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_TAP};

    private static SqliteHelper mSqliteHelper;
    private static SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(final Context context) {
        if (mSqliteHelper == null) {
            mSqliteHelper = new SqliteHelper(context);
            sqLiteDatabase = mSqliteHelper.getWritableDatabase();
        }
    }

    public class SqliteHelper extends SQLiteOpenHelper {

        public SqliteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Creating the thingy entries table
            db.execSQL(SQL_CREATE_THINGY_DB_COLUMN_ENTRIES);
            //Creating the cloud entries table
            db.execSQL(SQL_CREATE_CLOUD_DB_COLUMN_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1:
                    //Updgrading data base version from 1 to 2
                    //Creating the cloud entries table
                    db.execSQL(SQL_CREATE_CLOUD_DB_COLUMN_ENTRIES);
                    break;
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public void insertDevice(final String macAddress, final String name) {
        final ContentValues content = new ContentValues();
        content.put(DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS, macAddress);
        content.put(DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME, name);
        sqLiteDatabase.insert(DatabaseContract.ThingyDbColumns.TABLE_NAME, null, content);
    }

    public boolean isExist(final String macAddress) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, new String[]{DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS}, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{macAddress}, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public Thingy getSavedDevice(final String address) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, DatabaseHelper.THINGY_DEVICES, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return new Thingy(cursor.getString(1), cursor.getString(2));
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public ArrayList<Thingy> getSavedDevices() {
        final ArrayList<Thingy> devices = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, DatabaseHelper.THINGY_DEVICES, null, null, null, null, null);
        while (cursor.moveToNext()) {
            devices.add(new Thingy(cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();

        return devices;
    }

    public void setLastSelected(final String address, final boolean state) {
        final ContentValues content = new ContentValues();
        content.put(DatabaseContract.ThingyDbColumns.COLUMN_LAST_SELECTED, state ? Integer.valueOf(1) : Integer.valueOf(0));
        int ret = sqLiteDatabase.update(DatabaseContract.ThingyDbColumns.TABLE_NAME, content, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + " = ? ", new String[]{address});
        Log.d("DATABASE HELPLER", String.valueOf(ret) + String.valueOf(state));

    }

    public boolean getLastSelected(final String address) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, new String[]{DatabaseContract.ThingyDbColumns.COLUMN_LAST_SELECTED}, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return true;
    }

    public Thingy getLastSelected() {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, DatabaseHelper.THINGY_DEVICES, null, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                //4th Column has boolean for the last selected device;
                if (cursor.getInt(3) > 0) {
                    return new Thingy(cursor.getString(1), cursor.getString(2));
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public boolean getNotificationsState(final String address, final String columnName) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, new String[]{columnName}, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return true;
    }

    public void updateNotificationsState(final String address, final boolean flag, final String columnName) {
        final ContentValues content = new ContentValues();
        content.put(columnName, flag ? 1 : 0);
        sqLiteDatabase.update(DatabaseContract.ThingyDbColumns.TABLE_NAME, content, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address});
    }

    public void updateDeviceName(final String address, final String deviceName) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, new String[]{DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME}, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        if (cursor.getCount() > 0) {
            final ContentValues content = new ContentValues();
            content.put(DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME, deviceName);
            sqLiteDatabase.update(DatabaseContract.ThingyDbColumns.TABLE_NAME, content, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{String.valueOf(address)});
        }
    }

    public String getDeviceName(final String address) {
        Cursor cursor = sqLiteDatabase.query(DatabaseContract.ThingyDbColumns.TABLE_NAME, new String[]{DatabaseContract.ThingyDbColumns.COLUMN_DEVICE_NAME}, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        return "";
    }

    public void removeDevice(final String address) {
        sqLiteDatabase.delete(DatabaseContract.ThingyDbColumns.TABLE_NAME, DatabaseContract.ThingyDbColumns.COLUMN_ADDRESS + "=?", new String[]{address});
    }

    public boolean getTemperatureUploadState(final String address) {
        final Cursor cursor = sqLiteDatabase.query(DatabaseContract.CloudDbColumns.TABLE_NAME, new String[]{DatabaseContract.CloudDbColumns.COLUMN_TEMPERATURE_UPLOAD}, DatabaseContract.CloudDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }

    public boolean getPressureUploadState(final String address) {
        final Cursor cursor = sqLiteDatabase.query(DatabaseContract.CloudDbColumns.TABLE_NAME, new String[]{DatabaseContract.CloudDbColumns.COLUMN_PRESSURE_UPLOAD}, DatabaseContract.CloudDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }

    public boolean getButtonUploadState(final String address) {
        final Cursor cursor = sqLiteDatabase.query(DatabaseContract.CloudDbColumns.TABLE_NAME, new String[]{DatabaseContract.CloudDbColumns.COLUMN_BUTTON_STATE_UPLOAD}, DatabaseContract.CloudDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }

    public void enableCloudNotifications(final String address, final boolean flag, final String columnName) {
        final ContentValues content = new ContentValues();
        content.put(columnName, flag ? 1 : 0);
        if (sqLiteDatabase.update(DatabaseContract.CloudDbColumns.TABLE_NAME, content, DatabaseContract.CloudDbColumns.COLUMN_ADDRESS + "=?", new String[]{address}) == 0) {
            insertDeviceRecordToCloudUploadTable(address, flag, columnName);
        }
    }

    public void insertDeviceRecordToCloudUploadTable(final String macAddress, final boolean flag, final String columnName) {
        final ContentValues content = new ContentValues();
        content.put(DatabaseContract.CloudDbColumns.COLUMN_ADDRESS, macAddress);
        content.put(columnName, flag);
        sqLiteDatabase.insert(DatabaseContract.CloudDbColumns.TABLE_NAME, null, content);
    }
}
