package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import se.umu.nien1121.museumapplication.databinding.ActivityArtistBinding;
import se.umu.nien1121.museumapplication.model.Artist;

public class ArtistActivity extends AppCompatActivity {
    public static final String ARTIST_EXTRA = "ARTIST_EXTRA";
    private ActivityArtistBinding binding;
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.artist = getIntent().getParcelableExtra(ARTIST_EXTRA);
    }
}