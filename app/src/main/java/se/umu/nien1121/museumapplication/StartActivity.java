package se.umu.nien1121.museumapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import se.umu.nien1121.museumapplication.databinding.ActivityMainBinding;
import se.umu.nien1121.museumapplication.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scanBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                binding.scanBtn.setEnabled(false);
                binding.scanBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                Intent resultIntent = new Intent(StartActivity.this,MainActivity.class);
                startActivity(resultIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        binding.scanBtn.setEnabled(true);
        binding.scanBtn.setBackgroundResource(android.R.drawable.btn_default);
        super.onResume();
    }
}