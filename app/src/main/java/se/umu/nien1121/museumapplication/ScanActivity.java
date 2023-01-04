package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import se.umu.nien1121.museumapplication.databinding.ActivityScanBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.JsonReader;
import se.umu.nien1121.museumapplication.utils.NetworkProperties;

/**
 * Activity used for scanning environment for {@link Beacon} objects.
 */
public class ScanActivity extends AppCompatActivity {
    //Constants
    private static final int SCAN_PERIOD = 5; // Stops scanning after 5 seconds.

    //Bluetooth utilities
    private ActivityScanBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings mScanSettings;
    private boolean hasScannedPrior;

    /**
     * Collection of found {@link Beacon} objects during scan
     */
    private final ArrayList<Beacon> beacons = new ArrayList<>();

    /**
     * Callback method used by {@link BluetoothLeScanner} to describe procedure when detecting BLE beacon.
     */
    protected final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //Fetch data from result of scan
            ScanRecord mScanRecord = result.getScanRecord();
            SparseArray<byte[]> manufacturerSpecificData = mScanRecord.getManufacturerSpecificData();
            if (manufacturerSpecificData.size() != 0 && manufacturerSpecificData.valueAt(0) != null) {
                byte[] dataSignature = manufacturerSpecificData.valueAt(0);

                //Filter devices dependent on signature
                if (dataSignature != null && dataSignature.length == 23 && dataSignature[0] == (byte) 0x02 && dataSignature[22] == (byte) 0xc5) {
                    //Decode UUID
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < 18; i++) {
                        sb.append(String.format("%02x", dataSignature[i]));
                    }

                    //Save UUID and RSSI
                    String UUID = sb.toString().toUpperCase();
                    int rssi = Math.abs(result.getRssi());

                    //If beacon has been found prior, update object
                    for (Beacon b : beacons) {
                        if (b.getId().equals(UUID)) {
                            b.evaluateScan(rssi);
                            return;
                        }
                    }

                    //Otherwise add new beacon object
                    Beacon beacon = new Beacon(UUID);
                    beacons.add(beacon);
                    beacon.evaluateScan(rssi);
                }
            }
        }
    };

    /**
     * When created, sets up bluetooth capabilities and starts scan.
     *
     * @param savedInstanceState saved state of destroyed activity, not utilized
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBarHelper.setActionBar(this, getResources().getString(R.string.scanning_title));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        startScanning();

        binding.scanBtn.setOnClickListener(v -> startScanning());
    }

    /**
     * When activity is resumed, clear scan results and reset UI.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (hasScannedPrior) {
            beacons.clear();
            binding.textLoading.setText(R.string.scanning_startScanning_text);
            binding.scanBtn.setVisibility(View.VISIBLE);
            binding.gifImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Helper method for initiating BLE scan.
     */
    private void startScanning() {
        //Modify UI
        binding.textLoading.setText(R.string.scanning_text);
        binding.scanBtn.setVisibility(View.INVISIBLE);
        binding.gifImageView.setVisibility(View.VISIBLE);

        //Check bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //If API level is 31 or higher check BLUETOOTH_SCAN permission
            if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 0);

        } else {
            // If API level is below 31 check BLUETOOTH permission
            if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
            }
        }

        setScanSettings();

        //Start timer with specified duration and start scan
        TimerClass timer = new TimerClass(SCAN_PERIOD);
        timer.run();

        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(null, mScanSettings, mScanCallback);
        }
    }

    /**
     * Helper method for establishing which type of scan to be performed
     */
    private void setScanSettings() {
        //Settings
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    /**
     * Utility class used for scanning a certain period of time. Wraps {@link Timer}.
     */
    public class TimerClass {
        private final int durationInSeconds;
        private final Timer timer = new Timer();

        TimerClass(int durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
        }

        /**
         * Starts timer
         */
        public void run() {
            timer.schedule(new RemindTask(), durationInSeconds * 1000L);
        }

        /**
         * {@link TimerTask} which triggers after timer has run out.
         */
        class RemindTask extends TimerTask {
            @SuppressLint({"MissingPermission", "StaticFieldLeak"})
            public void run() {
                //Stop scan
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(mScanCallback);
                }
                timer.cancel();

                //Creates async task that fetches artwork data on separate thread.
                new AsyncTask<Integer, ArrayList<Beacon>, ArrayList<Beacon>>() {
                    @Override
                    protected ArrayList<Beacon> doInBackground(Integer... params) {
                        //For emulator use only!!!
                        loadBeacons();

                        //Create Artwork object for each Beacon
                        for (Beacon beacon : beacons) {
                            fetchArtwork(beacon);
                        }
                        Collections.sort(beacons);

                        return beacons;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Beacon> beacons) {
                        //Modify UI
                        binding.scanBtn.setEnabled(true);
                        binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.brown));
                        hasScannedPrior = true;

                        if (beacons.size() > 0) {
                            Intent resultIntent = new Intent(ScanActivity.this, ResultsActivity.class);
                            resultIntent.putParcelableArrayListExtra(ResultsActivity.RESULT_EXTRA, beacons);
                            startActivity(resultIntent);
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ScanActivity.this);
                            alert.setMessage(R.string.no_artworks_found);
                            alert.setPositiveButton(R.string.ok_button, null);
                            alert.show();
                            onResume();
                        }
                    }
                }.execute(1);
            }
        }
    }

    /**
     * Helper method for fetching a {@link Beacon} objects {@link Artwork}. Uses Gson to parse JSON to object.
     *
     * @param beacon beacon to fetch artwork for
     */
    private void fetchArtwork(Beacon beacon) {
        try {
            String url = "http://" + NetworkProperties.getInstance().gatewayIp +"/painting?id=" + beacon.getId();
            JSONObject artworkInfo = JsonReader.readJsonFromUrl(url);
            Gson gson = new Gson();
            beacon.setArtwork(gson.fromJson(artworkInfo.toString(), Artwork.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Helper method for simulating successful scan when using emulator (debug purposes)
     */
    private void loadBeacons() {
        //Composition
        Beacon compositionBeacon = new Beacon("C8232AFA1B79451BAD2ABB716704A8BF");
        beacons.add(compositionBeacon);
        compositionBeacon.evaluateScan(20);

        //Mona Lisa
        Beacon monaBeacon = new Beacon("C58C6FC8479A419FA040EE34575CAD04");
        beacons.add(monaBeacon);
        monaBeacon.evaluateScan(24);

        //School-of-Athen
        Beacon athensBeacon = new Beacon("24AB8B4EFD8C4E45AC79DFF20EF814A6");
        beacons.add(athensBeacon);
        athensBeacon.evaluateScan(1);

        //Ocean
        Beacon oceanBeacon = new Beacon("4973BCD43AD04793ACA53C3EDB625707");
        beacons.add(oceanBeacon);
        oceanBeacon.evaluateScan(34);

        //Flower of life
        Beacon flowerBeacon = new Beacon("0B723EBF867A41649660DDDADB1B655A");
        beacons.add(flowerBeacon);
        flowerBeacon.evaluateScan(38);

        //Animals and figures
        Beacon animalBeacon = new Beacon("A79234343CCA46CFA796F5F5FAE0300C");
        beacons.add(animalBeacon);
        animalBeacon.evaluateScan(10);

        //In the Forrest
        Beacon forrestBeacon = new Beacon("26EF226E9A9E416F9C4944710DEF75D8");
        beacons.add(forrestBeacon);
        forrestBeacon.evaluateScan(15);

        //Moon Light
        Beacon moonBeacon = new Beacon("18AC56008F1440ECADBBEEAEB2FC23BB");
        beacons.add(moonBeacon);
        moonBeacon.evaluateScan(60);
    }
}

