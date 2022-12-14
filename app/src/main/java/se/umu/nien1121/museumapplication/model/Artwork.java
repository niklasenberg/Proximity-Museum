package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Artwork implements Parcelable {
    private String title;
    private String artistName;
    private int completionYear;
    private String[] styles;
    private String description;
    private String image;

    public Artwork(String title, String artistName, int completionYear, String[] styles, String description, String image) {
        this.title = title;
        this.artistName = artistName;
        this.completionYear = completionYear;
        this.styles = styles;
        this.description = description;
        this.image = image;
    }

    protected Artwork(Parcel in) {
        this.title = in.readString();
        this.artistName = in.readString();
        this.completionYear = in.readInt();
        this.styles = in.createStringArray();
        this.description = in.readString();
        this.image = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getCompletionYear() {
        return completionYear;
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
        return "Artwork{" + "title='" + title + '\'' + ", artistName='" + artistName + '\'' + ", completitionYear=" + completionYear + ", styles=" + Arrays.toString(styles) + ", description='" + description + '\'' + ", image='" + image + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artistName);
        parcel.writeInt(completionYear);
        parcel.writeStringArray(styles);
        parcel.writeString(description);
        parcel.writeString(image);
    }

    public static final Parcelable.Creator<Artwork> CREATOR = new Parcelable.Creator<Artwork>() {
        public Artwork createFromParcel(Parcel in) {
            return new Artwork(in);
        }

        public Artwork[] newArray(int size) {
            return new Artwork[size];
        }
    };
}
