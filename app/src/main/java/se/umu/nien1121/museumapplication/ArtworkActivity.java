package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.net.URI;

import se.umu.nien1121.museumapplication.databinding.ActivityArtworkBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.DownloadImageTask;

public class ArtworkActivity extends AppCompatActivity {
    public static final String ARTWORK_EXTRA = "ARTWORK_EXTRA";
    private ActivityArtworkBinding binding;
    private Artwork artwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtworkBinding.inflate(getLayoutInflater());
        artwork = getIntent().getParcelableExtra(ARTWORK_EXTRA);
        setContentView(binding.getRoot());
        loadUI();

    }

    private void loadUI(){
        ActionBarHelper.setActionBar(this, artwork.getTitle());
        binding.artworkNameText.setText(artwork.getTitle());
        binding.artworkArtistText.setText(artwork.getArtistName());
        binding.artworkInfoText.setText(artwork.getDescription());

        //Change the picture

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}