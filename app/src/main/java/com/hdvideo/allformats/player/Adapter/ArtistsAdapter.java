package com.hdvideo.allformats.player.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {

    private List<String> artistNames;
    private Map<String, Integer> artistSongsCount;
    private LayoutInflater inflater;
    private OnArtistClickListener onArtistClickListener;

    public ArtistsAdapter(Context context, Map<String, Integer> artistSongsCount, OnArtistClickListener onArtistClickListener) {
        this.artistNames = new ArrayList<>(artistSongsCount.keySet());
        this.artistSongsCount = artistSongsCount;
        this.onArtistClickListener = onArtistClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String artistName = artistNames.get(position);
        int songCount = artistSongsCount.get(artistName);

        holder.artistNameTextView.setText(artistName);
        holder.songCountTextView.setText("Songs: " + songCount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onArtistClickListener.onArtistClick(artistName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artistNameTextView;
        TextView songCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            artistNameTextView = itemView.findViewById(R.id.artist_name_textview);
            songCountTextView = itemView.findViewById(R.id.song_count_textview);
        }
    }

    public interface OnArtistClickListener {
        void onArtistClick(String artistName);
    }
}
