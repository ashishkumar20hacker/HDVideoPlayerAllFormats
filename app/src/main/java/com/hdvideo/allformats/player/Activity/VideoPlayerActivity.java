package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioPlayerInfoList;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoPlayerInfoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityVideoPlayerBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoPlayerActivity extends AppCompatActivity {

    ActivityVideoPlayerBinding binding;
    private AudioManager audioManager;
    float brightnessValue = 0;
    ExoPlayer exoPlayer;

    int currentVideoPosition = 0;
    private boolean isFullScreen = false;
    private boolean lockOrientationToLandscape = false;
    private Handler handler;
    private boolean mVisible;
    boolean isSubtitlesOn = true;
    DefaultTrackSelector defaultTrackSelector;
    SharePreferences preferences;

    private String getPathFromURI(Context context, Uri uri) {
        String filePath = "";

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    if (index != -1) {
                        filePath = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = new SharePreferences(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        binding.volumeSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        binding.volumeSeek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        currentVideoPosition = getIntent().getIntExtra("pos", 0);
        defaultTrackSelector = new DefaultTrackSelector(this);
        exoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(defaultTrackSelector).build();

        mVisible = true;
//        brightnessValue = getCurrentScreenBrightness();
//        setBrightness(brightnessValue);
        setBrightness(125);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.brightnessSeek.setMin(1);
            binding.brightnessSeek.setMax(255);
        }

        try {
            // For handling video file intent (Improved Version)
            if (getIntent().getData() != null && "content".equals(getIntent().getData().getScheme())) {
                mainVideoPlayerInfoList = new ArrayList<>();
                currentVideoPosition = 0;

                Cursor cursor = getContentResolver().query(Objects.requireNonNull(getIntent().getData()), new String[]{MediaStore.Video.Media.DATA}, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    try {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        File file = new File(path);
                        VideoInfo video = new VideoInfo(0, file.getName(), 0, path/*, path*/);
                        mainVideoPlayerInfoList.add(video);
                        cursor.close();
                    } catch (Exception e) {
                        try {
                            String tempPath = getPathFromURI(this, getIntent().getData());
                            File tempFile = new File(tempPath);
                            VideoInfo video = new VideoInfo(0, tempFile.getName(), 0L, tempFile.getPath());
                            mainVideoPlayerInfoList.add(video);
                            cursor.close();
                        } catch (Exception ex) {
                            Log.e("TAG", "onCreate2: " + ex.getLocalizedMessage());
                        }
                    }
                    binding.fav.setVisibility(View.GONE);
                }
            } else {
                //do nothing
            }
        } catch (Exception e) {
            Log.e("TAG", "onCreate: " + e.getLocalizedMessage());
        }


        if (mainVideoPlayerInfoList != null && mainVideoPlayerInfoList.size() > 0) {
            binding.vpVideoPlayerView.setPlayer(exoPlayer);
            loadPlayVideo(currentVideoPosition);
            binding.videoName.setText(mainVideoPlayerInfoList.get(currentVideoPosition).getName());
        }

        binding.vpVideoPlayerView.setOnClickListener(v -> {
            if (isFullScreen) hide();
            if (binding.controls.getVisibility() == View.VISIBLE) {
//                Utils.loadFadeAnimationToView(binding.controls, View.GONE, 1f, 0f);
                hideControls();
            } else {
                Utils.loadFadeAnimationToView(binding.controls, View.VISIBLE, 0f, 1f);
//                binding.backbt.setVisibility(View.VISIBLE);
            }
        });

        initializeCustomSeekBar();

        videoPlayerEventListeners();

        hideControls();
    }

    private void videoPlayerEventListeners() {
        binding.backbt.setOnClickListener(v -> onBackPressed());

        binding.controls.setOnClickListener(v -> {
            if (binding.controls.getVisibility() == View.VISIBLE) {
//                Utils.loadFadeAnimationToView(binding.controls, View.GONE, 1f, 0f);
                hideControls();
            } else {
                Utils.loadFadeAnimationToView(binding.controls, View.VISIBLE, 0f, 1f);
            }
        });

        /*binding.vpVideoRotateButton.setOnClickListener(v->{
            if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });*/

       /* binding.speed.setOnClickListener(v->{
            if(binding.vpVideoAudioSpeedDialog.getVisibility() == View.VISIBLE){
                binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            }
            else{
                binding.vpVideoAudioSpeedDialog.setVisibility(View.VISIBLE);
            }
        });

        binding.vpsdZeroTwoFiveSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(0.25f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(0);
        });

        binding.vpsdZeroFiveSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(0.5f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(1);
        });

        binding.vpsdNormalSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(1f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(2);
        });

        binding.vpsdOneTwoFiveSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(1.25f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(3);
        });

        binding.vpsdOneFiveSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(1.5f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(4);
        });

        binding.vpsdDoubleSpeed.setOnClickListener(v->{
            PlaybackParameters param = new PlaybackParameters(2f);
            exoPlayer.setPlaybackParameters(param);
            binding.vpVideoAudioSpeedDialog.setVisibility(View.GONE);
            updateAudioSpeedLays(5);
        });*/

        binding.cc.setOnClickListener(v -> {
            if (isSubtitlesOn) {
                isSubtitlesOn = false;
                defaultTrackSelector.setParameters(new DefaultTrackSelector.Parameters.Builder(this).setRendererDisabled(C.TRACK_TYPE_VIDEO, true).build());
//                Snackbar.make(binding.getRoot(), getString(R.string.subtitle_off), Snackbar.LENGTH_SHORT).show();
                binding.cc.setImageResource(R.drawable.cc_off);
            } else {
                isSubtitlesOn = true;
                defaultTrackSelector.setParameters(new DefaultTrackSelector.Parameters.Builder(this).setRendererDisabled(C.TRACK_TYPE_VIDEO, false).build());
//                Snackbar.make(binding.getRoot(), getString(R.string.subtitle_on), Snackbar.LENGTH_SHORT).show();
                binding.cc.setImageResource(R.drawable.cc_on);

            }
        });

        binding.vpVideoPlayerView.setOnClickListener(v -> {
            if (isFullScreen) hide();
            if (binding.controls.getVisibility() == View.VISIBLE) {
//                Utils.loadFadeAnimationToView(binding.controls, View.GONE, 1f, 0f);
                hideControls();
            } else {
                Utils.loadFadeAnimationToView(binding.controls, View.VISIBLE, 0f, 1f);
//                binding.backbt.setVisibility(View.VISIBLE);
            }
        });

        binding.playPause.setOnClickListener(v -> togglePlayPause());
        binding.forwardTen.setOnClickListener(v -> seekForward());
        binding.replayTen.setOnClickListener(v -> seekBackward());
        binding.fullScreen.setOnClickListener(v -> {
            toggle();
            toggleFullScreen();
        });

        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<VideoInfo> list = preferences.getFavVideoDataModelList();
                VideoInfo newDataModel = mainVideoPlayerInfoList.get(currentVideoPosition);
                boolean found = false;

// Check if the list already contains a VideoInfo with the same path as newDataModel
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPath().equals(newDataModel.getPath())) {
                        list.remove(i);
                        found = true;
// Remove the item with the same path
                        break; // Stop after removing the first occurrence
                    }
                }
// Add the new instance if it wasn't found in the list
                if (!found) {
                    list.add(newDataModel);
                    binding.fav.setImageResource(R.drawable.fav);
                } else {
                    binding.fav.setImageResource(R.drawable.unfav);
                }

/*// Ensure the list doesn't exceed the maximum size (15 in this case)
                if (list.size() > 15) {
                    list.subList(0, list.size() - 15).clear(); // Remove oldest elements if exceeding size
                }*/

// Update the preferences with the modified list
                preferences.putFavVideoDataModelList(list);
            }
        });

        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long newPosition = (progress * exoPlayer.getDuration()) / 100;
                    exoPlayer.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        binding.controls.setVisibility(View.GONE);
        mVisible = false;
    }

    private void show() {
        mVisible = true;
        binding.controls.setVisibility(View.VISIBLE);
    }


    private void toggleFullScreen() {
        if (isFullScreen) {
            exitFullScreen();
        } else {
            enterFullScreen();
        }
    }

    private void enterFullScreen() {
        hide();
        setBrightness(125);
        binding.fullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_exit_fullscreen));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.vpVideoPlayerLay.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        binding.vpVideoPlayerLay.setLayoutParams(params);
        binding.controls.setVisibility(View.VISIBLE);
        lockOrientationToLandscape = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isFullScreen = true;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void exitFullScreen() {
        show();
        resetBrightness();
        binding.fullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_enter_fullscreen));
        binding.controls.setVisibility(View.VISIBLE);
        lockOrientationToLandscape = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isFullScreen = false;
    }

    private void resetBrightness() {
        float brightness = -1;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        getWindow().setAttributes(layoutParams);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!lockOrientationToLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void checkFav() {
        List<VideoInfo> list = preferences.getFavVideoDataModelList();
        VideoInfo newDataModel = mainVideoPlayerInfoList.get(currentVideoPosition);
        boolean found = false;

// Check if the list already contains a VideoInfo with the same path as newDataModel
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPath().equals(newDataModel.getPath())) {
                found = true;
// Remove the item with the same path
                break; // Stop after removing the first occurrence
            }
        }
// Add the new instance if it wasn't found in the list
        if (found) {
            binding.fav.setImageResource(R.drawable.fav);
        } else {
            binding.fav.setImageResource(R.drawable.unfav);
        }

        list = null;
    }

    private void loadPlayVideo(int position) {
        Uri uri = Uri.fromFile(new File(mainVideoPlayerInfoList.get(position).getPath()));
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
        checkFav();
//        adjustPlayerSize();

        binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_vp));
        exoPlayer.addListener(new Player.Listener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_vp));
                    if (position < mainVideoPlayerInfoList.size() - 1) {
                        currentVideoPosition = position + 1;
                        if (exoPlayer != null) {
                            if (exoPlayer.isPlaying()) {
                                exoPlayer.stop();
                            }
                            exoPlayer.release();
                            exoPlayer = null;
                        }
                        exoPlayer = new SimpleExoPlayer.Builder(VideoPlayerActivity.this).setTrackSelector(defaultTrackSelector).build();
                        binding.vpVideoPlayerView.setPlayer(exoPlayer);
                        binding.videoName.setText(mainVideoPlayerInfoList.get(currentVideoPosition).getName());
                        loadPlayVideo(currentVideoPosition);
                    } else binding.playPause.setImageResource(R.drawable.play_vp);
//                        Snackbar.make(binding.getRoot(), getString(R.string.no_more_videos), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
                updateDurationTextView();
            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
                updateDurationTextView();
            }
        });
    }

    private void togglePlayPause() {
        if (exoPlayer.isPlaying()) {
            binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_vp));
            exoPlayer.setPlayWhenReady(false);
        } else {
            binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_vp));
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void seekForward() {
        long currentPosition = exoPlayer.getCurrentPosition();
        long newPosition = currentPosition + 10000; // 10 seconds forward
        if (newPosition > exoPlayer.getDuration()) {
            newPosition = exoPlayer.getDuration();
        }
        exoPlayer.seekTo(newPosition);
    }

    private void seekBackward() {
        long currentPosition = exoPlayer.getCurrentPosition();
        long newPosition = currentPosition - 10000; // 10 seconds backward
        if (newPosition < 0) {
            newPosition = 0;
        }
        exoPlayer.seekTo(newPosition);
    }

    private void initializeCustomSeekBar() {
        handler = new Handler();
        handler.post(updateSeekBarTask);
    }

    private final Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            handler.postDelayed(this, 10);
        }
    };

    private void updateSeekBar() {
        if (exoPlayer != null) {
            long duration = exoPlayer.getDuration();
            long position = exoPlayer.getCurrentPosition();
            int progress = 0;
            if (duration > 0) {
                progress = (int) ((position * 100) / duration);
            }
            binding.seekbar.setProgress(progress);
            updateDurationTextView();
        }
    }

    private void updateDurationTextView() {
        if (exoPlayer != null) {
            long currentPosition = exoPlayer.getCurrentPosition();
            long duration = exoPlayer.getDuration();
            String formattedTime = Utils.formatTime(currentPosition) + " / " + Utils.formatTime(duration);
            binding.progressTv.setText(formattedTime);
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void adjustPlayerSize() {
        if (exoPlayer != null) {
            Log.e("check", "On Video Sized Changed Called");
            int videoWidth = exoPlayer.getVideoFormat() != null ? exoPlayer.getVideoFormat().width : 0;
            int videoHeight = exoPlayer.getVideoFormat() != null ? exoPlayer.getVideoFormat().height : 1;
            boolean isPortrait = videoHeight > videoWidth;
            if (isFullScreen) {
                if (isPortrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        }
    }

    public static int getCurrentScreenBrightness() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        float brightness = layoutParams.screenBrightness;
        return (int) (brightness * 255);
    }

    private void setBrightness(float brightness) {
        binding.brightnessSeek.setProgress((int) brightness);
        brightness = brightness / 255.0f;
        brightnessValue = brightness;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
        mainVideoPlayerInfoList = null;
    }

    private void hideControls() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.loadFadeAnimationToView(binding.controls, View.GONE, 1f, 0f);
            }
        }, 2000);
    }
}