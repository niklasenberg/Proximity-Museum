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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.stream.Collectors;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.model.JsonReader;

public class MainActivity extends AppCompatActivity {

    // Stops scanning after 5 seconds.
    private static final int SCAN_PERIOD = 5;

    /*   private boolean scanning;
       private Handler handler = new Handler();
       private BluetoothManager bluetoothManager;
       private final int REQUEST_ENABLE_BT = 1; */
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings mScanSettings;
    private List<ScanFilter> scanFilters;
    private ArrayList<Beacon> beacons = new ArrayList<>();
    private HashMap<String, Integer> beaconsWithRssi = new HashMap<>();

    public class TimerClass {
        Timer timer = new Timer();

        TimerClass(int seconds) {
            timer.schedule(new RemindTask(), seconds * 1000);
        }

        class RemindTask extends TimerTask {
            @SuppressLint("MissingPermission")
            public void run() {
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(mScanCallback);
                }
                System.out.println("Stoppa timer");
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                timer.cancel();

            }
        }
    }

    private void getArtworkInfo(String id) throws JSONException, IOException {
        String url = "http://85.230.192.244/painting?id=" + id;
        JSONObject artworkInfo = JsonReader.readJsonFromUrl(url);
        Gson gson = new Gson();
        Artwork artwork = gson.fromJson(artworkInfo.toString(), Artwork.class);
        System.out.println(artwork);
    }

    protected final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            SparseArray<byte[]> data = mScanRecord.getManufacturerSpecificData();

            //Fetch and decode UUID form byte to hexadecimal
            if (data.size() != 0 && data.valueAt(0) != null) {
                byte[] datavalue = data.valueAt(0);

                if (datavalue != null && datavalue.length == 23 && datavalue[0] == (byte) 0x02 && datavalue[22] == (byte) 0xc5) {
                    StringBuilder sb = new StringBuilder(); //id
                    int rssi = result.getRssi(); //rssi
                    Log.d("Rssi", String.valueOf(rssi));

                    for (int i = 2; i < 18; i++) {
                        sb.append(String.format("%02x", datavalue[i]));
                    }

                    for (Beacon b: beacons){
                        if (b.getId().equals(sb.toString())) {
                            b.setRssi(rssi);
                            return;
                        }
                    }

                    Beacon beacon = new Beacon (sb.toString(), rssi);
                    beacons.add(beacon);
                    Collections.sort(beacons);
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }


        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Times the scanning
                new TimerClass(30);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                    Log.d("MainActivity", "Premission not granted");
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

    /* Finds nearest beacon from scanning
    private void findBeacon() {
        int smallestValue = 0;
        String beaconid = null;

        //Find the beacon with the highest RSSI (highest since its a negative number)
        if (beaconsWithRssi.values().size() != 0 || beaconsWithRssi.values() != null) {
            smallestValue = Collections.max(beaconsWithRssi.values()); //max since rssi is a negative number
        }

        for (Map.Entry<String, Integer> entry : beaconsWithRssi.entrySet()) {
            if (entry.getValue() == smallestValue) {
                if (beaconid == null) {   //Kom på annan lösning för om två beacons har samma rssi
                    beaconid = entry.getKey();
                }
            }
        }

        //Get artwork information
        try {
            getArtworkInfo(beaconid);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

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