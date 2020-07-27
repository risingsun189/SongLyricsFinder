package com.songlyrics.finder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListOfSongs extends BaseAdapter {

    /**
     * List of the data items to be shown in the lsit
     */
    ArrayList<DetailOfSong> detailOfSongs;
    /**
     * Helps the class to bind the View and access it.
     */
    static LayoutInflater inflater;

    public ListOfSongs(Context context, ArrayList<DetailOfSong> detailOfSongs) {
        this.detailOfSongs = detailOfSongs;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return detailOfSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return detailOfSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.fav_item, parent, false);
        }
        //bind the textViews and show the passed data into it.
        DetailOfSong detailsOfSong = detailOfSongs.get(position);
        TextView songName = view.findViewById(R.id.songNameItem);
        TextView songArtist = view.findViewById(R.id.artistNameItem);
        songName.setText(detailsOfSong.getTitle());
        songArtist.setText(detailsOfSong.getArtist());
        return view;
    }
}
