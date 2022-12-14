package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    private final String id;
    private final String artistName;
    private final String url;
    private final String image;
    private final String birthDayAsString;
    private final String deathDayAsString;
    private final String biography;

    public Artist(String id, String artistName, String url, String image, String birthDayAsString, String deathDayAsString, String biography) {
        this.id = id;
        this.artistName = artistName;
        this.url = url;
        this.image = image;
        this.birthDayAsString = birthDayAsString;
        this.deathDayAsString = deathDayAsString;
        this.biography = biography;
    }

    public Artist(Parcel in) {
        this.id = in.readString();
        this.artistName = in.readString();
        this.url = in.readString();
        this.image = in.readString();
        this.birthDayAsString = in.readString();
        this.deathDayAsString = in.readString();
        this.biography = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(artistName);
        parcel.writeString(url);
        parcel.writeString(image);
        parcel.writeString(birthDayAsString);
        parcel.writeString(deathDayAsString);
        parcel.writeString(biography);
    }

    public static final Creator<Artwork> CREATOR = new Creator<Artwork>() {
        @Override
        public Artwork createFromParcel(Parcel in) {
            return new Artwork(in);
        }

        @Override
        public Artwork[] newArray(int size) {
            return new Artwork[size];
        }
    };
}
