package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Beacon implements Comparable<Beacon>, Parcelable {
    private final String id;
    private Artwork artwork;
    private int rssi;

    public Beacon(String id, int rssi) {
        this.id = id;
        this.rssi = Math.abs(rssi);
    }

    protected Beacon(Parcel in) {
        id = in.readString();
        artwork = in.readParcelable(Artwork.class.getClassLoader());
        rssi = in.readInt();
    }

    public static final Creator<Beacon> CREATOR = new Creator<Beacon>() {
        @Override
        public Beacon createFromParcel(Parcel in) {
            return new Beacon(in);
        }

        @Override
        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }
    };

    public String getId() {
        return id;
    }

    public int getRssi() {
        return rssi;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setRssi(int rssi) {
        this.rssi = Math.abs(rssi);
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    @Override
    public String toString() {
        return "Beacon{" + "id='" + id + '\'' + ", artwork=" + artwork + ", rssi=" + rssi + '}';
    }

    @Override
    public int compareTo(Beacon beacon) {
        return rssi - beacon.rssi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(artwork,1);
        parcel.writeInt(rssi);
    }
}
