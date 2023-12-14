package com.hdvideo.allformats.player.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Models.AudioPlaylistModel;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ItemPlaylistBinding;

import java.util.Objects;

public class AudioPlayListAdapter extends ListAdapter<AudioPlaylistModel, AudioPlayListAdapter.ViewHolder> {
    static DiffUtil.ItemCallback<AudioPlaylistModel> diffCallback = new DiffUtil.ItemCallback<AudioPlaylistModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull AudioPlaylistModel oldItem, @NonNull AudioPlaylistModel newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AudioPlaylistModel oldItem, @NonNull AudioPlaylistModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    private SharePreferences preferences;
    private AudioPlayListClickListener playListClickListener;
    private boolean isAdd = false;

    public AudioPlayListAdapter(boolean isAdd, AudioPlayListClickListener playListClickListener){
        super(diffCallback);
        this.isAdd = isAdd;
        this.playListClickListener = playListClickListener;
    }

    @NonNull
    @Override
    public AudioPlayListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        preferences = new SharePreferences(parent.getContext());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioPlayListAdapter.ViewHolder holder, int position) {
        AudioPlaylistModel model = getItem(position);
        holder.binding.title.setText(model.getPlaylistName());
        holder.binding.count.setText(model.getAudioList().size()+" Songs");
        holder.binding.placeholder.setImageResource(R.drawable.new_playlist);
        if (isAdd) {
            holder.binding.delete.setVisibility(View.GONE);
        }
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.deleteAudioPlaylist(model.getPlaylistName());
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

    public interface AudioPlayListClickListener {
        void onDelete();

        void onItemClick(String playlistName);
    }
}
