package com.hdvideo.allformats.player.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.databinding.ItemVideoBinding;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Activity activity;
    List<VideoInfo> videosInFolder;
    public VideoAdapter(Activity activity, List<VideoInfo> videosInFolder) {
        this.activity = activity;
        this.videosInFolder = videosInFolder;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        Glide.with(activity).load(videosInFolder.get(position).getPath()).into(holder.binding.previewIv);
        holder.binding.name.setText(videosInFolder.get(position).getName());
        holder.binding.size.setText(videosInFolder.get(position).getSizeInMB() + " MB");
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
