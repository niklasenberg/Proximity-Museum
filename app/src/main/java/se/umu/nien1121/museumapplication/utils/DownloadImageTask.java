package se.umu.nien1121.museumapplication.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Utility class used for downloading images from the web and using them in {@link ImageView}s.
 * Extends {@link AsyncTask} in order to fetch image on separate thread.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private final ImageView imageView;

    /**
     * Specifies which {@link ImageView} to insert image into.
     *
     * @param imageView ImageView to be populated.
     */
    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Establishes bitmap to be sent to ImageView.
     *
     * @param urls url to fetch image from.
     * @return Bitmap containing fetched image.
     */
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bm = null;
        try {
            if (url != null) {
                InputStream in = new java.net.URL(url).openStream();
                bm = BitmapFactory.decodeStream(in);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * Once bitmap has been established, populates ImageView with it.
     *
     * @param result established bitmap
     */
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}

