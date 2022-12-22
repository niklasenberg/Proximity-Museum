package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.databinding.ActivityArtistBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;

public class ArtistActivity extends AppCompatActivity {
    public static final String ARTIST_NAME_EXTRA = "ARTIST_NAME_EXTRA";
    public static final String ARTIST_ID_EXTRA = "ARTIST_ID_EXTRA";
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

    private void fetchArtworks() {

    }
}