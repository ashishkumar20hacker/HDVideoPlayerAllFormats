package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.gotoUrl;
import static com.hdvideo.allformats.player.Extras.Utils.makeStatusBarTransparent2;
import static com.hdvideo.allformats.player.Extras.Utils.nextActivity;
import static com.hdvideo.allformats.player.Extras.Utils.pushEffectCardView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityTermsOfUseBinding;

public class TermsOfUseActivity extends AppCompatActivity {

    ActivityTermsOfUseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent2(TermsOfUseActivity.this);
        binding = ActivityTermsOfUseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.agreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushEffectCardView(binding.agreebtn, new Utils.onPushEffect() {
                    @Override
                    public void onClick() {
                        nextActivity(TermsOfUseActivity.this, PermissionsActivity.class);
                    }
                }, false);
            }
        });
    }

    public void pp(View view) {
        gotoUrl(TermsOfUseActivity.this);
    }

    public void tc(View view) {
        nextActivity(TermsOfUseActivity.this, TermsConditions.class, "isDb", false);
    }
}