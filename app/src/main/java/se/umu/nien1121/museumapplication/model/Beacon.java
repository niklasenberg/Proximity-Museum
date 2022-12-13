package se.umu.nien1121.museumapplication.model;

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
        private String image;

        public Artwork(String title, String artistName, int completitionYear, String[] styles, String description, String image) {
            this.title = title;
            this.artistName = artistName;
            this.completitionYear = completitionYear;
            this.styles = styles;
            this.description = description;
            this.image = image;
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

        public String getImage() {
            return image;
        }

        @Override
        public String toString() {
            return "Artwork{" +
                    "title='" + title + '\'' +
                    ", artistName='" + artistName + '\'' +
                    ", completitionYear=" + completitionYear +
                    ", styles=" + Arrays.toString(styles) +
                    ", description='" + description + '\'' +
                    ", image='" + image + '\'' +
                    '}';
        }
    }
}
