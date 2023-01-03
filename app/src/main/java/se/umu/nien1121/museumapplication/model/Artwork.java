package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * Data class representing an artwork, based on PaintingJson from <a href="https://www.wikiart.org">WikiArt API</a>.
 * Implements {@link Parcelable} to be sent between activities.
 */
public class Artwork implements Parcelable {
    private final String title;
    private final String artistName;
    private final String artistId;
    private final int completitionYear;
    private final String[] styles;
    private final String description;
    private final String image;

    public Artwork(String title, String artistName, String artistId, int completitionYear, String[] styles, String description, String image) {
        this.title = title;
        this.artistName = artistName;
        this.artistId = artistId;
        this.completitionYear = completitionYear;
        this.styles = styles;
        this.description = description;
        this.image = image;
    }

    protected Artwork(Parcel in) {
        this.title = in.readString();
        this.artistName = in.readString();
        this.artistId = in.readString();
        this.completitionYear = in.readInt();
        this.styles = in.createStringArray();
        this.description = in.readString();
        this.image = in.readString();
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public int getCompletitionYear() {
        return completitionYear;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    @Override
    @NonNull
    public String toString() {
        return "Artwork{" + "title='" + title + '\'' + ", artistName='" + artistName + '\'' + ", completitionYear=" + completitionYear + ", styles=" + Arrays.toString(styles) + ", description='" + description + '\'' + ", image='" + image + '\'' + '}';
    }

    //Parcelable implementations
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artistName);
        parcel.writeString(artistId);
        parcel.writeInt(completitionYear);
        parcel.writeStringArray(styles);
        parcel.writeString(description);
        parcel.writeString(image);
    }

    /**
     * Companion object required to instantiate Parcel objects.
     */
    public static final Parcelable.Creator<Artwork> CREATOR = new Parcelable.Creator<Artwork>() {
        public Artwork createFromParcel(Parcel in) {
            return new Artwork(in);
        }

        public Artwork[] newArray(int size) {
            return new Artwork[size];
        }
    };
}
