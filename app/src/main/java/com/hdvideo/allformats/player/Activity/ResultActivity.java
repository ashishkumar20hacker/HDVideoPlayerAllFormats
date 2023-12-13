package com.hdvideo.allformats.player.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.hdvideo.allformats.player.Adapter.MusicAdapter;
import com.hdvideo.allformats.player.Adapter.VideoAdapter;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    String path, name;
    int sort_by = 0;
    int type = 0;

    SharePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = new SharePreferences(this);
        name = getIntent().getStringExtra("name");
        path = getIntent().getStringExtra("path");
        type = getIntent().getIntExtra("type", 0);

        binding.title.setText(name);

        binding.resultRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (type == 0) {
            VideoAdapter adapter = new VideoAdapter(this, Utils.getAllVideosFromFolder(this, path));
            binding.resultRv.setAdapter(adapter);
        } else if (type == 1) {
            MusicAdapter adapter = new MusicAdapter(this, Utils.getSongsFromAlbum(this, name));
            binding.resultRv.setAdapter(adapter);
        } else if (type == 2) {
            MusicAdapter adapter = new MusicAdapter(this, Utils.getSongsByArtist(this, name));
            binding.resultRv.setAdapter(adapter);
        } else if (type == 3) {
            VideoAdapter adapter = new VideoAdapter(this, preferences.getVideoListForPlaylist(name));
            binding.resultRv.setAdapter(adapter);
        } else if (type == 4) {
            VideoAdapter adapter = new VideoAdapter(this, preferences.getVideoDataModelList());
            binding.resultRv.setAdapter(adapter);
        } else if (type == 5) {
            VideoAdapter adapter = new VideoAdapter(this, preferences.getFavVideoDataModelList());
            binding.resultRv.setAdapter(adapter);
        } else if (type == 6) {
            MusicAdapter adapter = new MusicAdapter(this, preferences.getAudioListForPlaylist(name));
            binding.resultRv.setAdapter(adapter);
        } else if (type == 7) {
            MusicAdapter adapter = new MusicAdapter(this, preferences.getAudioDataModelList());
            binding.resultRv.setAdapter(adapter);
        } else if (type == 8) {
            MusicAdapter adapter = new MusicAdapter(this, preferences.getFavAudioDataModelList());
            binding.resultRv.setAdapter(adapter);
        }

        binding.sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog();
            }
        });

    }

    private void sortDialog() {
        Dialog dialog = new Dialog(ResultActivity.this, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(ResultActivity.this);

        View lay = inflater.inflate(R.layout.dialog_sort, null);
        ImageView closeBt = lay.findViewById(R.id.close);
        MaterialCardView nameRb = lay.findViewById(R.id.name_rb);
        MaterialCardView dateRb = lay.findViewById(R.id.date_rb);
        MaterialCardView sizeRb = lay.findViewById(R.id.size_rb);
        ImageView nameRbIv = lay.findViewById(R.id.name_rb_iv);
        ImageView dateRbIv = lay.findViewById(R.id.date_rb_iv);
        ImageView sizeRbIv = lay.findViewById(R.id.size_rb_iv);
        TextView applyBt = lay.findViewById(R.id.apply_bt);

        dialog.setContentView(lay);

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        nameRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                nameRb.setStrokeColor(Utils.setColorFromAttribute(ResultActivity.this, R.attr.light_color, R.color.light_blue));
                nameRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 0;
            }
        });

        dateRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                dateRb.setStrokeColor(Utils.setColorFromAttribute(ResultActivity.this, R.attr.light_color, R.color.light_blue));
                dateRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 1;
            }
        });

        sizeRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRb.setStrokeColor(Utils.setColorFromAttribute(ResultActivity.this, R.attr.light_color, R.color.light_blue));
                sizeRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 2;
            }
        });

        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (sort_by == 0) {
                    //TODO sort by name
                } else if (sort_by == 1) {
                    //TODO sort by date
                } else {
                    //TODO sort by size
                }
            }
        });

        dialog.show();
    }
}