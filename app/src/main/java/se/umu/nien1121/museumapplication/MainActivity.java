package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Stops scanning after 10 seconds.
    private static final int SCAN_PERIOD = 10000;

    /*   private boolean scanning;
       private Handler handler = new Handler();
       private BluetoothManager bluetoothManager;
       private BluetoothAdapter bluetoothAdapter;
       private BluetoothLeScanner bluetoothLeScanner;
       private final int REQUEST_ENABLE_BT = 1; */
    private ActivityMainBinding binding;
    private SimpleBleScanner mScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityCompat.requestPermissions(
                this,
                new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.BLUETOOTH_CONNECT,
                        }, 0);

        mScanner = new SimpleBleScanner.Builder()
                .addScanPeriod(SCAN_PERIOD) // 10s in milliseconds
                .build();

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager.class);
            System.out.println("Hit");
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Skickas till inbyggd aktivitetshanterare

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("Tora", "då");
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner(); */


        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScan();
                Log.d("Tora", "Börjar scanna");
            }
        });
    }

    private void startScan() {
        mScanner.startScan(new SimpleScannerCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult scanResult) throws SecurityException {
                BluetoothDevice device = scanResult.getDevice();

               // Log.d("MainActivity", "Hejhopp");
               // Log.d("MainActivity", "Found Device: " + device.getAddress());

                ParcelUuid[] uuids = device.getUuids();

                if (uuids != null){
                    for (ParcelUuid uuid: uuids) {
                        Log.d("MainActivity", "UUID: " + uuid.getUuid().toString());
                    }
                }

                //Log.d("MainActivity", "Found Device: " + device.getAddress());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> scanResults) {
                Log.d("MainActivity", "onBatchScanResults(): "+ Arrays.toString(scanResults.toArray()));
            }

            @Override
            public void onFinish() {
                Log.d("MainActivity", "onFinish()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.d("MainActivity", "onScanFailed() " + errorCode);
            }
        });
    }


/*
    private void scanLeDevice() {
        if (!scanning) {

            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;

                    //Ber om tillåtelse från användaren.
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    System.out.println("Scanning stoppad!");
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }


    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    System.out.println("Hittad!");
                }
            }; */

}