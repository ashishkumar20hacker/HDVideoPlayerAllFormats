package com.hdvideo.allformats.player.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.DataModel;
import com.hdvideo.allformats.player.databinding.ItemStatusBinding;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
    ArrayList<DataModel> mData;
    String folderPath;
    boolean isWApp;
    private Activity activity;

    public StatusAdapter(Activity activity, ArrayList<DataModel> jData, boolean isWApp) {
        this.mData = jData;
        this.activity = activity;
        this.isWApp = isWApp;
        folderPath = Constants.downloadWhatsAppDir.getAbsolutePath();
    }

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStatusBinding binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, int position) {
        final DataModel jpast = this.mData.get(position);
        Glide.with(activity).load(jpast.getFilePath()).into(holder.binding.previewIv);

        holder.binding.downloadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.copyFileInSavedDir(activity, jpast.getFilePath(), isWApp);
                Toast.makeText(activity, "Saved successfully!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemStatusBinding binding;
        public ViewHolder(@NonNull ItemStatusBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
