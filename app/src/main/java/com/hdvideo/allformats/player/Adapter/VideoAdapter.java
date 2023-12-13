package com.hdvideo.allformats.player.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ItemVideoBinding;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Activity activity;
    List<VideoInfo> videosInFolder;
    SharePreferences preferences;

    public VideoAdapter(Activity activity, List<VideoInfo> videosInFolder) {
        this.activity = activity;
        this.videosInFolder = videosInFolder;
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
        holder.binding.size.setText(videosInFolder.get(position).getSizeInMB() + " MB");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<VideoInfo> list = preferences.getVideoDataModelList();
                if (list.size() < 15) {
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putVideoDataModelList(list);
                } else {
                    //TODO PERFORM FIFO
                    int elementsToRemove = list.size() - 14; // Number of elements to remove (15 - 1)
                    list.subList(0, elementsToRemove).clear(); // Removes the oldest elements
                    VideoInfo newDataModel = videosInFolder.get(position);
                    list.add(newDataModel);
                    preferences.putVideoDataModelList(list);
                }
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
                /*List<VideoInfo> list = new ArrayList<>();
                list.add(videosInFolder.get(position));
                preferences.addItemsToPlaylist(playListName,list);*/
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
