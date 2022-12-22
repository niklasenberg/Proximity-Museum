package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import se.umu.nien1121.museumapplication.databinding.ActivityArtistBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.JsonReader;

public class ArtistActivity extends AppCompatActivity {
    public static final String ARTIST_NAME_EXTRA = "ARTIST_NAME_EXTRA";
    public static final String ARTIST_ID_EXTRA = "ARTIST_ID_EXTRA";
    private static final String TAG = "ArtistActivity";
    private ActivityArtistBinding binding;
    private String artistName;
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.artistName = getIntent().getStringExtra(ARTIST_NAME_EXTRA);
        this.artistId = getIntent().getStringExtra(ARTIST_ID_EXTRA);

        ActionBarHelper.setActionBar(this, "Paintings by " + artistName);
        fetchArtworks();
    }

    private void updateList(ArrayList<Artwork> artworks) {
        ArtworkAdapter adapter = new ArtworkAdapter(this, artworks);
        binding.artworkRecyclerView.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchArtworks() {
        new AsyncTask<Integer, JSONObject, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Integer... params) {
                try {
                    String url = "http://85.230.192.244/artist?id=" + artistId;
                    JSONObject jsonObject = JsonReader.readJsonFromUrl(url);
                    return jsonObject;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject artworksJSON) {
                ArrayList<Artwork> artworks = new ArrayList<>();
                if(artworksJSON != null){
                    try {
                        JSONArray data = (JSONArray) artworksJSON.get("data");
                        Gson gson = new Gson();
                        for (int i = 0; i < data.length(); i++) {
                            Log.d(TAG, data.get(i).toString());
                            artworks.add(gson.fromJson(data.get(i).toString(), Artwork.class));
                        }
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }

                updateList(artworks);
            }
        }.execute(1);
    }
}