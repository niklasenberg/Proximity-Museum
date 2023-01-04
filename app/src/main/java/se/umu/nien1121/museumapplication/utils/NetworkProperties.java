package se.umu.nien1121.museumapplication.utils;

/**
 * Properties class for configuring endpoint IP adress.
 */
public class NetworkProperties {
    private static NetworkProperties instance = null;
    public String gatewayIp = "GATEWAY IP GOES HERE";

    protected NetworkProperties() {
    }

    /**
     * Implements singleton pattern to ensure static variables are not consumed by GC.
     *
     * @return global instance of class
     */
    public static synchronized NetworkProperties getInstance() {
        if (instance == null) {
            instance = new NetworkProperties();
        }
        return instance;
    }
}
