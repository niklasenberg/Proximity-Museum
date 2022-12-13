package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.model.Beacon;
import se.umu.nien1121.museumapplication.model.JsonReader;

public class MainActivity extends AppCompatActivity {

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
    private BeaconAdapter adapter = new BeaconAdapter(new ArrayList<>());

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
                timer.cancel();

                new AsyncTask<Integer, ArrayList<Beacon>, ArrayList<Beacon>>() {
                    @Override
                    protected ArrayList<Beacon> doInBackground(Integer... params) {
                        /*For emulator use
                        beacons.add(new Beacon("C8232AFA1B79451BAD2ABB716704A8BF", 20));
                        beaconIdToNumberOfReads.put("C8232AFA1B79451BAD2ABB716704A8BF", 1);
                        beaconIdToRssiSum.put("C8232AFA1B79451BAD2ABB716704A8BF", 20);*/

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
                        return beacons;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Beacon> beacons) {
                        if (beacons.size() > 0) {
                            /* Detta ska göras efter vi hämtat all info*/
                            binding.artworkRecyclerView.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.scanBtn.setEnabled(true);
                            binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            adapter = new BeaconAdapter(beacons);
                            binding.artworkRecyclerView.setAdapter(adapter);
                            System.out.println(beacons.get(0));
                        }
                    }
                }.execute(1);
            }
        }
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

        binding.artworkRecyclerView.setAdapter(adapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        binding.progressBar.setVisibility(View.INVISIBLE);

        binding.scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Times the scanning
                new TimerClass(SCAN_PERIOD);
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
                binding.artworkRecyclerView.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scanBtn.setEnabled(false);
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        });
    }

    public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

        private Beacon[] beacons;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView title_textView;
            private final TextView author_textView;
            private final ImageView artwork_image;


            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                title_textView = view.findViewById(R.id.artwork_title_textView);
                author_textView = view.findViewById(R.id.artwork_author_textView);
                artwork_image = view.findViewById(R.id.imageview_artwork);
            }
        }

        public BeaconAdapter(ArrayList<Beacon> beacons) {
            Beacon[] array = new Beacon[beacons.size()];
            this.beacons = beacons.toArray(array);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artwork, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)

        /*Binder objekt till dataobjekt*/
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Beacon.Artwork artwork = beacons[position].getArtwork();

            if (artwork != null) {
                viewHolder.title_textView.setText(artwork.getTitle());
                viewHolder.author_textView.setText(artwork.getArtistName());
                DownloadImageTask imageTask = new DownloadImageTask(viewHolder.artwork_image);
                imageTask.execute(artwork.getImage());
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return beacons.length;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                if (urldisplay != null) {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
