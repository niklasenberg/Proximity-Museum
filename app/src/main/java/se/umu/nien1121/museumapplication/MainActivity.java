package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;

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
    private LinkedHashSet<String> beacons = new LinkedHashSet<>();
    private boolean scanning;


    protected final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            SparseArray<byte[]> data = mScanRecord.getManufacturerSpecificData();

            //Fetch and decode UUID form byte to hexadecimal
            if (data.size() != 0 && data.valueAt(0) != null) {
                byte[] datavalue = data.valueAt(0);

                if (datavalue != null && datavalue.length == 23 && datavalue[0]==(byte)0x02 && datavalue[22] == (byte)0xc5) {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 2 ; i < 18 ; i++){
                        sb.append(String.format("%02x",datavalue[i]));
                    }
                    beacons.add(sb.toString());
                }
            } else {
                Log.d("MainActivity", mScanRecord.toString());
            }

            //Adds beacon uuid to linkedhashset
            for (String s: beacons){
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
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();


        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                    Log.d("MainActivity", "Premission not granted");
                }
                Log.d("MainActivity", "Granted, börjar scanna");
                setScanSettings();
                scanFilters();
                //bluetoothAdapter.startLeScan(mLeScanCallback);
                bluetoothLeScanner.startScan(scanFilters, mScanSettings, mScanCallback);
                scanning = true;

                //testar lägga in scan period
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
                }

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