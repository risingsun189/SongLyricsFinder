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
    public FragmentFavorites() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        songLyricsDB = new SongLyricsDB(getActivity());
        listView = rootView.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /** show detail for the selected list item in another activity*/
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
    @Override
    public void onResume() {
        super.onResume();
        readDatabaseData();
    }

    /**
     * Gets all the favourite song details from the database
     * and sets the retrieved data to ListView using adapter.
     */
    private void readDatabaseData() {
        ArrayList<DetailOfSong> detailOfSongs = songLyricsDB.getAllFavourites();
        listOfSongs = new ListOfSongs(getActivity(), detailOfSongs);
        listView.setAdapter(listOfSongs);
    }
}
