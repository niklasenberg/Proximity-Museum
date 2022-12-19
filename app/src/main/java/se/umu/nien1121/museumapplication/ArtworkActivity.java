package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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
        Log.d("Year", String.valueOf(artwork.getCompletitionYear()));
       // binding.artworkCompletionYearText.setText(artwork.getTitle());
        DownloadImageTask imageTask = new DownloadImageTask(binding.imageView);
        imageTask.execute(artwork.getImage());

        if (artwork.getCompletitionYear() != 0){
            binding.artworkCompletionYearText.setText(String.valueOf(artwork.getCompletitionYear()));
        } else {
            binding.artworkCompletionYearText.setText("Year not known");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}