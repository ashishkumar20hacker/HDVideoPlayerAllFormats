package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.getArtistName;
import static com.hdvideo.allformats.player.Extras.Utils.shareAudioFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityAudioPlayerBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MPlayerActivity extends AppCompatActivity {

    ActivityAudioPlayerBinding binding;
    List<AudioInfo> audioInfoList;
    int songPosition;
    ExoPlayer player;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        binding = ActivityAudioPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if ("content".equals(getIntent().getData().getScheme())) {
            songPosition = 0;
            audioInfoList = new ArrayList<>();
            audioInfoList.add(getMusicDetails(Objects.requireNonNull(getIntent().getData())));
            player= new ExoPlayer.Builder(getApplicationContext()).build();

            AudioAttributes audioAttributes= new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build();

            player.setAudioAttributes(audioAttributes, true);
            binding.songName.setText(audioInfoList.get(0).getName());
            binding.artistName.setText(getArtistName(MPlayerActivity.this,audioInfoList.get(0).getId()));
            player.addListener(new Player.Listener() {
                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                    Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                    updatePlayerUi(true);
//                    adapter.updateCurrentMusic(player.getCurrentMediaItemIndex());
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == player.STATE_READY) {
                        updatePlayerUi(false);
//                        adapter.updateCurrentMusic(player.getCurrentMediaItemIndex());
                    } else if (playbackState == player.STATE_ENDED) {
                        updatePlayerUi(false);
                    } else {
                        binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));
                    }
                }
            });
            playMusic(songPosition);
            updatePlayerUi(true);
        }

        binding.playPause.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));
            } else {
                player.play();
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_mp));
            }
        });

        binding.previous.setOnClickListener(v -> {
            if (player.hasPreviousMediaItem()) {
                player.seekToPrevious();
                updatePlayerUi(true);
//                adapter.updateCurrentMusic(player.getCurrentMediaItemIndex());
            }
        });

        binding.next.setOnClickListener(v -> {
            if (player.hasNextMediaItem()) {
                player.seekToNext();
                updatePlayerUi(true);
//                adapter.updateCurrentMusic(player.getCurrentMediaItemIndex());
            }
        });
        binding.backbt.setOnClickListener(v -> onBackPressed());
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAudioFile(MPlayerActivity.this, new File(audioInfoList.get(0).getPath()));
            }
        });

        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player.getPlaybackState() == ExoPlayer.STATE_READY) {
                    seekBar.setProgress(progressValue);
                    binding.currentProgress.setText(Utils.formatTimeDuration(progressValue));
                    player.seekTo(progressValue);
                }
            }
        });

    }

    private void playMusic(int position) {
        if (!player.isPlaying()) {
            player.setMediaItems(getMediaItems(), position, 0);
        } else {
            player.pause();
            player.seekTo(position, 0);
        }

        player.prepare();
        player.play();
    }

    private void initializeCustomSeekBar() {
        handler = new Handler();
        handler.post(updateSeekBarTask);
    }

    private final Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateSeekBar() {
        if (player != null) {
            long duration = player.getDuration();
            long position = player.getCurrentPosition();
            int progress = 0;
            if (duration > 0) {
                progress = (int) ((position * 100) / duration);
            }
            binding.seekbar.setMax((int) duration);
            binding.seekbar.setProgress((int) position);
            updateDurationTextView();
        }
    }

    private void updateDurationTextView() {
        if (player != null) {
            binding.duration.setText(Utils.formatTimeDuration(player.getDuration()));
            binding.currentProgress.setText(Utils.formatTimeDuration(player.getCurrentPosition()));
            binding.seekbar.setProgress((int) player.getCurrentPosition());
        }
    }

    @NonNull
    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (AudioInfo song : audioInfoList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.fromFile(new File(song.getPath())))
                    .setMediaMetadata(Utils.getMusicMetaData(song))
                    .build();

            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }
    
    private void updatePlayerUi(boolean check) {
        if (player != null && player.getCurrentMediaItem() != null) {
            initializeCustomSeekBar();
            MediaItem item = player.getCurrentMediaItem();
            Uri artworkUri = Uri.parse("content://media/external/audio/media/" + item.mediaMetadata.extras.getLong("musicID") + "/albumart");

            Glide.with(getApplicationContext()).asBitmap().placeholder(R.drawable.logo).load(artworkUri).addListener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    binding.thumbnailIv.setImageBitmap(resource);
                    return true;
                }
            }).submit();

            binding.songName.setText(item.mediaMetadata.title);
            binding.artistName.setText(getArtistName(MPlayerActivity.this, audioInfoList.get(player.getCurrentMediaItemIndex()).getId()));
            binding.duration.setText(Utils.formatTimeDuration(player.getDuration()));
            binding.seekbar.setProgress((int) player.getCurrentPosition());
            binding.seekbar.setMax((int) player.getDuration());
            if (check && !player.isPlaying()) {
                player.play();
            }
            if (player.isPlaying())
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_mp));
            else
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));

        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (player != null) {
            if (!player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class).putExtra("type",10));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (!player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    private AudioInfo getMusicDetails(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID};
            cursor = getContentResolver().query(contentUri, projection, null, null, null);
            int nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

            cursor.moveToFirst();
            String name = cursor.getString(nameColumnIndex);
            String path = cursor.getString(dataColumnIndex);
            long duration = cursor.getLong(durationColumnIndex);

            return new AudioInfo(duration, path != null ? path : "Unknown", name, 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}