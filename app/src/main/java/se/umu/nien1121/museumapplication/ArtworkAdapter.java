package se.umu.nien1121.museumapplication;

import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.DownloadImageTask;

/**
 * Custom {@link RecyclerView.Adapter} which enables {@link Artwork} objects to be displayed in lists.
 */
public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ViewHolder> {

    //Hosting activity
    private final AppCompatActivity activity;
    //Data collection
    private final Artwork[] artworks;

    public ArtworkAdapter(AppCompatActivity activity, ArrayList<Artwork> artworks) {
        Artwork[] array = new Artwork[artworks.size()];
        this.artworks = artworks.toArray(array);
        this.activity = activity;
    }

    /**
     * Custom {@link RecyclerView.ViewHolder} which specifies custom layout of artworks.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title_textView;
        private final TextView author_textView;
        private final ImageView artwork_image;

        public ViewHolder(View view) {
            super(view);
            //Bind views to holder
            title_textView = view.findViewById(R.id.artwork_title_textView);
            author_textView = view.findViewById(R.id.artwork_author_textView);
            artwork_image = view.findViewById(R.id.imageview_artwork);
            CardView cardView = view.findViewById(R.id.artwork_card);

            //Set listeners
            cardView.setOnClickListener(view1 -> {
                System.out.println("Clicked");
                Intent artworkIntent = new Intent(activity, ArtworkActivity.class);
                Artwork artwork = ArtworkAdapter.this.artworks[getAdapterPosition()];
                artworkIntent.putExtra(ArtworkActivity.ARTWORK_EXTRA, artwork);
                activity.startActivity(artworkIntent);
            });
        }
    }

    /**
     * Given a custom view, creates {@link ViewHolder} to store artwork info in.
     *
     * @param viewGroup - "The ViewGroup into which the new View will be added after it is bound to an adapter position."
     * @param viewType  – "The view type of the new View"
     * @return a new ViewHolder object
     */
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artwork, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Populates ViewHolder data with data. Uses {@link DownloadImageTask} to establish ImageView.
     *
     * @param viewHolder {@link ViewHolder} object to populate with data
     * @param position   index of current {@link Artwork} in data collection.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Artwork artwork = artworks[position];

        if (artwork != null) {
            viewHolder.title_textView.setText(artwork.getTitle());
            viewHolder.author_textView.setText(artwork.getArtistName());
            DownloadImageTask imageTask = new DownloadImageTask(viewHolder.artwork_image);
            imageTask.execute(artwork.getImage());
        }
    }

    /**
     * Required by superclass
     *
     * @return size of data collection (amount of {@link Artwork} objects).
     */
    @Override
    public int getItemCount() {
        return artworks.length;
    }
}
