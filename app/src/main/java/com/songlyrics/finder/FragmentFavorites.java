package com.songlyrics.finder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentFavorites extends Fragment {
    /**
     * ListView for showing database data
     */
    ListView listView;
    /**
     * Layout for showing current fragment view
     */
    View view;
    /**
     * Adapter for setting up the ListView
     */
    ListOfSongs listOfSongs;

    /**
     * Object reference for database
     */
    SongLyricsDB songLyricsDB;

    /**
     * public constructor
     */
    public FragmentFavorites() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         * setting up layout
         */
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        /**
         * initializing database
         */
        songLyricsDB = new SongLyricsDB(getActivity());


        listView = rootView.findViewById(R.id.listView);

        /**
         * setting up onItemClickListener to move user to SongLyrics activity
         * along with song data
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * passing data on the user click
                 */
                DetailOfSong detailOfSong = (DetailOfSong) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), SongLyrics.class);
                intent.putExtra(Constants.INTENT_ARTIST, detailOfSong.getArtist());
                intent.putExtra(Constants.INTENT_SONG, detailOfSong.getTitle());
                intent.putExtra(Constants.INTENT_LYRICS, detailOfSong.getLyrics());
                startActivity(intent);
            }
        });
        return rootView;
    }

    /**
     * reading data when application comes back from onPause state
     */
    @Override
    public void onResume() {
        super.onResume();
        readDatabaseData();
    }

    /**
     * getting list of favorite songs and setting it
     * to listview via adapter
     */
    private void readDatabaseData() {
        ArrayList<DetailOfSong> detailOfSongs = songLyricsDB.getAllFavourites();
        listOfSongs = new ListOfSongs(getActivity(), detailOfSongs);
        listView.setAdapter(listOfSongs);
    }
}
