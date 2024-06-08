package com.yremhl.ystgdh.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.initToolbar(this , binding.toolbar , true);
    }


}