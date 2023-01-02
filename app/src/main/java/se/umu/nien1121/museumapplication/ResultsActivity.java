package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.databinding.ActivityResultsBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.DownloadImageTask;

public class ResultsActivity extends AppCompatActivity {

    private ActivityResultsBinding binding;
    private ArrayList<Beacon> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        beacons = getIntent().getParcelableArrayListExtra(MainActivity.LIST_KEY);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBarHelper.setActionBar(this, "Nearby paintings");

        updateList();
    }

    private void updateList() {
        ArrayList<Artwork> artworks = new ArrayList<>();
        for(Beacon beacon : beacons){
            //An artwork can != null and still have every element null
            if(beacon.getArtwork().getImage() != null && beacon.getArtwork().getTitle() != null && beacon.getArtwork().getArtistName() != null
                    && artworks.size()<5){
                artworks.add(beacon.getArtwork());
            }
        }
        ArtworkAdapter adapter = new ArtworkAdapter(this ,artworks);
        binding.artworkRecyclerView.setAdapter(adapter);
    }
}