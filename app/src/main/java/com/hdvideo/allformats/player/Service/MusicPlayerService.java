package com.hdvideo.allformats.player.Service;

import static com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_HIGH;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.R;

import java.util.Objects;

public class MusicPlayerService extends Service {

    private final IBinder serviceBinder= new ServiceBinder();
    public ExoPlayer exoplayer;
    int selectedMusicPosition= 0;
    public PlayerNotificationManager notificationManager;

    public class ServiceBinder extends Binder {
        public MusicPlayerService getMusicPlayerService(){
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exoplayer= new ExoPlayer.Builder(getApplicationContext()).build();

        AudioAttributes audioAttributes= new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();

        exoplayer.setAudioAttributes(audioAttributes, true);

        final String channelId= getResources().getString(R.string.app_name)+ " Music Channel ";
        notificationManager= new PlayerNotificationManager.Builder(this, Constants.MUSIC_NOTIFICATION_ID, channelId)
                .setMediaDescriptionAdapter(descriptionAdapter)
                .setNotificationListener(notificationListener)
                .setChannelImportance(IMPORTANCE_HIGH)
                .setSmallIconResourceId(R.drawable.logo)
                .setChannelDescriptionResourceId(R.string.app_name)
                .setPlayActionIconResourceId(R.drawable.play_mp)
                .setPauseActionIconResourceId(R.drawable.pause_mp)
                .setPreviousActionIconResourceId(R.drawable.previous)
                .setNextActionIconResourceId(R.drawable.next_mp)
                .setChannelNameResourceId(R.string.app_name)
                .build();

        notificationManager.setPlayer(exoplayer);
        notificationManager.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.setUseRewindAction(false);
        notificationManager.setUseFastForwardAction(false);
    }

    @Override
    public void onDestroy() {
        if(exoplayer.isPlaying()) exoplayer.stop();
        notificationManager.setPlayer(null);
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    public void stopMusicNotification(){
        if(exoplayer.isPlaying()) exoplayer.stop();
        stopForeground(true);
        Intent intent= new Intent(getApplicationContext(), MusicPlayerService.class);
        stopService(intent);
        exoplayer.clearMediaItems();
        startService(intent);
    }

    PlayerNotificationManager.NotificationListener notificationListener= new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
            stopForeground(true);
            if(exoplayer.isPlaying()) exoplayer.pause();

        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId, notification);
        }
    };

    PlayerNotificationManager.MediaDescriptionAdapter descriptionAdapter= new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            if(player!=null && player.getCurrentMediaItem()!=null && player.getCurrentMediaItem().mediaMetadata.title!=null){
                return player.getCurrentMediaItem().mediaMetadata.title;
            }
            return "Song Name";
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return Utils.createNotificationPendingIntent(getApplicationContext(), exoplayer);
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return null;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            BitmapDrawable bitmapDrawable= (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo);
            Bitmap bitmap= Objects.requireNonNull(bitmapDrawable).getBitmap();
            if(player != null && player.getCurrentMediaItem() != null) {
                ImageView imageView= new ImageView(getApplicationContext());
                imageView.setImageURI(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri);
                bitmapDrawable= (BitmapDrawable) imageView.getDrawable();
                if(bitmapDrawable!=null){
                    bitmap= bitmapDrawable.getBitmap();
                }
                return bitmap;
            }
            else stopMusicNotification();
            return bitmap;
        }
    };
}