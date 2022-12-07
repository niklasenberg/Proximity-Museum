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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.model.Artwork;
import se.umu.nien1121.museumapplication.model.JsonReader;

public class MainActivity extends AppCompatActivity {

    // Stops scanning after 10 seconds.
    private static final int SCAN_PERIOD = 10000;

    /*   private boolean scanning;
       private Handler handler = new Handler();
       private BluetoothManager bluetoothManager;
       private final int REQUEST_ENABLE_BT = 1; */
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings mScanSettings;
    private List<ScanFilter> scanFilters;
    private ArrayList<String> beacons = new ArrayList<>();
    private boolean scanning;

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
                try {
                    getArtworkInfo();
                } catch (Exception e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void getArtworkInfo() throws JSONException, IOException {
        String url = "http://85.230.192.244/painting?id=" + "C58C6FC8479A419FA040EE34575CAD04"; //Ersätt med beacons[0]
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
                    StringBuilder sb = new StringBuilder();

                    for (int i = 2; i < 18; i++) {
                        sb.append(String.format("%02x", datavalue[i]));
                    }
                    if (!beacons.contains(sb.toString()))
                        beacons.add(sb.toString().toUpperCase()); //Adds beacon uuid to linkedhashset

                }
            } else {
                Log.d("MainActivity", mScanRecord.toString());
            }


            for (String s : beacons) {
                Log.d("MainActivity", "Beacon: " + s);
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

                //times the scanning
                new TimerClass(5);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                    Log.d("MainActivity", "Premission not granted");
                }
                Log.d("MainActivity", "Granted, börjar scanna");
                setScanSettings();
                scanFilters();
                //bluetoothAdapter.startLeScan(mLeScanCallback);
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.startScan(scanFilters, mScanSettings, mScanCallback);
                }
                binding.scanBtn.setEnabled(false);
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));


               /*
                //Testar lägga in scan period
                if (!scanning) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            bluetoothLeScanner.stopScan(mScanCallback);
                        }
                    }, SCAN_PERIOD);
                } */


            }
        });
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
        //ScanFilter scanFilterTime = new ScanFilter.Builder()
        scanFilters.add(scanFilterName);
    }
}