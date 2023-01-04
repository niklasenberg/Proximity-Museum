package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.databinding.ActivityArtworksFromArtistBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.JsonReader;
import se.umu.nien1121.museumapplication.utils.NetworkProperties;

/**
 * Activity displaying {@link Artwork} objects made by a specific artist.
 */
public class ArtworksFromArtistActivity extends AppCompatActivity {
    //Constants
    public static final String ARTIST_NAME_EXTRA = "ARTIST_NAME_EXTRA";
    public static final String ARTIST_ID_EXTRA = "ARTIST_ID_EXTRA";

    private ActivityArtworksFromArtistBinding binding;
    private String artistName;
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtworksFromArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get Artist info from Intent
        this.artistName = getIntent().getStringExtra(ARTIST_NAME_EXTRA);
        this.artistId = getIntent().getStringExtra(ARTIST_ID_EXTRA);

        ActionBarHelper.setActionBar(this, getString(R.string.artworks_by_artist_title, artistName));
        fetchArtworks();
    }

    /**
     * Helper method used to fetch {@link Artwork} objects made by same artist.
     * Uses {@link AsyncTask} to perform fetching on separate thread.
     */
    @SuppressLint("StaticFieldLeak")
    private void fetchArtworks() {
        new AsyncTask<Integer, JSONObject, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Integer... params) {
                try {
                    String url = "http://" + NetworkProperties.getInstance().gatewayIp + "/artist?id=" + artistId;
                    return JsonReader.readJsonFromUrl(url);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject artworksJSON) {
                ArrayList<Artwork> artworks = new ArrayList<>();
                if (artworksJSON != null) {
                    try {
                        JSONArray data = (JSONArray) artworksJSON.get("data");
                        Gson gson = new Gson();
                        for (int i = 0; i < data.length(); i++) {
                            artworks.add(gson.fromJson(data.get(i).toString(), Artwork.class));
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                updateList(artworks);
            }
        }.execute(1);
    }

    /**
     * Helper method used to update {@link androidx.recyclerview.widget.RecyclerView} using {@link ArtworkAdapter}.
     *
     * @param artworks {@link Artwork} objects to be displayed.
     */
    private void updateList(ArrayList<Artwork> artworks) {
        ArtworkAdapter adapter = new ArtworkAdapter(this, artworks);
        binding.artworkRecyclerView.setAdapter(adapter);
    }
}