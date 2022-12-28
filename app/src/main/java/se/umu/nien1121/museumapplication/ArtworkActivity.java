package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

    private void loadUI() {
        ActionBarHelper.setActionBar(this, artwork.getTitle());
        binding.artworkNameText.setText(artwork.getTitle());
        binding.artworkArtistText.setText(artwork.getArtistName());
        binding.artworksFromArtistText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.artworksFromArtistText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artistIntent = new Intent(ArtworkActivity.this, ArtistActivity.class);
                artistIntent.putExtra(ArtistActivity.ARTIST_NAME_EXTRA, artwork.getArtistName());
                artistIntent.putExtra(ArtistActivity.ARTIST_ID_EXTRA, artwork.getArtistId());
                startActivity(artistIntent);
            }
        });
        setDescription();

        DownloadImageTask imageTask = new DownloadImageTask(binding.imageView);
        imageTask.execute(artwork.getImage());

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //View the image in full screen
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtworkActivity.this);
                ImageView image = new ImageView(ArtworkActivity.this);
                alert.setView(image);
                image.setAdjustViewBounds(true);
                DownloadImageTask imageTask2 = new DownloadImageTask(image);
                imageTask2.execute(artwork.getImage());
                alert.show();

            }
        });


        if (artwork.getCompletitionYear() != 0) {
            binding.artworkCompletionYearText.setText(String.valueOf("Completion year: " + artwork.getCompletitionYear()));
        } else {
            binding.artworkCompletionYearText.setText("Year unknown");
        }
    }

    private void setDescription() {
        if (artwork.getDescription() != null && !artwork.getDescription().isEmpty()) {

            //removes everything within the brackets.
            String description = artwork.getDescription().replaceAll("\\[.*?\\]","");
            binding.artworkInfoText.setText(description);
        } else {
            binding.artworkInfoText.setText("There is no description for this artwork.");
        }
    }
}