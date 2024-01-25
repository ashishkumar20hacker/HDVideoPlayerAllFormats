package com.hdvideo.allformats.player.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adsmodule.api.adsModule.enums.NativeAdType;
import com.hdvideo.allformats.player.databinding.ItemFoldersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoFoldersAdapter  extends RecyclerView.Adapter<VideoFoldersAdapter.ViewHolder> {
    private List<String> folderPaths;
    private Map<String, Integer> folderVideoCounts;
    private Activity activity;
    private OnFolderClickListener onFolderClickListener;

    public VideoFoldersAdapter(Activity activity, Map<String, Integer> folderVideoCounts, OnFolderClickListener onFolderClickListener) {
        this.folderPaths = new ArrayList<>(folderVideoCounts.keySet());
        this.folderVideoCounts = folderVideoCounts;
        this.activity = activity;
        this.onFolderClickListener = onFolderClickListener;
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
        String val = String.valueOf(position);
        if (val.endsWith("4") || val.endsWith("9")) {
            Log.e("onBindViewHolder: ", position + "   " + val + "   "+val.endsWith("4")+ "  "+val.endsWith("9"));
            holder.binding.nativeads.setVisibility(View.VISIBLE);
            holder.binding.nativeads.checkAndLoadAd(true, activity, NativeAdType.MEDIUM, null);
        } else {
            holder.binding.nativeads.setVisibility(View.GONE);
            holder.binding.nativeads.checkAndLoadAd(false, activity, NativeAdType.MEDIUM, null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFolderClickListener.onFolderClick(folderName,folderPath);
            }
        });

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

    public interface OnFolderClickListener{
        void onFolderClick(String folderName, String path);
    }
}
