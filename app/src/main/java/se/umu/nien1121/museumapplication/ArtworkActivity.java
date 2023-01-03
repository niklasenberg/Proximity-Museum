package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.ImageView;

import se.umu.nien1121.museumapplication.databinding.ActivityArtworkBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.DownloadImageTask;

/**
 * Acitivty useed to present the details of a specific {@link Artwork}.
 */
public class ArtworkActivity extends AppCompatActivity {
    //Constants
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

    /**
     * Helper method used to populate user interface with data from {@link Artwork}.
     */
    private void loadUI() {
        ActionBarHelper.setActionBar(this, artwork.getTitle());

        binding.artworkNameText.setText(artwork.getTitle());
        binding.artworkArtistText.setText(artwork.getArtistName());
        binding.artworksFromArtistText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.artworksFromArtistText.setOnClickListener(view -> {
            Intent artistIntent = new Intent(ArtworkActivity.this, ArtworksFromArtistActivity.class);
            artistIntent.putExtra(ArtworksFromArtistActivity.ARTIST_NAME_EXTRA, artwork.getArtistName());
            artistIntent.putExtra(ArtworksFromArtistActivity.ARTIST_ID_EXTRA, artwork.getArtistId());
            startActivity(artistIntent);
        });

        setDescription();
        setCompletionYear();
        setImage();
        makeImageClickable();
    }

    /**
     * Helper method for null-checking and formatting description of {@link Artwork}.
     */
    private void setDescription() {
        if (artwork.getDescription() != null && !artwork.getDescription().isEmpty()) {
            //removes everything within the brackets.
            String description = artwork.getDescription().replaceAll("\\[.*?]", "");
            binding.artworkInfoText.setText(description);
        } else {
            binding.artworkInfoText.setText(R.string.description_not_found);
        }
    }

    /**
     * Helper method for null-checking completion year of {@link Artwork}.
     */
    private void setCompletionYear() {
        if (artwork.getCompletitionYear() != 0) {
            binding.artworkCompletionYearText.setText(getString(R.string.artwork_completionYear_text, String.valueOf(artwork.getCompletitionYear())));
        } else {
            binding.artworkCompletionYearText.setText(R.string.unknown_year);
        }
    }

    /**
     * Helper method which uses {@link DownloadImageTask} to fetch image of {@link Artwork}.
     */
    private void setImage() {
        DownloadImageTask imageTask = new DownloadImageTask(binding.imageView);
        imageTask.execute(artwork.getImage());
    }

    /**
     * Helper method which makes ImageView clickable to enlarge.
     */
    private void makeImageClickable() {
        binding.imageView.setOnClickListener(view -> {
            //View the image in full screen
            AlertDialog.Builder alert = new AlertDialog.Builder(ArtworkActivity.this);
            ImageView image = new ImageView(ArtworkActivity.this);
            alert.setView(image);
            image.setAdjustViewBounds(true);
            DownloadImageTask imageTask = new DownloadImageTask(image);
            imageTask.execute(artwork.getImage());
            alert.show();
        });
    }
}