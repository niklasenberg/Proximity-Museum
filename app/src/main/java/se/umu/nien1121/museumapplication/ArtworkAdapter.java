package se.umu.nien1121.museumapplication;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.utils.DownloadImageTask;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ViewHolder> {

    private final AppCompatActivity activity;
    private final Artwork[] artworks;

    public ArtworkAdapter(AppCompatActivity activity, ArrayList<Artwork> artworks) {
        Artwork[] array = new Artwork[artworks.size()];
        this.artworks = artworks.toArray(array);
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked");
                    Intent artworkIntent = new Intent(activity, ArtworkActivity.class);
                    Artwork artwork = ArtworkAdapter.this.artworks[getAdapterPosition()];
                    artworkIntent.putExtra(ArtworkActivity.ARTWORK_EXTRA, artwork);
                    activity.startActivity(artworkIntent);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artwork, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Artwork artwork = artworks[position];

        if (artwork != null) {
            viewHolder.title_textView.setText(artwork.getTitle());
            viewHolder.author_textView.setText(artwork.getArtistName());
            DownloadImageTask imageTask = new DownloadImageTask(viewHolder.artwork_image);
            imageTask.execute(artwork.getImage());
        } else {
            Log.d("NullArtwork", artwork.toString() + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return artworks.length;
    }
}
