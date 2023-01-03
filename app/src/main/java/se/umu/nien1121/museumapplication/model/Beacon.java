package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Data class representing a BLE beacon. Implements {@link Parcelable} to be sent between activities.
 * Implements {@link Comparable<Beacon>} for sorting purposes.
 */
public class Beacon implements Comparable<Beacon>, Parcelable {
    private static final int OUTLIER_BOUNDARY_VALUE = 10;
    private final String id;
    private Artwork artwork;
    private int latestRssi;
    private int rssiSum;
    private int numberOfReads;

    /**
     * Constructor which creates entirely new Beacon object
     *
     * @param id the id-serial of the beacon to be created
     */
    public Beacon(String id) {
        this.id = id;
    }

    /**
     * Constructor which creates Beacon objects from priorly established {@link Parcel}.
     *
     * @param in {@link Parcel} object which contains Beacon information.
     */
    protected Beacon(Parcel in) {
        id = in.readString();
        artwork = in.readParcelable(Artwork.class.getClassLoader());
        latestRssi = in.readInt();
        rssiSum = in.readInt();
        numberOfReads = in.readInt();
    }

    /**
     * Evaluates whether latest scan result is valid
     *
     * @param rssi new signal strength value
     */
    public void evaluateScan(int rssi) {
        if (latestRssi == 0) { //If first scan result
            addNewScan(rssi);
        } else {
            int difference = Math.abs(rssi - latestRssi);
            if (difference < OUTLIER_BOUNDARY_VALUE) { //If within boundary value
                addNewScan(rssi);
            }
        }
    }

    /**
     * Adds valid RSSI value to cache
     *
     * @param rssi new signal strength value to be added
     */
    private void addNewScan(int rssi) {
        latestRssi = Math.abs(rssi);
        numberOfReads++;
        rssiSum += Math.abs(rssi);
    }

    //Getters
    public String getId() {
        return id;
    }

    public int getAverageRssi() {
        return rssiSum / numberOfReads;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    //Setters
    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    @Override
    @NonNull
    public String toString() {
        return "Beacon{" + "id='" + id + '\'' + ", artwork=" + artwork + ", rssi=" + getAverageRssi() + '}';
    }

    //Comparable implementation
    @Override
    public int compareTo(Beacon beacon) {
        return getAverageRssi() - beacon.getAverageRssi();
    }

    //Parcelable implementations
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

    /**
     * Companion object required to instantiate Parcel objects.
     */
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
