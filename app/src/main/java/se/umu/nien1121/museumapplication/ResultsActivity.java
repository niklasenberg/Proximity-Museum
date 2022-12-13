package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import se.umu.nien1121.museumapplication.model.Beacon;

public class ResultsActivity extends AppCompatActivity {

    private List<Beacon> beacons;
    private BeaconAdapter adapter = new BeaconAdapter(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        beacons = getIntent().getParcelableArrayListExtra(MainActivity.LIST_KEY);
        System.out.println(beacons.get(0));
    }

    public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

        private Beacon[] beacons;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView title_textView;
            private final TextView author_textView;
            private final ImageView artwork_image;


            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                title_textView = view.findViewById(R.id.artwork_title_textView);
                author_textView = view.findViewById(R.id.artwork_author_textView);
                artwork_image = view.findViewById(R.id.imageview_artwork);
            }
        }

        public BeaconAdapter(ArrayList<Beacon> beacons) {
            Beacon[] array = new Beacon[beacons.size()];
            this.beacons = beacons.toArray(array);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artwork, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)

        /*Binder objekt till dataobjekt*/
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Beacon.Artwork artwork = beacons[position].getArtwork();

            if (artwork != null) {
                viewHolder.title_textView.setText(artwork.getTitle());
                viewHolder.author_textView.setText(artwork.getArtistName());
                DownloadImageTask imageTask = new DownloadImageTask(viewHolder.artwork_image);
                imageTask.execute(artwork.getImage());
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return beacons.length;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                if (urldisplay != null) {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}