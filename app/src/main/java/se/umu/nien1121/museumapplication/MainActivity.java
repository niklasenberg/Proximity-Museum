package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;
import se.umu.nien1121.museumapplication.utils.JsonReader;

public class MainActivity extends AppCompatActivity {
    public static final String LIST_KEY = "BEACON_LIST";

    // Stops scanning after 5 seconds.
    private static final int SCAN_PERIOD = 5;
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings mScanSettings;
    private List<ScanFilter> scanFilters;
    private ArrayList<Beacon> beacons = new ArrayList<>();
    private HashMap<String, Integer> beaconIdToNumberOfReads = new HashMap<>();
    private HashMap<String, Integer> beaconIdToRssiSum = new HashMap<>();

    public class TimerClass {
        Timer timer = new Timer();

        TimerClass(int seconds) {
            timer.schedule(new RemindTask(), seconds * 1000);
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

                        //For emulator use
                        beacons.add(new Beacon("C8232AFA1B79451BAD2ABB716704A8BF", 20));
                        beaconIdToNumberOfReads.put("C8232AFA1B79451BAD2ABB716704A8BF", 1);
                        beaconIdToRssiSum.put("C8232AFA1B79451BAD2ABB716704A8BF", 20);

                        for (Beacon beacon : beacons) {
                            int numberOfReads = beaconIdToNumberOfReads.get(beacon.getId());
                            int rssiSum = beaconIdToRssiSum.get(beacon.getId());
                            beacon.setRssi(rssiSum / numberOfReads);

                            try {
                                Log.d("MainActivity", "Jag nådde hit");
                                String url = "http://85.230.192.244/painting?id=" + beacon.getId();
                                JSONObject artworkInfo = JsonReader.readJsonFromUrl(url);
                                Log.d("MainActivity", "Jag nådde hit också");
                                Gson gson = new Gson();
                                beacon.setArtwork(gson.fromJson(artworkInfo.toString(), Beacon.Artwork.class));
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        Collections.sort(beacons);

                        for (Beacon b : beacons) {
                            Log.d("Beacons", b.getId() + ": " + b.getRssi());
                        }
                        return beacons;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Beacon> beacons) {

                        if (beacons.size() > 0) {
                            /* Detta ska göras efter vi hämtat all info*/
                            binding.scanBtn.setEnabled(true);
                            binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            Intent resultIntent = new Intent(MainActivity.this, ResultsActivity.class);
                            resultIntent.putParcelableArrayListExtra(LIST_KEY, beacons);
                            startActivity(resultIntent);
                        }
                    }
                }.execute(1);
            }
        }
    }

    protected final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord(); //fetch scanrecord form result
            SparseArray<byte[]> data = mScanRecord.getManufacturerSpecificData(); //fetch manufacturerspecificdata that stores rssi

            /* Fetch and decode UUID form byte to hexadecimal */
            if (data.size() != 0 && data.valueAt(0) != null) {
                byte[] datavalue = data.valueAt(0);

                if (datavalue != null && datavalue.length == 23 && datavalue[0] == (byte) 0x02 && datavalue[22] == (byte) 0xc5) {
                    StringBuilder sb = new StringBuilder(); //id
                    int rssi = result.getRssi(); //rssi

                    for (int i = 2; i < 18; i++) {
                        sb.append(String.format("%02x", datavalue[i]));
                    }

                    String id = sb.toString().toUpperCase();

                    for (Beacon b : beacons) {
                        if (b.getId().equals(id)) {
                            beaconIdToNumberOfReads.put(id, beaconIdToNumberOfReads.get(id) + 1);
                            beaconIdToRssiSum.put(id, beaconIdToRssiSum.get(id) + rssi);
                            return;
                        }
                    }

                    Beacon beacon = new Beacon(id, rssi);
                    beacons.add(beacon);
                    beaconIdToNumberOfReads.put(id, 1);
                    beaconIdToRssiSum.put(id, rssi);
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }


        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Times the scanning
                new TimerClass(SCAN_PERIOD);

                //Check bluetooth premissions
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    //If API level is 31 or higher check BLUETOOTH_SCAN premission
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
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.startScan(scanFilters, mScanSettings, mScanCallback);
                }
                binding.scanBtn.setEnabled(false);
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        beacons.clear();
        beaconIdToNumberOfReads.clear();
        beaconIdToRssiSum.clear();
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    private void scanFilters() {
        scanFilters = new ArrayList<>();
        ScanFilter scanFilterName = new ScanFilter.Builder().setManufacturerData(76, null).build();
        scanFilters.add(scanFilterName);
    }
}

//Testa avstånd med Eddies telefon
//Snygga till kod
//Fixa UI (bildstorlek, detaljsida mm.)
