package com.example.prm_group8;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.prm_group8.model.Album;
import com.example.prm_group8.model.Song;
import com.example.prm_group8.model.User;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "prm_group8.db";
    private static final int DATABASE_VERSION = 4;


    private static final String TABLE_USERS = "users";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_ALBUMS = "albums";


    // Columns for Users
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IMAGE = "image";

    // Columns for Songs
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SONG_URL = "song_url";
    public static final String COLUMN_SONG_IMAGE = "image";

    // Thêm các cột cho bảng Album
    public static final String COLUMN_ALBUM_ID = "album_id";
    public static final String COLUMN_ALBUM_TITLE = "title";
    public static final String COLUMN_ALBUM_IMAGE = "image";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_USER_ID;

    static {
        COLUMN_USER_ID = "user_id";
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Users trước
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB)";

        // 2. Tạo bảng Albums (phụ thuộc vào Users)
        String createAlbumsTable = "CREATE TABLE " + TABLE_ALBUMS + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALBUM_TITLE + " TEXT NOT NULL, "
                + COLUMN_ALBUM_IMAGE + " BLOB, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "))";

        // 3. Tạo bảng Songs (phụ thuộc vào Albums)
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_TITLE + " TEXT NOT NULL, "
                + COLUMN_ARTIST + " TEXT NOT NULL, "
                + COLUMN_ALBUM_ID + " INTEGER, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_SONG_URL + " TEXT, "
                + COLUMN_SONG_IMAGE + " BLOB, "
                + "FOREIGN KEY(" + COLUMN_ALBUM_ID + ") REFERENCES "
                + TABLE_ALBUMS + "(" + COLUMN_ALBUM_ID + "))";


        Log.d(TAG, "Database tables created");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}