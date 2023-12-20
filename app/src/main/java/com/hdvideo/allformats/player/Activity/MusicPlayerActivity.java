package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioPlayerInfoList;
import static com.hdvideo.allformats.player.Extras.Utils.getArtistName;
import static com.hdvideo.allformats.player.Extras.Utils.openMenuDialog;
import static com.hdvideo.allformats.player.Extras.Utils.shareAudioFile;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.hdvideo.allformats.player.Adapter.MoreMusicAdapter;
import com.hdvideo.allformats.player.Adapter.MusicAdapter;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.Service.MusicPlayerService;
import com.hdvideo.allformats.player.databinding.ActivityMusicPlayerBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";

    ActivityMusicPlayerBinding binding;
    private ExoPlayer player;
    private Handler handler;
    boolean isBound = false;
    boolean fromNotification = false;
    int currentMusicPosition = 0;
    SharePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = new SharePreferences(this);

        currentMusicPosition = getIntent().getIntExtra("currentMusicPosition", 0);
        fromNotification = getIntent().getBooleanExtra("fromNotification", false);
        if (fromNotification) {
            Utils.getMusicServiceExoPlayer(this, player -> {
                if (player != null && player.getCurrentMediaItem() != null && player.isPlaying()) {
                    List<MediaItem> mediaItemList = new ArrayList<>();
                    for (int i = 0; i < player.getMediaItemCount(); i++) {
                        Log.e("check", "The media data count : " + i);
                        mediaItemList.add(player.getMediaItemAt(i));
                    }
                }
            });
        }
        doBindService();
        if (!fromNotification)
            startService(new Intent(getApplicationContext(), MusicPlayerService.class));

    }

    private void doBindService() {
        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayerService.ServiceBinder binder = (MusicPlayerService.ServiceBinder) iBinder;
            player = binder.getMusicPlayerService().exoplayer;
            isBound = true;

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
                        if (player.getCurrentMediaItemIndex() < mainAudioPlayerInfoList.size() - 1) {
                            updatePlayerUi(false);
//                            adapter.updateCurrentMusic(player.getCurrentMediaItemIndex());
                        }
                    } else {
                        binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));
                    }
                }
            });

            if (!fromNotification) playMusic(currentMusicPosition);
            updatePlayerUi(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void checkFav() {
        List<AudioInfo> list = preferences.getFavAudioDataModelList();
        AudioInfo newDataModel = mainAudioPlayerInfoList.get(player.getCurrentMediaItemIndex());
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

    private void musicPlayerEventListners() {

        binding.backbt.setOnClickListener(v -> onBackPressed());

        binding.playPause.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));
            } else {
                player.play();
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_mp));
            }
        });

        /*binding.mpOptionsButton.setOnClickListener(v->{
            AlignedDialog popupWindow= Functions.createAlignedDialogBox(this, R.layout.music_player_options_popup, true, Gravity.END | Gravity.TOP);
            if(!isFinishing() && !popupWindow.isShowing()) popupWindow.show();

            LinearLayout setRingtoneButton= popupWindow.findViewById(R.id.mpopSetRingtoneButton);
            LinearLayout deleteButton= popupWindow.findViewById(R.id.mpopDeleteButton);

            setRingtoneButton.setOnClickListener(v1-> {
                if(popupWindow.isShowing()) popupWindow.dismiss();
                if (Settings.System.canWrite(MusicPlayerActivity.this)){
                    Functions.setRingtone(MusicPlayerActivity.this, Uri.fromFile(new File(Objects.requireNonNull(Objects.requireNonNull(
                            Objects.requireNonNull(item).mediaMetadata.extras).getString("musicFilePath")))));
                }
                else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            deleteButton.setOnClickListener(v1->{
                if(popupWindow.isShowing()) popupWindow.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    List<Uri> uriList= new ArrayList<>();
                    Uri uri_one = ContentUris.withAppendedId(MediaStore.Audio.Media.getContentUri("external"),
                            item.mediaMetadata.extras.getLong("musicID"));
                    uriList.add(uri_one);
                    deleteSelectionPosition= player.getCurrentMediaItemIndex();
                    PendingIntent intent= MediaStore.createDeleteRequest(getContentResolver(), uriList);
                    try {
                        startIntentSenderForResult(intent.getIntentSender(), Functions.REQUEST_PERM_DELETE, null, 0, 0, 0, null);
                    } catch (IntentSender.SendIntentException e) {
                        Toast.makeText(this, getString(R.string.unable_to_delete_file_due_to)+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Dialog alert_dialog= Functions.createDialogBox(this, R.layout.alert_dialog, false);
                    TextView alertHeading= alert_dialog.findViewById(R.id.alertHeading);
                    TextView alertContent= alert_dialog.findViewById(R.id.alertContent);
                    Button alertProceedButton= alert_dialog.findViewById(R.id.alertProceedButton);
                    ImageView alertCloseButton= alert_dialog.findViewById(R.id.alertCloseButton);
                    alertCloseButton.setVisibility(View.GONE);

                    File file= new File(Objects.requireNonNull(Objects.requireNonNull(
                            Objects.requireNonNull(item).mediaMetadata.extras).getString("musicFilePath")));
                    if(file.exists() && file.canWrite() && file.delete()){
                        alertHeading.setText(getString(R.string.music_file_delete_success));
                        alertContent.setText(getString(R.string.the_selected_music_file_has_been_deleted_successfully));
                        musicArrayList.remove(player.getCurrentMediaItemIndex());
                        adapter.notifyItemChanged(player.getCurrentMediaItemIndex());
                    }
                    else{
                        alertHeading.setText(getString(R.string.music_deletion_failed));
                        alertContent.setText(getString(R.string.your_selected_music_file_was_not_able_to_delete_please_try_again_and_some_time_later));
                    }

                    if(!isFinishing() && !alert_dialog.isShowing()) alert_dialog.show();
                    alertProceedButton.setOnClickListener(v2->{
                        if(alert_dialog.isShowing()) alert_dialog.dismiss();
                    });
                }
            });
        });*/

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

       /* binding.mpMusicRepeatButton.setOnClickListener(v->{
            player.setShuffleModeEnabled(false);
            binding.mpMusicShuffleButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white80)));
            if(repeat_mode == 0){
                player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
                repeat_mode=1;
                binding.mpMusicRepeatButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat));
                binding.mpMusicRepeatButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            }
            else {
                player.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF);
                repeat_mode=0;
                binding.mpMusicRepeatButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat));
                binding.mpMusicRepeatButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white80)));
            }
        });

        binding.mpMusicShuffleButton.setOnClickListener(v->{
            if(player.getShuffleModeEnabled()){
                player.setShuffleModeEnabled(false);
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
                binding.mpMusicShuffleButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white80)));
            }
            else{
                player.setShuffleModeEnabled(true);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                binding.mpMusicShuffleButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            }
        });*/

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAudioFile(MusicPlayerActivity.this, new File(mainAudioPlayerInfoList.get(player.getCurrentMediaItemIndex()).getPath()));
            }
        });

        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AudioInfo> list = preferences.getFavAudioDataModelList();
                AudioInfo newDataModel = mainAudioPlayerInfoList.get(player.getCurrentMediaItemIndex());
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
                preferences.putFavAudioDataModelList(list);
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
            binding.artistName.setText(getArtistName(MusicPlayerActivity.this, mainAudioPlayerInfoList.get(player.getCurrentMediaItemIndex()).getId()));
            binding.duration.setText(Utils.formatTimeDuration(player.getDuration()));
            binding.seekbar.setProgress((int) player.getCurrentPosition());
            binding.seekbar.setMax((int) player.getDuration());
            checkFav();
            if (player.isPlaying())
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_mp));
            else
                binding.playPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_mp));
            if (check && !player.isPlaying()) {
                player.play();
                setAdapterForMore();
            }

            musicPlayerEventListners();
        }
    }

    private void setAdapterForMore() {
        binding.musicRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<AudioInfo> targetList = new ArrayList<>(); // Your target list to copy to

        int startPos = player.getCurrentMediaItemIndex() + 1; // Starting position (pos + 1)
        int itemsToCopy = 10; // Number of items to copy

// Ensure startPos is within the bounds of the sourceList
        if (startPos >= 0 && startPos < mainAudioPlayerInfoList.size()) {
            // Iterate through the sourceList and copy 10 items starting from startPos + 1 to targetList
            for (int i = startPos; i < mainAudioPlayerInfoList.size() && itemsToCopy > 0; i++) {
                targetList.add(mainAudioPlayerInfoList.get(i));
                itemsToCopy--;
            }
        }
        MoreMusicAdapter adapter = new MoreMusicAdapter(MusicPlayerActivity.this, targetList, player.getCurrentMediaItemIndex(), new AppInterfaces.OnMoreListener() {
            @Override
            public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                openMenuDialog(MusicPlayerActivity.this, id, name, path, size, false, more, "");
            }
        });
        binding.musicRv.setAdapter(adapter);
        if (targetList.size() > 0) {
            binding.moreLikeThis.setVisibility(View.VISIBLE);
        } else {
            binding.moreLikeThis.setVisibility(View.GONE);
        }
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
        for (AudioInfo song : mainAudioPlayerInfoList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.fromFile(new File(song.getPath())))
                    .setMediaMetadata(Utils.getMusicMetaData(song))
                    .build();

            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!player.isPlaying()) {
            player.stop();
            doUnbindService();
            Utils.stopMusicService(MusicPlayerActivity.this);
        }
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(playerServiceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    /*    if (player != null) {
            handler.removeCallbacks(updateSeekBarTask);
            if(player.isPlaying()) player.stop();
            player.release();
            player = null;
        }*/

        doUnbindService();
    }


}