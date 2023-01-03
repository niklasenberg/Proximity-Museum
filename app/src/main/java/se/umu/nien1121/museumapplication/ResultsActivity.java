package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.databinding.ActivityResultsBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;

/**
 * Activity used for presenting results of {@link ScanActivity}. Only displays the nearest 5 artworks.
 */
public class ResultsActivity extends AppCompatActivity {
    public static final String RESULT_EXTRA = "RESULT_EXTRA";
    private static final int MAX_CAPACITY = 5;
    private ActivityResultsBinding binding;
    private ArrayList<Beacon> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        beacons = getIntent().getParcelableArrayListExtra(RESULT_EXTRA);

        ActionBarHelper.setActionBar(this, getResources().getString(R.string.results_activity_title));

        updateList();
    }

    /**
     * Helper method which populates recycler view using {@link ArtworkAdapter}.
     */
    private void updateList() {
        ArrayList<Artwork> artworks = new ArrayList<>();
        for (Beacon beacon : beacons) {
            //An artwork can != null and still have every element null
            if (beacon.getArtwork().getImage() != null && beacon.getArtwork().getTitle() != null && beacon.getArtwork().getArtistName() != null && artworks.size() < MAX_CAPACITY) {
                artworks.add(beacon.getArtwork());
            }
        }
        ArtworkAdapter adapter = new ArtworkAdapter(this, artworks);
        binding.artworkRecyclerView.setAdapter(adapter);
    }
}