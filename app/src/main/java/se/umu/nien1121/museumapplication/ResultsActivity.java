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

        ActionBarHelper.setActionBar(this, "Paintings");

        updateList();
    }

    private void updateList() {
        BeaconAdapter adapter = new BeaconAdapter(beacons);
        binding.artworkRecyclerView.setAdapter(adapter);
    }

    public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

        private final Beacon[] beacons;
        private final OnArtworkClickedListener mCallback;

        public BeaconAdapter(ArrayList<Beacon> beacons) {
            Beacon[] array = new Beacon[beacons.size()];
            this.beacons = beacons.toArray(array);
            mCallback = new OnArtworkClickedListener() {
                @Override
                public void artworkClicked(int position) {
                    Intent artworkIntent = new Intent(ResultsActivity.this, ArtworkActivity.class);
                    artworkIntent.putExtra(ArtworkActivity.ARTWORK_EXTRA, BeaconAdapter.this.beacons[position].getArtwork());
                    startActivity(artworkIntent);
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView title_textView;
            private final TextView author_textView;
            private final ImageView artwork_image;
            private final CardView cardView;

            public ViewHolder(View view) {
                super(view);
                title_textView = view.findViewById(R.id.artwork_title_textView);
                author_textView = view.findViewById(R.id.artwork_author_textView);
                artwork_image = view.findViewById(R.id.imageview_artwork);
                cardView = view.findViewById(R.id.artwork_card);
            }

            public void bind(OnArtworkClickedListener listener){
                int position = getAdapterPosition();

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position >= 0)
                            listener.artworkClicked(position);
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artwork, viewGroup, false);
            return new ViewHolder(view);
        }

        /*Binder objekt till dataobjekt*/
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Artwork artwork = beacons[position].getArtwork();

            if (artwork != null) {
                viewHolder.title_textView.setText(artwork.getTitle());
                viewHolder.author_textView.setText(artwork.getArtistName());
                DownloadImageTask imageTask = new DownloadImageTask(viewHolder.artwork_image);
                imageTask.execute(artwork.getImage());
            }
        }

        @Override
        public int getItemCount() {
            return beacons.length;
        }
    }

    interface OnArtworkClickedListener {
        void artworkClicked(int position);
    }
}