package com.songlyrics.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class SongLyrics extends AppCompatActivity {
    /**
     * TextViews for showing required data
     */
    TextView artistName, songName, songLyrics;
    /**
     * Buttons for calling specific operations
     */
    Button Searchgoogle, addToFav, removeFav;
    /**
     * String Objects for holding the intent data
     */
    String song, artist, lyrics;
    /**
     * Reference to the SQLite database object
     */
    SongLyricsDB songLyricsDB;
    /**
     * To hold the recordId if the data if loaded from the database
     */
    String recordId;
    /**
     * Linear Layout for snack Bar
     */
    LinearLayout sl1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics);
        artistName = findViewById(R.id.artistNameTxt);
        songName = findViewById(R.id.songNameTxt);
        songLyrics = findViewById(R.id.songLyricsTxt);
        Searchgoogle = findViewById(R.id.searchGoogleBtn);
        addToFav = findViewById(R.id.addFavBtn);
        removeFav = findViewById(R.id.remFavBtn);
        sl1 =findViewById(R.id.sl1);

        /**
         * initializing database
         */
        songLyricsDB = new SongLyricsDB(this);

        /**
         * getting values from intent object
         */
        Intent intent = getIntent();
        artist = intent.getStringExtra(Constants.INTENT_ARTIST);
        song = intent.getStringExtra(Constants.INTENT_SONG);
        lyrics = intent.getStringExtra(Constants.INTENT_LYRICS);

        /**
         * setting values to TextViews
         */
        artistName.setText(artist);
        songName.setText(song);
        songLyrics.setText(lyrics);

        /**
         * @param artist
         * @param song
         * checking if song exist in the database as favorite
         */
        recordId = songLyricsDB.isFavouriteExists(artist, song);

        /**
         * changing visibility of buttons based on boolean values
         */
        boolean isFavouriteAdded = !recordId.equals("0");
        if (isFavouriteAdded) {
            addToFav.setVisibility(View.GONE);
            removeFav.setVisibility(View.VISIBLE);
        } else {
            removeFav.setVisibility(View.GONE);
            addToFav.setVisibility(View.VISIBLE);
        }
        /**
         * logic for searching on google
         */
        Searchgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constants.GOOGLE_SEARCH_URL + artist + "+" + song;
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(url));
                startActivity(intent1);
            }
        });

        /**
         * adding song data into database
         */
        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //performing insert operation
                DetailOfSong detailOfSong = new DetailOfSong();
                detailOfSong.setArtist(artist);
                detailOfSong.setTitle(song);
                detailOfSong.setLyrics(lyrics);
                songLyricsDB.addToFavourites(detailOfSong);
                addToFav.setVisibility(View.GONE);
                removeFav.setVisibility(View.VISIBLE);
                //show the snackbar
                Snackbar.make(sl1,getResources().getString(R.string.fav_added), Snackbar.LENGTH_SHORT).show();
            }
        });

        /**
         * removing song data from database
         */
        removeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SongLyrics.this)
                .setTitle(getResources().getString(R.string.alert_title))
                .setMessage(getResources().getString(R.string.alert_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //deleting record from the db by using recordId as primary key
                        songLyricsDB.deleteFavourite(recordId);
                        removeFav.setVisibility(View.GONE);
                        addToFav.setVisibility(View.VISIBLE);
                        //shows the snackbar
                        Snackbar.make(sl1, getResources().getString(R.string.fav_removed), Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}