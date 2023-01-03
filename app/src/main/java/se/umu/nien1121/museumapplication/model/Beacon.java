package se.umu.nien1121.museumapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Data class representing a BLE beacon. Implements {@link Parcelable} to be sent between activities.
 * Implements {@link Comparable<Beacon>} for sorting purposes.
 */
public class Beacon implements Comparable<Beacon>, Parcelable {
    public static final int OUTLIER_BOUNDARY_VALUE = 10;
    private final String id;
    private Artwork artwork;
    private int latestRssi;
    private int rssiSum;
    private int numberOfReads;

    /**
     * Constructor which creates entirely new Beacon object
     * @param id the id-serial of the beacon to be created
     */
    public Beacon(String id) {
        this.id = id;
    }

    /**
     * Constructor which creates Beacon objects from priorly established {@link Parcel}.
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
     * Preserves count of how many times this beacon has been scanned.
     */
    public void incrementNumberOfReads() {
        numberOfReads++;
    }

    /**
     * Adds rssi-value to total sum of values (rssiSum), for average calculation purposes.
     * @param addend rssi value to be added (positive)
     */
    public void addToRssiSum(int addend) {
        rssiSum += Math.abs(addend);
    }

    public void addNewScan(int rssi) {
        if (latestRssi == 0) {
            latestRssi = Math.abs(rssi);
            incrementNumberOfReads();
            addToRssiSum(rssi);
        } else {
            int difference = Math.abs(rssi - latestRssi);
            System.out.println("Difference: " + difference);

            if (difference < OUTLIER_BOUNDARY_VALUE) {
                latestRssi = Math.abs(rssi);
                incrementNumberOfReads();
                addToRssiSum(rssi);
            }
        }
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
