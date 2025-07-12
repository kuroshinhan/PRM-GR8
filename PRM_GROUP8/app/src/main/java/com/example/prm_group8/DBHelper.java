package com.example.prm_group8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.prm_group8.model.Song;
import com.example.prm_group8.model.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "prm_group8.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_ALBUMS = "albums";
    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";
    private static final String TABLE_LISTENING_HISTORY = "user_listening_history";

    // Columns for Users
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_IS_EMAIL_VERIFIED = "is_email_verified";

    // Columns for Songs
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SONG_URL = "song_url";
    public static final String COLUMN_SONG_IMAGE = "image";

    // Columns for Albums
    public static final String COLUMN_ALBUM_ID = "album_id";
    public static final String COLUMN_ALBUM_TITLE = "title";
    public static final String COLUMN_ALBUM_IMAGE = "image";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_USER_ID = "user_id";

    // Columns for UserListeningHistory
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Columns for FavoriteSongs
    public static final String COLUMN_FAVORITE_ID = "favorite_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        createDefaultAdmin(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " BLOB, "
                + COLUMN_IS_EMAIL_VERIFIED + " INTEGER DEFAULT 0)";

        // Create Albums table
        String createAlbumsTable = "CREATE TABLE " + TABLE_ALBUMS + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALBUM_TITLE + " TEXT NOT NULL, "
                + COLUMN_ALBUM_IMAGE + " BLOB, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "))";

        // Create Songs table
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

        // Create History table
        String createHistoryTable = "CREATE TABLE " + TABLE_LISTENING_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";

        // Create Favorite Songs table
        String createFavoriteSongsTable = "CREATE TABLE " + TABLE_FAVORITE_SONGS + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_SONG_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "), "
                + "UNIQUE(" + COLUMN_USER_ID + ", " + COLUMN_SONG_ID + "))";

        // Execute table creation
        db.execSQL(createUsersTable);
        db.execSQL(createAlbumsTable);
        db.execSQL(createSongsTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createFavoriteSongsTable);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse order
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTENING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    private void createDefaultAdmin(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{"admin@prm_group8"},
                null, null, null);

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "Admin");
            values.put(COLUMN_PASSWORD, "admin123");
            values.put(COLUMN_EMAIL, "admin@prm_group8");
            values.put(COLUMN_ROLE, "admin");
            values.put(COLUMN_IS_EMAIL_VERIFIED, 1); // Admin is verified by default
            db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "Default admin account created");
        }
        cursor.close();
    }

    // User methods
    public boolean addUser(String username, String password, String email, String role, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_IS_EMAIL_VERIFIED, 0);
        if (image != null) {
            values.put(COLUMN_IMAGE, image);
        }

        long result = db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "Add user result: " + result);
        return result != -1;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED};
        Cursor cursor = db.query(TABLE_USERS, columns, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5),
                    cursor.getInt(6) == 1
            );
        }
        cursor.close();
        return user;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED};
        Cursor cursor = db.query(TABLE_USERS, columns, COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5),
                    cursor.getInt(6) == 1
            );
        }
        cursor.close();
        return user;
    }

    public boolean updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return rowsAffected > 0;
    }

    public boolean setEmailVerified(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_EMAIL_VERIFIED, 1);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return rowsAffected > 0;
    }

    public Cursor getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED},
                COLUMN_ROLE + "!=?", new String[]{"admin"},
                null, null, COLUMN_ID + " ASC");
    }
    public int getOrCreateDefaultAlbum() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem album mặc định đã tồn tại chưa
        Cursor cursor = db.query(TABLE_ALBUMS, new String[]{COLUMN_ALBUM_ID},
                COLUMN_ALBUM_TITLE + "=?", new String[]{"Default Album"},
                null, null, null);

        if (cursor.moveToFirst()) {
            int albumId = cursor.getInt(0);
            cursor.close();
            return albumId;
        }

        // Nếu chưa có, tạo album mặc định
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALBUM_TITLE, "Default Album");
        values.put(COLUMN_RELEASE_DATE, "2024-01-01");
        values.put(COLUMN_USER_ID, 1); // Giả sử user_id = 1 là admin

        long albumId = db.insert(TABLE_ALBUMS, null, values);
        cursor.close();
        return (int) albumId;
    }
    public boolean addSong(String title, String artist, int albumId, int duration, String songUrl, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Nếu không có albumId được chỉ định, sử dụng album mặc định
        if (albumId <= 0) {
            albumId = getOrCreateDefaultAlbum();
        }

        contentValues.put(COLUMN_SONG_TITLE, title);
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_ALBUM_ID, albumId);
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_SONG_URL, songUrl);
        if (image != null) {
            contentValues.put(COLUMN_SONG_IMAGE, image);
        }

        long result = db.insert(TABLE_SONGS, null, contentValues);
        Log.d(TAG, "Add song result: " + result + " for song: " + title);
        return result != -1;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null, null, null, null, null, null);

        Log.d(TAG, "getAllSongs: Total rows = " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getBlob(6)
                );
                songs.add(song);
                Log.d(TAG, "getAllSongs: Added song - " + song.getTitle());
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "getAllSongs: No songs found in database");
        }
        cursor.close();
        Log.d(TAG, "getAllSongs: Total songs retrieved = " + songs.size());
        return songs;
    }

    public boolean deleteSong(int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SONGS, COLUMN_SONG_ID + " = ?", new String[]{String.valueOf(songId)});
        return rowsAffected > 0;
    }

    public boolean addFavoriteSong(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SONG_ID, songId);

        long result = db.insert(TABLE_FAVORITE_SONGS, null, values);
        return result != -1;
    }

    public boolean removeFavoriteSong(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_FAVORITE_SONGS,
                COLUMN_USER_ID + " = ? AND " + COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(songId)});
        return rowsDeleted > 0;
    }

    public boolean isSongFavorite(int userId, int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITE_SONGS, null,
                COLUMN_USER_ID + " = ? AND " + COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(songId)},
                null, null, null);
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }
}