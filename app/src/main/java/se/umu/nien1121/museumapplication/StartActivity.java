package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import se.umu.nien1121.museumapplication.databinding.ActivityStartBinding;
import se.umu.nien1121.museumapplication.utils.ActionBarHelper;

/**
 *
 */
public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("StartActivity", "OnCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBarHelper.setActionBar(this, getResources().getString(R.string.actionbar_homepage));

        binding.scanBtn.setOnClickListener(view -> {
            binding.scanBtn.setEnabled(false);
            binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            Intent resultIntent = new Intent(StartActivity.this, ScanActivity.class);
            startActivity(resultIntent);
        });
    }

    @Override
    protected void onResume() {
        binding.scanBtn.setEnabled(true);
        binding.scanBtn.setBackgroundResource(android.R.drawable.btn_default);
        super.onResume();
    }
}