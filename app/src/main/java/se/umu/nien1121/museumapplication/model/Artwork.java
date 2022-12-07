package se.umu.nien1121.museumapplication.model;

import java.util.Arrays;

public class Artwork {
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
