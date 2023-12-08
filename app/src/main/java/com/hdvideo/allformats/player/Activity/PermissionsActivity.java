package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.STORAGE_PERMISSION_REQ_CODE;
import static com.hdvideo.allformats.player.Extras.Utils.isStoragePermissionGranted;
import static com.hdvideo.allformats.player.Extras.Utils.makeStatusBarTransparent2;
import static com.hdvideo.allformats.player.Extras.Utils.pushEffectCardView;
import static com.hdvideo.allformats.player.Extras.Utils.requestStoragePermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityPermissionsBinding;

public class PermissionsActivity extends AppCompatActivity {

    ActivityPermissionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent2(this);
        binding = ActivityPermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isStoragePermissionGranted(this)) {
            binding.switch1.setImageResource(R.drawable.on);
        }

        binding.switchLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStoragePermissionGranted(PermissionsActivity.this)) {
                    requestStoragePermission(PermissionsActivity.this);
                }
            }
        });

        binding.switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStoragePermissionGranted(PermissionsActivity.this)) {
                    requestStoragePermission(PermissionsActivity.this);
                }
            }
        });

        binding.grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushEffectCardView(binding.grantPermission, new Utils.onPushEffect() {
                    @Override
                    public void onClick() {
                        if (!isStoragePermissionGranted(PermissionsActivity.this)) {
                            requestStoragePermission(PermissionsActivity.this);
                        } else {
                            startActivity(new Intent(PermissionsActivity.this, DashboardActivity.class));
                        }
                    }
                }, false);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isStoragePermissionGranted(PermissionsActivity.this)) {
                    binding.switch1.setImageResource(R.drawable.on);
                } else {
                    binding.switch1.setImageResource(R.drawable.off);
                    requestStoragePermission(PermissionsActivity.this);
                }
            }
        }
    }
}