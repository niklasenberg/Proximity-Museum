package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import se.umu.nien1121.museumapplication.databinding.ActivityArtworkBinding;
import se.umu.nien1121.museumapplication.model.Artwork;

public class ArtworkActivity extends AppCompatActivity {
    public static final String ARTWORK_EXTRA = "ARTWORK_EXTRA";
    private ActivityArtworkBinding binding;
    private Artwork artwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        artwork = getIntent().getParcelableExtra(ARTWORK_EXTRA);

        System.out.println(artwork);
    }
}