package com.songlyrics.finder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SongLyricsDB extends SQLiteOpenHelper {

    /**
     *name of the Table
     */
    String favouritesTable = "FavouriteSongs";
    /**
     * Primary key
     */
    String recordId = "RecordId";
    /**
     * Column for song name
     */
    String songName = "songName";
    /**
     * Column for artist
     */
    String artistName = "artistName";
    /**
     * Column for song lyrics
     */
    String lyrics = "SongLyrics";

    /**
     * @param context Context of activity or fragment
     * */
    public SongLyricsDB(Context context) {
        super(context, "SongLyricsDB", null, 1);
    }

    /**
     * Creating the database when user runs
     * app for the first time
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + favouritesTable + "(" + recordId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + songName + " text," +
                        artistName + " text, " + lyrics + " text)"
        );
    }

    /**
     * drops previous database and
     * creates new database on update
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropQuery = "DROP TABLE " + favouritesTable;
        db.execSQL(dropQuery);
        onCreate(db);
    }

    /**
     * @return List of saved records for songs with lyrics
     * */
    public ArrayList<DetailOfSong> getAllFavourites() {
        ArrayList<DetailOfSong> DetailOfSong = new ArrayList<>();
        //get database object
        SQLiteDatabase database = this.getReadableDatabase();
        //perform query
        Cursor cursor = database.query(favouritesTable,
                new String[]{recordId, songName, artistName, lyrics},
                null, null,
                null, null, null, null);
        //check if the data returned
        if (cursor.moveToFirst()) {
            do {
                //read the retrieved data and put it in the list
                DetailOfSong detailOfSong = new DetailOfSong();
                detailOfSong.setRecordId(cursor.getString(0));
                detailOfSong.setTitle(cursor.getString(1));
                detailOfSong.setArtist(cursor.getString(2));
                detailOfSong.setLyrics(cursor.getString(3));
                DetailOfSong.add(detailOfSong);
            } while (cursor.moveToNext());
        }
        //close cursor and database after performing an operation
        cursor.close();
        database.close();
        return DetailOfSong;
    }

    /**
     * Stores the new record of the song
     * @param DetailOfSong An object containing all the details containing lyrics
     * */
    public void addToFavourites(DetailOfSong DetailOfSong) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(songName, DetailOfSong.getTitle());
        values.put(artistName, DetailOfSong.getArtist());
        values.put(lyrics, DetailOfSong.getLyrics());
        //perform insert operation
        db.insert(favouritesTable, null, values);
        db.close();
    }

    /**
     * Checks if the data already exists or not
     * @param artist name of an artist
     * @param title title of the song
     * */
    public String isFavouriteExists(String artist, String title) {
        boolean isFavouriteExists;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(favouritesTable,
                new String[]{recordId},
                artistName + "=? AND " + songName + "=?", new String[]{artist, title},
                null, null, null, null);
        isFavouriteExists = cursor.moveToFirst();
        String songId = "0";
        if (isFavouriteExists) {
            songId = cursor.getString(0);
        }
        cursor.close();
        database.close();
        return songId;
    }

    /**
     * Removes the favourite from the database
     * @param index id of the record stored in table
     * */
    public void deleteFavourite(String index) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(favouritesTable, recordId + " = ?",
                new String[]{index});
        db.close();
    }
}
