package se.umu.nien1121.museumapplication.model;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class Beacon implements Comparable<Beacon> {
    private final String id;
    private Artwork artwork;
    private int rssi;

    public Beacon(String id, int rssi){
        this.id = id;
        this.rssi = -rssi;
    }

    public String getId() {
        return id;
    }

    public int getRssi() {
        return rssi;
    }

    public Artwork getArtwork(){
        return artwork;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setArtwork(Artwork artwork){
        this.artwork = artwork;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "id='" + id + '\'' +
                ", artwork=" + artwork +
                ", rssi=" + rssi +
                '}';
    }

    @Override
    public int compareTo(Beacon beacon) {
        return rssi - beacon.rssi;
    }

    public static class Artwork {
        private String title;
        private String artistName;
        private int completitionYear;
        private String[] styles;
        private String description;
        private String imageUrl;

        public Artwork(String title, String artistName, int completitionYear, String[] styles, String description, String imageUrl) {
            this.title = title;
            this.artistName = artistName;
            this.completitionYear = completitionYear;
            this.styles = styles;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getArtistName() {
            return artistName;
        }

        public int getCompletitionYear() {
            return completitionYear;
        }

        public String[] getStyles() {
            return styles;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        @Override
        public String toString() {
            return "Artwork{" +
                    "title='" + title + '\'' +
                    ", artistName='" + artistName + '\'' +
                    ", completitionYear=" + completitionYear +
                    ", styles=" + Arrays.toString(styles) +
                    ", description='" + description + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }
}
