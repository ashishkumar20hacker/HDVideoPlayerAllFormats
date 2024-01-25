package com.hdvideo.allformats.player.Adapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adsmodule.api.adsModule.enums.NativeAdType;
import com.adsmodule.api.adsModule.views.NativeRecyclerView;
import com.hdvideo.allformats.player.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AudioAlbumsAdapter extends RecyclerView.Adapter<AudioAlbumsAdapter.ViewHolder> {

    private List<String> albumNames;
    private Map<String, Integer> albumSongsCount;
    private LayoutInflater inflater;
    private OnAlbumClickListener onAlbumClickListener;
    private Activity activity;

    public AudioAlbumsAdapter(Activity context, Map<String, Integer> albumSongsCount, OnAlbumClickListener onAlbumClickListener) {
        this.albumNames = new ArrayList<>(albumSongsCount.keySet());
        this.activity = context;
        this.albumSongsCount = albumSongsCount;
        this.onAlbumClickListener = onAlbumClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String albumName = albumNames.get(position);
        int songCount = albumSongsCount.get(albumName);

        holder.albumNameTextView.setText(albumName);
        holder.songCountTextView.setText(songCount + " Songs");

        String val = String.valueOf(position);
        if (val.endsWith("4") || val.endsWith("9")) {
            Log.e("onBindViewHolder: ", position + "   " + val + "   "+val.endsWith("4")+ "  "+val.endsWith("9"));
            holder.nativeads.setVisibility(View.VISIBLE);
            holder.nativeads.checkAndLoadAd(true, activity, NativeAdType.MEDIUM, null);
        } else {
            holder.nativeads.setVisibility(View.GONE);
            holder.nativeads.checkAndLoadAd(false, activity, NativeAdType.MEDIUM, null);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlbumClickListener.onAlbumClick(albumName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameTextView;
        TextView songCountTextView;
        NativeRecyclerView nativeads;

        public ViewHolder(View itemView) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.album_name_textview);
            songCountTextView = itemView.findViewById(R.id.song_count_textview);
            nativeads = itemView.findViewById(R.id.nativeads);
        }
    }

    public interface OnAlbumClickListener{
        void onAlbumClick(String albumName);
    }
}
