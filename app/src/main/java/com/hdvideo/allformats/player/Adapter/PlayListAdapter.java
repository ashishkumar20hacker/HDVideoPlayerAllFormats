package com.hdvideo.allformats.player.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Models.VideoPlaylistModel;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ItemPlaylistBinding;

import java.util.Objects;

public class PlayListAdapter extends ListAdapter<VideoPlaylistModel, PlayListAdapter.ViewHolder> {
    static DiffUtil.ItemCallback<VideoPlaylistModel> diffCallback = new DiffUtil.ItemCallback<VideoPlaylistModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull VideoPlaylistModel oldItem, @NonNull VideoPlaylistModel newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull VideoPlaylistModel oldItem, @NonNull VideoPlaylistModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    private SharePreferences preferences;
    private PlayListClickListener playListClickListener;
    private boolean isAdd = false;

    public PlayListAdapter(boolean isAdd, PlayListClickListener playListClickListener){
        super(diffCallback);
        this.isAdd = isAdd;
        this.playListClickListener = playListClickListener;
    }

    @NonNull
    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        preferences = new SharePreferences(parent.getContext());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapter.ViewHolder holder, int position) {
        VideoPlaylistModel model = getItem(position);
        holder.binding.title.setText(model.getPlaylistName());
        holder.binding.count.setText(model.getVideoList().size()+" Videos");
        holder.binding.placeholder.setImageResource(R.drawable.video_playlist_ph);

        if (isAdd) {
            holder.binding.delete.setVisibility(View.GONE);
        }

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.deletePlaylist(model.getPlaylistName());
                playListClickListener.onDelete();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playListClickListener.onItemClick(model.getPlaylistName());
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPlaylistBinding binding;
        public ViewHolder(@NonNull ItemPlaylistBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface PlayListClickListener {
        void onDelete();

        void onItemClick(String playlistName);
    }
}
