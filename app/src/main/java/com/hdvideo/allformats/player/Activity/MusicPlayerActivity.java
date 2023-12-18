package com.hdvideo.allformats.player.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityMusicPlayerBinding;

public class MusicPlayerActivity extends AppCompatActivity {

    ActivityMusicPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}