package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioInfoList;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainId;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainIsPresentInPlaylist;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainIsVideo;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainNewFileName;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainOldFilePath;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainSize;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoInfoList;
import static com.hdvideo.allformats.player.Extras.Utils.getAudioPlayListName;
import static com.hdvideo.allformats.player.Extras.Utils.getPlayListName;
import static com.hdvideo.allformats.player.Extras.Utils.openMenuDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.hdvideo.allformats.player.Models.AudioPlaylistModel;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.Models.VideoPlaylistModel;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityResultBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";
    ActivityResultBinding binding;
    String path, name;
    int sort_by = 0;
    int type = 0;

    SharePreferences preferences;

    List<VideoInfo> videoInfoList;
    List<AudioInfo> audioInfoList;

    boolean isPresentInPlaylist = false;
    String s = "";

    @Override
    public void onBackPressed() {
        mainAudioInfoList = null;
        mainVideoInfoList = null;
        switch (type) {
            case 0:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 0));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 1:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 1));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 2:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 2));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 3:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 3));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 4:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 4));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 5:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 5));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 6:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 6));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 7:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 7));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 8:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 8));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 9:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 9));
                    overridePendingTransition(0,0);
                    finish();
                break;
            case 10:
                startActivity(new Intent(ResultActivity.this, DashboardActivity.class).putExtra("type", 10));
                break;
            case 11:
            case 12:
                super.onBackPressed();
                break;
        }
    }

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
            setAdapterForVideos(type);
        } else if (type == 1) {
            audioInfoList = Utils.getSongsFromAlbum(this, name);
            setAdapterForAudios(type);
        } else if (type == 2) {
            audioInfoList = Utils.getSongsByArtist(this, name);
            setAdapterForAudios(type);
        } else if (type == 3) {
            isPresentInPlaylist = true;
            videoInfoList = preferences.getVideoListForPlaylist(name);
            setAdapterForVideos(type);
        } else if (type == 4) {
            videoInfoList = preferences.getVideoDataModelList();
            setAdapterForVideos(type);
        } else if (type == 5) {
            videoInfoList = preferences.getFavVideoDataModelList();
            setAdapterForVideos(type);
        } else if (type == 6) {
            isPresentInPlaylist = true;
            audioInfoList = preferences.getAudioListForPlaylist(name);
            setAdapterForAudios(type);
        } else if (type == 7) {
            audioInfoList = preferences.getAudioDataModelList();
            setAdapterForAudios(type);
        } else if (type == 8) {
            audioInfoList = preferences.getFavAudioDataModelList();
            setAdapterForAudios(type);
        } else if (type == 9) {
            binding.title.setText("Search result for \"" + name +"\"");
            videoInfoList = new ArrayList<>();
            for (VideoInfo d : mainVideoInfoList) {
                if (d.getName().toLowerCase() != null && d.getName().contains(name)) {
                    videoInfoList.add(d);
                }
            }
            checkVideoList();
        } else if (type == 10) {
            binding.title.setText("Search result for \"" + name +"\"");
            audioInfoList = new ArrayList<>();
            for (AudioInfo d : mainAudioInfoList) {
                if (d.getName().toLowerCase() != null && d.getName().contains(name)) {
                    audioInfoList.add(d);
                }
            }
            checkAudioList();
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

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setAdapterForAudioPlaylist() {
        binding.resultRv.setLayoutManager(new GridLayoutManager(ResultActivity.this, 2));
        AudioPlayListAdapter adapter = new AudioPlayListAdapter(true, new AudioPlayListAdapter.AudioPlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                preferences.addItemsToAudioPlaylist(playlistName, mainAudioInfoList);
                Toast.makeText(ResultActivity.this, getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        binding.resultRv.setAdapter(adapter);
        adapter.submitList(preferences.getAudioPlaylists());
        if (preferences.getAudioPlaylists().size() > 0) {
            binding.noStatus.setVisibility(View.GONE);
            binding.resultRv.setVisibility(View.VISIBLE);
        } else {
            binding.noStatus.setText(getString(R.string.no_playlists_here));
            binding.resultRv.setVisibility(View.GONE);
            binding.noStatus.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapterForPlaylist() {
        binding.resultRv.setLayoutManager(new GridLayoutManager(ResultActivity.this, 2));

        PlayListAdapter adapter = new PlayListAdapter(true, new PlayListAdapter.PlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                preferences.addItemsToPlaylist(playlistName, mainVideoInfoList);
                Toast.makeText(ResultActivity.this, getString(R.string.added_to_playlist), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        binding.resultRv.setAdapter(adapter);
        adapter.submitList(preferences.getPlaylists());
        if (preferences.getPlaylists().size() > 0) {
            binding.noStatus.setVisibility(View.GONE);
            binding.resultRv.setVisibility(View.VISIBLE);
        } else {
            binding.noStatus.setText(getString(R.string.no_playlists_here));
            binding.resultRv.setVisibility(View.GONE);
            binding.noStatus.setVisibility(View.VISIBLE);
        }
    }

    private void checkAudioList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (audioInfoList.size() != 0) {
                    setAdapterForAudios(type);
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
                    setAdapterForVideos(type);
                } else {
                    checkVideoList();
                }
            }
        }, 500);
    }

    private void setAdapterForAudios(int type) {
        if (this.type == 6) s = binding.title.getText().toString();
        MusicAdapter adapter = new MusicAdapter(ResultActivity.this, audioInfoList, new AppInterfaces.OnMoreListener() {
            @Override
            public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                openMenuDialog(ResultActivity.this, id, name, path, size, false, more, s);
            }
        });
        binding.resultRv.setAdapter(adapter);
        if (audioInfoList.size() > 0) {
            binding.noStatus.setVisibility(View.GONE);
            binding.resultRv.setVisibility(View.VISIBLE);
        } else {
            binding.noStatus.setText(getString(R.string.no_audios_here));
            binding.resultRv.setVisibility(View.GONE);
            binding.noStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                if (Utils.renameAudioFile(mainOldFilePath, mainNewFileName)) {
                    File oldFile = new File(mainOldFilePath);
                    String extension = Utils.getFileExtension(oldFile.getName());
                    if (!extension.isEmpty()) {
                        File parentDir = oldFile.getParentFile();
                        File newFile = new File(parentDir, mainNewFileName + "." + extension);
                        // Playlist
                        if (!mainIsPresentInPlaylist.isEmpty()) {
                            if (mainIsVideo) {
                                preferences.removeItemFromPlaylist(mainIsPresentInPlaylist, mainOldFilePath);
                                mainVideoInfoList = new ArrayList<>();
                                mainVideoInfoList.add(new VideoInfo(mainId, mainNewFileName + "." + extension, Double.parseDouble(mainSize), newFile.getPath()));
                                preferences.addItemsToPlaylist(mainIsPresentInPlaylist, mainVideoInfoList);
                            } else {
                                preferences.removeItemFromAudioPlaylist(mainIsPresentInPlaylist, mainOldFilePath);
                                mainAudioInfoList = new ArrayList<>();
                                mainAudioInfoList.add(new AudioInfo(mainId, newFile.getPath(), mainNewFileName + "." + extension, Double.parseDouble(mainSize)));
                                preferences.addItemsToAudioPlaylist(mainIsPresentInPlaylist, mainAudioInfoList);
                            }
                        } else {
                            if (mainIsVideo) {
                                List<VideoPlaylistModel> videoPlaylistModels = preferences.getPlaylists();
                                for (VideoPlaylistModel playlist : videoPlaylistModels) {
                                    // Get the video list for the current playlist
                                    if (getPlayListName(playlist,mainOldFilePath)) {
                                        preferences.removeItemFromPlaylist(playlist.getPlaylistName(), mainOldFilePath);
                                        mainVideoInfoList = new ArrayList<>();
                                        mainVideoInfoList.add(new VideoInfo(mainId, mainNewFileName + "." + extension, Double.parseDouble(mainSize), newFile.getPath()));
                                        preferences.addItemsToPlaylist(playlist.getPlaylistName(), mainVideoInfoList);
                                    }
                                }
                            } else {
                                List<AudioPlaylistModel> audioPlaylistModels = preferences.getAudioPlaylists();
                                for (AudioPlaylistModel playlist : audioPlaylistModels) {
                                    // Get the video list for the current playlist
                                    if (getAudioPlayListName(playlist,mainOldFilePath)) {
                                        preferences.removeItemFromAudioPlaylist(playlist.getPlaylistName(), mainOldFilePath);
                                        mainAudioInfoList = new ArrayList<>();
                                        mainAudioInfoList.add(new AudioInfo(mainId, newFile.getPath(), mainNewFileName + "." + extension, Double.parseDouble(mainSize)));
                                        preferences.addItemsToAudioPlaylist(playlist.getPlaylistName(), mainAudioInfoList);
                                    }
                                }
                            }
                        }

                        // Recents
                        if (mainIsVideo) {
                            List<VideoInfo> list = preferences.getVideoDataModelList();
                            VideoInfo newDataModel = new VideoInfo(mainId, mainNewFileName + "." + extension, Double.parseDouble(mainSize), newFile.getPath());

// Check if the list already contains a VideoInfo with the same path as newDataModel
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPath().equals(mainOldFilePath)) {
                                    list.remove(i); // Remove the item with the same path
                                    break; // Stop after removing the first occurrence
                                }
                            }
                            list.add(newDataModel);
                            preferences.putVideoDataModelList(list);
                        } else {
                            List<AudioInfo> list = preferences.getAudioDataModelList();
                            AudioInfo newDataModel = new AudioInfo(mainId, newFile.getPath(), mainNewFileName + "." + extension, Double.parseDouble(mainSize));

// Check if the list already contains a VideoInfo with the same path as newDataModel
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPath().equals(mainOldFilePath)) {
                                    list.remove(i); // Remove the item with the same path
                                    break; // Stop after removing the first occurrence
                                }
                            }
                            list.add(newDataModel);
                            preferences.putAudioDataModelList(list);
                        }

                        // Fav
                        if (mainIsVideo) {
                            List<VideoInfo> list = preferences.getFavVideoDataModelList();
                            VideoInfo newDataModel = new VideoInfo(mainId, mainNewFileName + "." + extension, Double.parseDouble(mainSize), newFile.getPath());
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPath().equals(mainOldFilePath)) {
                                    list.remove(i); // Remove the item with the same path
                                    break; // Stop after removing the first occurrence
                                }
                            }
                            list.add(newDataModel);
                            preferences.putFavVideoDataModelList(list);
                        } else {
                            List<AudioInfo> list = preferences.getFavAudioDataModelList();
                            AudioInfo newDataModel = new AudioInfo(mainId, newFile.getPath(), mainNewFileName + "." + extension, Double.parseDouble(mainSize));

// Check if the list already contains a VideoInfo with the same path as newDataModel
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPath().equals(mainOldFilePath)) {
                                    list.remove(i); // Remove the item with the same path
                                    break; // Stop after removing the first occurrence
                                }
                            }
                            list.add(newDataModel);
                            preferences.putFavAudioDataModelList(list);
                        }
                    }
                    recreate();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult:  RESULT_CANCELED");
            }
        } else if (requestCode == 124) {
            if (resultCode == Activity.RESULT_OK) {
                if (Utils.deleteFile(mainOldFilePath)) {
                    //PlayList
                    if (!mainIsPresentInPlaylist.isEmpty()) {
                        if (mainIsVideo) {
                            preferences.removeItemFromPlaylist(mainIsPresentInPlaylist, mainOldFilePath);
                        } else {
                            preferences.removeItemFromAudioPlaylist(mainIsPresentInPlaylist, mainOldFilePath);
                        }
                    } else {
                        if (mainIsVideo) {
                            List<VideoPlaylistModel> videoPlaylistModels = preferences.getPlaylists();
                            for (VideoPlaylistModel playlist : videoPlaylistModels) {
                                // Get the video list for the current playlist
                                if (getPlayListName(playlist, mainOldFilePath)) {
                                    preferences.removeItemFromPlaylist(playlist.getPlaylistName(), mainOldFilePath);
                                }
                            }
                        } else {
                            List<AudioPlaylistModel> audioPlaylistModels = preferences.getAudioPlaylists();
                            for (AudioPlaylistModel playlist : audioPlaylistModels) {
                                // Get the video list for the current playlist
                                if (getAudioPlayListName(playlist, mainOldFilePath)) {
                                    preferences.removeItemFromAudioPlaylist(playlist.getPlaylistName(), mainOldFilePath);
                                }
                            }
                        }
                    }

                    //Recent
                    if (mainIsVideo) {
                        List<VideoInfo> list = preferences.getVideoDataModelList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPath().equals(mainOldFilePath)) {
                                list.remove(i); // Remove the item with the same path
                                break; // Stop after removing the first occurrence
                            }
                        }
                        preferences.putVideoDataModelList(list);
                    } else {
                        List<AudioInfo> list = preferences.getAudioDataModelList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPath().equals(mainOldFilePath)) {
                                list.remove(i); // Remove the item with the same path
                                break; // Stop after removing the first occurrence
                            }
                        }
                        preferences.putAudioDataModelList(list);
                    }

                    // Fav
                    if (mainIsVideo) {
                        List<VideoInfo> list = preferences.getFavVideoDataModelList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPath().equals(mainOldFilePath)) {
                                list.remove(i); // Remove the item with the same path
                                break; // Stop after removing the first occurrence
                            }
                        }
                        preferences.putFavVideoDataModelList(list);
                    } else {
                        List<AudioInfo> list = preferences.getFavAudioDataModelList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPath().equals(mainOldFilePath)) {
                                list.remove(i); // Remove the item with the same path
                                break; // Stop after removing the first occurrence
                            }
                        }
                        preferences.putFavAudioDataModelList(list);
                    }
                    recreate();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult:  RESULT_CANCELED");
            }
        }
    }


    private void setAdapterForVideos(int type) {
        if (this.type == 3) s = binding.title.getText().toString();
        VideoAdapter adapter = new VideoAdapter(ResultActivity.this, videoInfoList, new AppInterfaces.OnMoreListener() {
            @Override
            public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                openMenuDialog(ResultActivity.this, id, name, path, size, true, more, s);
            }
        });
        binding.resultRv.setAdapter(adapter);
        if (videoInfoList.size() > 0) {
            binding.noStatus.setVisibility(View.GONE);
            binding.resultRv.setVisibility(View.VISIBLE);
        } else {
            binding.noStatus.setText(getString(R.string.no_videos_here));
            binding.resultRv.setVisibility(View.GONE);
            binding.noStatus.setVisibility(View.VISIBLE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        mainAudioInfoList = null;
        mainVideoInfoList = null;
    }
}