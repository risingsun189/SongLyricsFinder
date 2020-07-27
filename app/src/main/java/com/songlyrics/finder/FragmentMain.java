package com.songlyrics.finder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentMain extends Fragment {

    /**
     * EditText for artist name
     */
    EditText artistName;
    /**
     * EditText for song name
     */
    EditText songName;
    /**
     * Button to call api
     */
    Button findLyricsBtn;
    /**
     * ProgressDialog for showing progress
     */
    ProgressDialog progressDialog;

    /**
     * For storing searched data
     */
    SharedPreferences sharedPreferences;

    public FragmentMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        findLyricsBtn = rootView.findViewById(R.id.findLyricsBtn);
        sharedPreferences = getActivity().getSharedPreferences(Constants.SEARCH_DATA, Context.MODE_PRIVATE);
        artistName = rootView.findViewById(R.id.artistNameEdt);
        songName = rootView.findViewById(R.id.songNameEdt);

        //fetch last searched data if any
        getLastSearch();

        findLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artist = artistName.getText().toString();
                String song = songName.getText().toString();

                if (artist.trim().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_artist_name), Toast.LENGTH_SHORT).show();
                } else if (song.trim().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_song_name), Toast.LENGTH_SHORT).show();
                } else {
                    new LyricSearch(artist, song).execute();
                }
            }
        });

        return rootView;
    }
    /**
     * Reading shared preference for getting last successful searched data
     */
    private void getLastSearch() {
        String lastArtistName = sharedPreferences.getString(Constants.NAME_Of_ARTIST, "");
        String lastSongName = sharedPreferences.getString(Constants.NAME_OF_SONG, "");
        songName.setText(lastSongName);
        artistName.setText(lastArtistName);
    }

    /**
     * Saving last successful searched data
     */
    private void saveSearchedData(String artistName, String songName) {
        sharedPreferences.edit()
                .putString(Constants.NAME_Of_ARTIST, artistName)
                .putString(Constants.NAME_OF_SONG, songName)
                .apply();
    }
    /**
     * AsyncTask for performing API call
     */
    class LyricSearch extends AsyncTask<Void, Void, String> {

        String artist;
        String song;

        /**
         * @param artist Name of the artist
         * @param song   name of the song
         */
        LyricSearch(String artist, String song) {
            this.artist = artist;
            this.song = song;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.cancel();
            //parse the json data
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.has("lyrics")) {
                    String lyrics = jsonObject.getString("lyrics");
                    saveSearchedData(artist, song);
                    Intent intent = new Intent(getActivity(), SongLyrics.class);
                    intent.putExtra(Constants.INTENT_ARTIST, artist);
                    intent.putExtra(Constants.INTENT_SONG, song);
                    intent.putExtra(Constants.INTENT_LYRICS, lyrics);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.lyric_not_found), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.lyric_not_found_Json), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progressDialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(R.string.searching_lyrics);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String httpResponse = "";
            try {
                //replace spaces with %20 for URL safe arguments
                String encodedArtistName = artist.replaceAll(" ", "%20");
                String encodedSongName = song.replaceAll(" ", "%20");
                String apiUrl = Constants.API_URL + encodedArtistName + "/" + encodedSongName;
                //using HttpUrlConnection to call API
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                String responseLine;
                //reading the response
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                while ((responseLine = br.readLine()) != null) {
                    stringBuilder.append(responseLine);
                }
                httpResponse = stringBuilder.toString();
            } catch (IOException e) {
            }
            return httpResponse;
        }
    }
}
