package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioInfoList;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoInfoList;
import static com.hdvideo.allformats.player.Extras.Utils.openMenuDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.hdvideo.allformats.player.Adapter.AudioPlayListAdapter;
import com.hdvideo.allformats.player.Adapter.MusicAdapter;
import com.hdvideo.allformats.player.Adapter.PlayListAdapter;
import com.hdvideo.allformats.player.Adapter.VideoAdapter;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityResultBinding;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    String path, name;
    int sort_by = 0;
    int type = 0;

    SharePreferences preferences;

    List<VideoInfo> videoInfoList;
    List<AudioInfo> audioInfoList;

    boolean isPresentInPlaylist = false;

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
            videoInfoList = Utils.getAllVideosFromFolder(this, path);
            setAdapterForVideos();
        } else if (type == 1) {
            audioInfoList = Utils.getSongsFromAlbum(this, name);
            setAdapterForAudios();
        } else if (type == 2) {
            audioInfoList = Utils.getSongsByArtist(this, name);
            setAdapterForAudios();
        } else if (type == 3) {
            isPresentInPlaylist = true;
            videoInfoList = preferences.getVideoListForPlaylist(name);
            setAdapterForVideos();
        } else if (type == 4) {
            videoInfoList = preferences.getVideoDataModelList();
            setAdapterForVideos();
        } else if (type == 5) {
            videoInfoList = preferences.getFavVideoDataModelList();
            setAdapterForVideos();
        } else if (type == 6) {
            isPresentInPlaylist = true;
            audioInfoList = preferences.getAudioListForPlaylist(name);
            setAdapterForAudios();
        } else if (type == 7) {
            audioInfoList = preferences.getAudioDataModelList();
            setAdapterForAudios();
        } else if (type == 8) {
            audioInfoList = preferences.getFavAudioDataModelList();
            setAdapterForAudios();
        } else if (type == 9) {
//            AppAsyncTask.AllVideos allVideos = new AppAsyncTask.AllVideos(ResultActivity.this, new AppInterfaces.AllVideosListener() {
//                @Override
//                public void getAllVideos(List<VideoInfo> allVideoList) {
            videoInfoList = new ArrayList<>();
            for (VideoInfo d : mainVideoInfoList) {
                if (d.getName().toLowerCase() != null && d.getName().contains(name)) {
                    videoInfoList.add(d);
                }
            }
            checkVideoList();
//                }
//            });
//            allVideos.execute();
        } else if (type == 10) {
//            AppAsyncTask.AllSongs allSongs = new AppAsyncTask.AllSongs(ResultActivity.this, new AppInterfaces.AllAudiosListener() {
//                @Override
//                public void getAllAudios(List<AudioInfo> allAudioList) {
            audioInfoList = new ArrayList<>();
            for (AudioInfo d : mainAudioInfoList) {
                if (d.getName().toLowerCase() != null && d.getName().contains(name)) {
                    audioInfoList.add(d);
                }
            }
            checkAudioList();
//                }
//            });
//            allSongs.execute();
        } else if (type == 11) {
            setAdapterForPlaylist();
        } else if (type == 12) {
            setAdapterForAudioPlaylist();
        }

        binding.sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog();
            }
        });

    }

    private void setAdapterForAudioPlaylist() {
        binding.resultRv.setLayoutManager(new GridLayoutManager(ResultActivity.this,2));
        AudioPlayListAdapter adapter = new AudioPlayListAdapter(true,new AudioPlayListAdapter.AudioPlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                preferences.addItemsToAudioPlaylist(playlistName,mainAudioInfoList);
                Toast.makeText(ResultActivity.this, getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        binding.resultRv.setAdapter(adapter);
        adapter.submitList(preferences.getAudioPlaylists());
    }

    private void setAdapterForPlaylist() {
        binding.resultRv.setLayoutManager(new GridLayoutManager(ResultActivity.this,2));

        PlayListAdapter adapter = new PlayListAdapter(true,new PlayListAdapter.PlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                preferences.addItemsToPlaylist(playlistName,mainVideoInfoList);
                Toast.makeText(ResultActivity.this, getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        binding.resultRv.setAdapter(adapter);
        adapter.submitList(preferences.getPlaylists());
    }

    private void checkAudioList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (audioInfoList.size() != 0) {
                    setAdapterForAudios();
                } else {
                    checkAudioList();
                }
            }
        }, 500);
    }

    private void checkVideoList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoInfoList.size() != 0) {
                    setAdapterForVideos();
                } else {
                    checkVideoList();
                }
            }
        }, 500);
    }

    private void setAdapterForAudios() {
        MusicAdapter adapter = new MusicAdapter(ResultActivity.this, audioInfoList, new AppInterfaces.OnMoreListener() {
            @Override
            public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                openMenuDialog(ResultActivity.this,id, name, path, size, false, more, binding.title.getText().toString());
            }
        });
        binding.resultRv.setAdapter(adapter);
    }

    private void setAdapterForVideos() {
        VideoAdapter adapter = new VideoAdapter(ResultActivity.this, videoInfoList, new AppInterfaces.OnMoreListener() {
            @Override
            public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                openMenuDialog(ResultActivity.this,id, name, path, size, true, more, binding.title.getText().toString());
            }
        });
        binding.resultRv.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainAudioInfoList = null;
        mainVideoInfoList = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainAudioInfoList = null;
        mainVideoInfoList = null;
    }
}