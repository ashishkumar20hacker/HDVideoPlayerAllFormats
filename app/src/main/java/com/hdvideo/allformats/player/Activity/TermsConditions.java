package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.makeStatusBarTransparent2;
import static com.hdvideo.allformats.player.Extras.Utils.pushEffectCardView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;

public class TermsConditions extends AppCompatActivity {

    ImageView backbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent2(TermsConditions.this);
        setContentView(R.layout.activity_terms_conditions);

        backbt = findViewById(R.id.backbt);

        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushEffectCardView(backbt, new Utils.onPushEffect() {
                    @Override
                    public void onClick() {
                        onBackPressed();
                    }
                }, false);
            }
        });

    }
}