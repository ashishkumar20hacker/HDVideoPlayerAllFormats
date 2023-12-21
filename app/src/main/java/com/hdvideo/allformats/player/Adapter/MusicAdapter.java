package com.hdvideo.allformats.player.Adapter;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioPlayerInfoList;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainPos;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoPlayerInfoList;
import static com.hdvideo.allformats.player.Extras.Constants.SELECTED_MUSIC_POSITION;
import static com.hdvideo.allformats.player.Extras.Utils.formatFileSize;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Activity.MusicPlayerActivity;
import com.hdvideo.allformats.player.Activity.VideoPlayerActivity;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.databinding.ItemMusicBinding;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    Activity activity;
    List<AudioInfo> videosInFolder;
    SharePreferences preferences;
    private AppInterfaces.OnMoreListener moreListener;
    public MusicAdapter(Activity activity, List<AudioInfo> videosInFolder, AppInterfaces.OnMoreListener moreListener) {
        this.activity = activity;
        this.videosInFolder = videosInFolder;
        this.moreListener = moreListener;
        preferences = new SharePreferences(activity);
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MusicAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, int position) {
        holder.binding.name.setText(videosInFolder.get(position).getName());
        holder.binding.size.setText(formatFileSize(videosInFolder.get(position).getSizeInMB()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    List<AudioInfo> list = preferences.getAudioDataModelList();
                if (list.size() < 15) {
                    AudioInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putAudioDataModelList(list);
                } else {
                    //TODO PERFORM FIFO
                    int elementsToRemove = list.size() - 14; // Number of elements to remove (15 - 1)
                    list.subList(0, elementsToRemove).clear(); // Removes the oldest elements
                    AudioInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putAudioDataModelList(list);
                }*/
                List<AudioInfo> list = preferences.getAudioDataModelList();
                AudioInfo newDataModel = videosInFolder.get(position);

// Check if the list already contains a VideoInfo with the same path as newDataModel
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPath().equals(newDataModel.getPath())) {
                        list.remove(i); // Remove the item with the same path
                        break; // Stop after removing the first occurrence
                    }
                }

// Add the new instance
                list.add(newDataModel);

// Ensure the list doesn't exceed the maximum size (15 in this case)
                if (list.size() > 15) {
                    list.subList(0, list.size() - 15).clear(); // Remove oldest elements if exceeding size
                }

// Update the preferences with the modified list
                preferences.putAudioDataModelList(list);
                mainAudioPlayerInfoList = videosInFolder;
                preferences.putInt(SELECTED_MUSIC_POSITION,holder.getAbsoluteAdapterPosition());
                Utils.stopMusicService(activity);
                activity.startActivity(new Intent(activity, MusicPlayerActivity.class).putExtra("currentMusicPosition",position).putExtra("fromNotification",false).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
               /* List<AudioInfo> list = preferences.getFavAudioDataModelList();
                if (list.size() < 15) {
                    AudioInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putFavAudioDataModelList(list);
                } else {
                    //TODO PERFORM FIFO
                    int elementsToRemove = list.size() - 14; // Number of elements to remove (15 - 1)
                    list.subList(0, elementsToRemove).clear(); // Removes the oldest elements
                    AudioInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putFavAudioDataModelList(list);
                }*/
            }
        });

        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPos = position;
                mainAudioPlayerInfoList = videosInFolder;
                moreListener.onMoreClick(videosInFolder.get(position).getId(),videosInFolder.get(position).getName(),videosInFolder.get(position).getPath(), String.valueOf(videosInFolder.get(position).getSizeInMB()),holder.binding.more);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosInFolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMusicBinding binding;
        public ViewHolder(@NonNull ItemMusicBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}

