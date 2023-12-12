package com.hdvideo.allformats.player.Fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.databinding.ItemFoldersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoFoldersAdapter  extends RecyclerView.Adapter<VideoFoldersAdapter.ViewHolder> {
    private List<String> folderPaths;
    private Map<String, Integer> folderVideoCounts;
    private Activity activity;

    public VideoFoldersAdapter(Activity activity, Map<String, Integer> folderVideoCounts) {
        this.folderPaths = new ArrayList<>(folderVideoCounts.keySet());
        this.folderVideoCounts = folderVideoCounts;
        this.activity = activity;
    }


    @NonNull
    @Override
    public VideoFoldersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoldersBinding binding = ItemFoldersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new VideoFoldersAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFoldersAdapter.ViewHolder holder, int position) {
        String folderPath = folderPaths.get(position);
        int videoCount = folderVideoCounts.get(folderPath);
        String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1); // Extract folder name

        holder.binding.folderName.setText(folderName);
        holder.binding.totalVideo.setText(videoCount + " Videos");
    }

    @Override
    public int getItemCount() {
        return folderPaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFoldersBinding binding;
        public ViewHolder(@NonNull ItemFoldersBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
