package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.JsonReader;

public class MainActivity extends AppCompatActivity {
    public static final String LIST_KEY = "BEACON_LIST";
    private static final int SCAN_PERIOD = 5; // Stops scanning after 5 seconds.
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings mScanSettings;
    private List<ScanFilter> scanFilters;
    private boolean created;

    private final ArrayList<Beacon> beacons = new ArrayList<>();

    protected final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord(); //fetch scanrecord form result
            SparseArray<byte[]> manufacturerSpecificData = mScanRecord.getManufacturerSpecificData(); //fetch manufacturerspecificdata that stores rssi

            /* Fetch and decode UUID form byte to hexadecimal */
            if (manufacturerSpecificData.size() != 0 && manufacturerSpecificData.valueAt(0) != null) {
                byte[] dataValues = manufacturerSpecificData.valueAt(0);

                if (dataValues != null && dataValues.length == 23 && dataValues[0] == (byte) 0x02 && dataValues[22] == (byte) 0xc5) {
                    StringBuilder sb = new StringBuilder();
                    int rssi = Math.abs(result.getRssi()); //rssi


                    for (int i = 2; i < 18; i++) {
                        sb.append(String.format("%02x", dataValues[i]));
                    }

                    String id = sb.toString().toUpperCase(); //id

                    for (Beacon b : beacons) {

                        if (b.getId().equals(id)) {
                            b.addNewScan(rssi);
                            return;
                        }
                    }

                    Beacon beacon = new Beacon(id);
                    beacons.add(beacon);
                    beacon.addNewScan(rssi);
                }
            } else {
                Log.d("MainActivity", mScanRecord.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("MainActivity", "onScanFailed() " + errorCode);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBarHelper.setActionBar(this, "Scanning");
        createBluetoothAdapter();
        startScanning();

        Log.d("TAG", "onCreate() called");
        binding.scanBtn.setOnClickListener(v -> startScanning());

    }

    @Override
    protected void onResume() {
        super.onResume();
        beacons.clear();

        if (created){
            binding.textLoading.setText(R.string.scanning_startScanning_text);
            binding.scanBtn.setVisibility(View.VISIBLE);
            binding.gifImageView.setVisibility(View.INVISIBLE);

        }
        Log.d("TAG", "onResume() called");
    }

    public class TimerClass {
        Timer timer = new Timer();

        TimerClass(int seconds) {
            timer.schedule(new RemindTask(), seconds * 1000L);
        }

        class RemindTask extends TimerTask {
            @SuppressLint({"MissingPermission", "StaticFieldLeak"})
            public void run() {

                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(mScanCallback);
                }
                System.out.println("Stoppa timer");
                timer.cancel();

                //creates async task that fetches data before presenting
                new AsyncTask<Integer, ArrayList<Beacon>, ArrayList<Beacon>>() {
                    @Override
                    protected ArrayList<Beacon> doInBackground(Integer... params) {
                        Log.d("TAG", "doInBackround() called");

                        //For emulator use
                        // loadBeacons();

                        //Set mean value for rssi and number of reads and create JSON object
                        for (Beacon beacon : beacons) {
                            fetchArtwork(beacon);
                        }
                        Collections.sort(beacons);
                        return beacons;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Beacon> beacons) {
                        Log.d("TAG", "onPostExecute() called");
                        binding.scanBtn.setEnabled(true);
                        binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.brown));
                        created = true;

                        if (beacons.size() > 0) {
                            Intent resultIntent = new Intent(MainActivity.this, ResultsActivity.class);
                            resultIntent.putParcelableArrayListExtra(LIST_KEY, beacons);
                            startActivity(resultIntent);
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setMessage("No nearby artworks to display.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            onResume();
                        }
                    }
                }.execute(1);
            }
        }
    }

    private void startScanning() {
        binding.gifImageView.setVisibility(View.VISIBLE);
        binding.textLoading.setText(R.string.scanning_text);
        binding.scanBtn.setVisibility(View.INVISIBLE);

        new TimerClass(SCAN_PERIOD);

        //Check bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            //If API level is 31 or higher check BLUETOOTH_SCAN permission
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 0);
            Log.d("MainActivity", "Premission not granted");

        } else {
            // If API level is below 31 check BLUETOOTH premission
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                Log.d("MainActivity", "Premission not granted");
            }
        }


        Log.d("MainActivity", "Granted, börjar scanna");
        setScanSettings();
        scanFilters();


        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(scanFilters, mScanSettings, mScanCallback);
        }

    }

    private void fetchArtwork(Beacon beacon) {
        try {
            String url = "http://85.230.192.244/painting?id=" + beacon.getId();
            JSONObject artworkInfo = JsonReader.readJsonFromUrl(url);
            Gson gson = new Gson();
            beacon.setArtwork(gson.fromJson(artworkInfo.toString(), Artwork.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    //For emulator use
    private void loadBeacons() {
        //Composition
        Beacon compositionBeacon = new Beacon("C8232AFA1B79451BAD2ABB716704A8BF");
        compositionBeacon.incrementNumberOfReads();
        compositionBeacon.addToRssiSum(20);
        beacons.add(compositionBeacon);
        compositionBeacon.addNewScan(20);

        //Mona Lisa
        Beacon monaBeacon = new Beacon("C58C6FC8479A419FA040EE34575CAD04");
        monaBeacon.incrementNumberOfReads();
        monaBeacon.addToRssiSum(24);
        beacons.add(monaBeacon);
        monaBeacon.addNewScan(24);

        //School-of-Athen
        Beacon athensBeacon = new Beacon("24AB8B4EFD8C4E45AC79DFF20EF814A6");
        athensBeacon.addToRssiSum(30);
        athensBeacon.incrementNumberOfReads();
        beacons.add(athensBeacon);
        athensBeacon.addNewScan(30);
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    /* Använder vi ens denna? */
    private void scanFilters() {
        scanFilters = new ArrayList<>();
        ScanFilter scanFilterName = new ScanFilter.Builder().setManufacturerData(76, null).build();
        scanFilters.add(scanFilterName);
    }
}

