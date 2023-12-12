package com.hdvideo.allformats.player.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.databinding.ItemMusicBinding;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    Activity activity;
    List<AudioInfo> videosInFolder;
    public MusicAdapter(Activity activity, List<AudioInfo> videosInFolder) {
        this.activity = activity;
        this.videosInFolder = videosInFolder;
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
        holder.binding.size.setText(videosInFolder.get(position).getSizeInMB() + " KB");
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

