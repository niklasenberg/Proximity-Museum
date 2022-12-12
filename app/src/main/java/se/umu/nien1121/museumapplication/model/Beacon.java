package se.umu.nien1121.museumapplication.model;

public class Beacon implements Comparable<Beacon> {
    private int rssi;
    private String id;

    public Beacon(String id, int rssi){
        this.id = id;
        this.rssi = -rssi;
    }

    @Override
    public int compareTo(Beacon beacon) {
        return rssi - beacon.rssi;
    }

    public int getRssi() {
        return rssi;
    }

    public String getId() {
        return id;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}
