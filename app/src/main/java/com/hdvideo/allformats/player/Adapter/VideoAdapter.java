package com.hdvideo.allformats.player.Adapter;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoPlayerInfoList;
import static com.hdvideo.allformats.player.Extras.Utils.formatFileSize;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Activity.VideoPlayerActivity;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.databinding.ItemVideoBinding;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Activity activity;
    List<VideoInfo> videosInFolder;
    SharePreferences preferences;
    private AppInterfaces.OnMoreListener moreListener;

    public VideoAdapter(Activity activity, List<VideoInfo> videosInFolder, AppInterfaces.OnMoreListener moreListener) {
        this.activity = activity;
        this.videosInFolder = videosInFolder;
        this.moreListener = moreListener;
        preferences = new SharePreferences(activity);
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        Glide.with(activity).load(videosInFolder.get(position).getPath()).into(holder.binding.previewIv);
        holder.binding.name.setText(videosInFolder.get(position).getName());
        holder.binding.size.setText(formatFileSize(videosInFolder.get(position).getSizeInMB()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* List<VideoInfo> list = preferences.getVideoDataModelList();
                if (list.size() < 15) {
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putVideoDataModelList(list);
                } else {
                    int elementsToRemove = list.size() - 14; // Number of elements to remove (15 - 1)
                    list.subList(0, elementsToRemove).clear(); // Removes the oldest elements
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putVideoDataModelList(list);
                }*/
                List<VideoInfo> list = preferences.getVideoDataModelList();
                VideoInfo newDataModel = videosInFolder.get(position);

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
                preferences.putVideoDataModelList(list);
                mainVideoPlayerInfoList = videosInFolder;
                activity.startActivity(new Intent(activity, VideoPlayerActivity.class).putExtra("pos",position));
              /*  List<VideoInfo> list = preferences.getFavVideoDataModelList();
                if (list.size() < 15) {
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putFavVideoDataModelList(list);
                } else {
                    //TODO PERFORM FIFO
                    int elementsToRemove = list.size() - 14; // Number of elements to remove (15 - 1)
                    list.subList(0, elementsToRemove).clear(); // Removes the oldest elements
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putFavVideoDataModelList(list);
                }*/
            }
        });

        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreListener.onMoreClick(videosInFolder.get(position).getId(), videosInFolder.get(position).getName(),videosInFolder.get(position).getPath(), String.valueOf(videosInFolder.get(position).getSizeInMB()), holder.binding.more);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosInFolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public ViewHolder(@NonNull ItemVideoBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
