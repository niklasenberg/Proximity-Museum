package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon implements Comparable<Beacon>, Parcelable {
    public static final int OUTLIER_BOUNDARY_VALUE = 10;
    private final String id;
    private Artwork artwork;
    private int latestRssi;
    private int rssiSum;
    private int numberOfReads;

    public Beacon(String id) {
        this.id = id;
    }

    protected Beacon(Parcel in) {
        id = in.readString();
        artwork = in.readParcelable(Artwork.class.getClassLoader());
        latestRssi = in.readInt();
        rssiSum = in.readInt();
        numberOfReads = in.readInt();
    }

    public void incrementNumberOfReads() {
        numberOfReads++;
    }

    public void addToRssiSum(int addend) {
        rssiSum += Math.abs(addend);
    }

    public void addNewScan(int rssi) {
        int difference = Math.abs(rssi - latestRssi);

        if (difference < OUTLIER_BOUNDARY_VALUE) {
            latestRssi = Math.abs(rssi);
            incrementNumberOfReads();
            addToRssiSum(rssi);
        }
    }

    public String getId() {
        return id;
    }

    public int getAverageRssi() {
        return rssiSum / numberOfReads;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    @Override
    public String toString() {
        return "Beacon{" + "id='" + id + '\'' + ", artwork=" + artwork + ", rssi=" + getAverageRssi() + '}';
    }

    @Override
    public int compareTo(Beacon beacon) {
        return getAverageRssi() - beacon.getAverageRssi();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(artwork, 1);
        parcel.writeInt(latestRssi);
        parcel.writeInt(rssiSum);
        parcel.writeInt(numberOfReads);
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
}
