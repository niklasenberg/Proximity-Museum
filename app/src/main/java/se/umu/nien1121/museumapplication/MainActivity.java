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
import android.view.View;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
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

    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            List<ParcelUuid> uuids = mScanRecord.getServiceUuids();
            for (ParcelUuid parcelUuid : uuids) {
                System.out.println(parcelUuid.getUuid());
                Log.d("MainActivity", parcelUuid.getUuid().toString());
            }
            //int mRssi = result.getRssi();
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
                Log.d("Tora", "Börjar scanna");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                }
                bluetoothLeScanner.startScan(mScanCallback);
            }
        });
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }
}