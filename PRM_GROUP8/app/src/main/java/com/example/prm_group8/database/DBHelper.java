package com.example.prm_group8.database;

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

import android.database.SQLException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "prm_group8.db";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_ALBUMS = "albums";
    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";
    private static final String TABLE_LISTENING_HISTORY = "user_listening_history";

    // Columns for Users
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
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
                COLUMN_EMAIL + "=?", new String[]{"han171023fpt@gmail.com"},
                null, null, null);

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "Admin");
            values.put(COLUMN_EMAIL, "han171023fpt@gmail.com");
            values.put(COLUMN_ROLE, "admin");
            values.put(COLUMN_IS_EMAIL_VERIFIED, 1);
            db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "Default admin account created in SQLite");
        }
        cursor.close();
    }

    // User methods
    public boolean addUser(String username, String email, String role, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_IS_EMAIL_VERIFIED, 0);
        if (image != null) {
            values.put(COLUMN_IMAGE, image);
        }

        try {
            long result = db.insertOrThrow(TABLE_USERS, null, values);
            Log.d(TAG, "Add user result: " + result + " for email: " + email);
            return result != -1;
        } catch (SQLException e) {
            Log.e(TAG, "SQLException adding user for email: " + email + ", Error: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void syncEmailVerificationStatus(String email, boolean isVerified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_EMAIL_VERIFIED, isVerified ? 1 : 0);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
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

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED};
        Cursor cursor = db.query(TABLE_USERS, columns, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    "", // Không có password
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4),
                    cursor.getInt(5) == 1
            );
        }
        cursor.close();
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED};
        Cursor cursor = db.query(TABLE_USERS, columns, COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    "", // Không có password
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4),
                    cursor.getInt(5) == 1
            );
        }
        cursor.close();
        return user;
    }

    public User getUserByEmailAndPassword(String email, String password) {

        return null;
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED};
            cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);

            Log.d(TAG, "getAllUsers: Total rows = " + (cursor != null ? cursor.getCount() : 0));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                            "", // Không có password
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                            cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_EMAIL_VERIFIED)) == 1
                    );
                    users.add(user);
                    Log.d(TAG, "getAllUsers: Added user - " + user.getUsername());
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "getAllUsers: No users found in database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        Log.d(TAG, "getAllUsers: Total users retrieved = " + users.size());
        return users;
    }

    public boolean updateUserPassword(String email, String newPassword) {
        return false;
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
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_IMAGE, COLUMN_IS_EMAIL_VERIFIED},
                COLUMN_ROLE + "!=?", new String[]{"admin"},
                null, null, COLUMN_ID + " ASC");
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_ROLE, user.getRole());
        if (user.getImage() != null) {
            values.put(COLUMN_IMAGE, user.getImage());
        }

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        return rowsAffected > 0;
    }

    public int getOrCreateDefaultAlbum() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ALBUMS, new String[]{COLUMN_ALBUM_ID},
                COLUMN_ALBUM_TITLE + "=?", new String[]{"Default Album"},
                null, null, null);

        if (cursor.moveToFirst()) {
            int albumId = cursor.getInt(0);
            cursor.close();
            return albumId;
        }

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

    public Song getSongById(int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Song song = null;

        Cursor cursor = db.query(TABLE_SONGS, null, COLUMN_SONG_ID + "=?",
                new String[]{String.valueOf(songId)}, null, null, null);
        if (cursor.moveToFirst()) {
            song = new Song(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getBlob(6)
            );
        }
        cursor.close();
        return song;
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

    public List<Song> getFavoriteSongs(int userId) {
        List<Song> favoriteSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.* FROM " + TABLE_SONGS + " s "
                + "INNER JOIN " + TABLE_FAVORITE_SONGS + " f ON s." + COLUMN_SONG_ID + " = f." + COLUMN_SONG_ID
                + " WHERE f." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

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
                favoriteSongs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteSongs;
    }

    public boolean addListeningHistory(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SONG_ID, songId);

        long result = db.insert(TABLE_LISTENING_HISTORY, null, values);
        return result != -1;
    }

    public static class ListeningHistoryItem {
        private int historyId;
        private int userId;
        private int songId;
        private String songTitle;
        private String artist;
        private String timestamp;

        public ListeningHistoryItem(int historyId, int userId, int songId,
                                    String songTitle, String artist, String timestamp) {
            this.historyId = historyId;
            this.userId = userId;
            this.songId = songId;
            this.songTitle = songTitle;
            this.artist = artist;
            this.timestamp = timestamp;
        }

        // Getters
        public int getHistoryId() { return historyId; }
        public int getUserId() { return userId; }
        public int getSongId() { return songId; }
        public String getSongTitle() { return songTitle; }
        public String getArtist() { return artist; }
        public String getTimestamp() { return timestamp; }
    }

    public List<ListeningHistoryItem> getUserListeningHistory(int userId) {
        List<ListeningHistoryItem> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT h." + COLUMN_HISTORY_ID + ", h." + COLUMN_USER_ID +
                ", h." + COLUMN_SONG_ID + ", s." + COLUMN_SONG_TITLE +
                ", s." + COLUMN_ARTIST + ", h." + COLUMN_TIMESTAMP +
                " FROM " + TABLE_LISTENING_HISTORY + " h" +
                " JOIN " + TABLE_SONGS + " s ON h." + COLUMN_SONG_ID + " = s." + COLUMN_SONG_ID +
                " WHERE h." + COLUMN_USER_ID + " = ?" +
                " ORDER BY h." + COLUMN_TIMESTAMP + " DESC";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            Log.d(TAG, "Executing query: " + query + " with userId: " + userId);
            if (cursor.moveToFirst()) {
                do {
                    ListeningHistoryItem item = new ListeningHistoryItem(
                            cursor.getInt(0),    // history_id
                            cursor.getInt(1),    // user_id
                            cursor.getInt(2),    // song_id
                            cursor.getString(3),  // song_title
                            cursor.getString(4),  // artist
                            cursor.getString(5)   // timestamp
                    );
                    history.add(item);
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No history found for userId: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user listening history: " + e.getMessage());
        }

        Log.d(TAG, "Total history items retrieved: " + history.size());
        return history;
    }
}