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
    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";
    private static final String TABLE_LISTENING_HISTORY = "user_listening_history";


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
    public static final String COLUMN_USER_ID = "user_id";

    // Thêm constant cho bảng UserListeningHistory
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Thêm constant cho bảng FavoriteSongs
    public static final String COLUMN_FAVORITE_ID = "favorite_id";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        initializeDefaultAdmin();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //  Tạo bảng Users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB)";

        //  Tạo bảng Albums
        String createAlbumsTable = "CREATE TABLE " + TABLE_ALBUMS + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALBUM_TITLE + " TEXT NOT NULL, "
                + COLUMN_ALBUM_IMAGE + " BLOB, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "))";

        //  Tạo bảng Songs
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

        // Tạo bảng History
        String createHistoryTable = "CREATE TABLE " + TABLE_LISTENING_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";
        // Tạo bảng Favorite Songs

        String createFavoriteSongsTable = "CREATE TABLE " + TABLE_FAVORITE_SONGS + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "), "
                + "UNIQUE(" + COLUMN_USER_ID + ", " + COLUMN_SONG_ID + "))";

        // Thực thi theo đúng thứ tự
        db.execSQL(createUsersTable);
        db.execSQL(createAlbumsTable);
        db.execSQL(createSongsTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createFavoriteSongsTable);

        Log.d(TAG, "Database tables created");
        createDefaultAdmin(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // Xóa theo thứ tự ngược lại
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_FAVORITE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTENING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }
    // User methods
    public boolean addUser(String username, String password, String phone, String role, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_ROLE, role);
        if (image != null) {
            contentValues.put(COLUMN_IMAGE, image);
        }

        long result = db.insert(TABLE_USERS, null, contentValues);
        Log.d(TAG, "Add user result: " + result);
        return result != -1;
    }
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_PHONE + "=?", new String[]{phoneNumber},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    public User getUserByPhoneAndPassword(String phoneNumber, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ROLE, COLUMN_IMAGE};
        String selection = COLUMN_PHONE + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {phoneNumber, password};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                byte[] imageBytes = cursor.getBlob(5);
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        imageBytes
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by phone and password: " + e.getMessage());
        }

        return user;
    }
    public Cursor getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_USERNAME,
                COLUMN_PASSWORD,
                COLUMN_PHONE,
                COLUMN_ROLE,
                COLUMN_IMAGE
        };

        try {
            // Truy vấn tất cả user trừ admin
            return db.query(
                    TABLE_USERS,
                    columns,
                    COLUMN_ROLE + " != ?", // điều kiện loại trừ admin
                    new String[]{"admin"}, // tham số cho điều kiện
                    null,
                    null,
                    COLUMN_ID + " ASC" // sắp xếp theo ID tăng dần
            );
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting user details: " + e.getMessage());
            return null;
        }
    }

    public void initializeDefaultAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Checking for admin account...");

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_ROLE + "=?", new String[]{"admin"},
                null, null, null);

        Log.d(TAG, "Number of admin accounts found: " + cursor.getCount());

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "Admin");
            values.put(COLUMN_PASSWORD, "admin123");
            values.put(COLUMN_PHONE, "0123456789");
            values.put(COLUMN_ROLE, "admin");

            long result = db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "Admin account creation result: " + result);
        }
        cursor.close();
    }
    private void createDefaultAdmin(SQLiteDatabase db) {
        // Kiểm tra xem admin đã tồn tại chưa
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_PHONE + "=?", new String[]{"0123456789"},
                null, null, null);

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "Admin");
            values.put(COLUMN_PASSWORD, "admin123");
            values.put(COLUMN_PHONE, "0123456789");
            values.put(COLUMN_ROLE, "admin");

            db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "Default admin account created");
        }
        cursor.close();
    }

}