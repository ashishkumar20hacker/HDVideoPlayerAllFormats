package com.hdvideo.allformats.player.Adapter;

import android.animation.LayoutTransition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Models.ThemesModal;
import com.hdvideo.allformats.player.databinding.ItemThemesBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    public int currentPos = 0;
    List<ThemesModal> list;
    boolean isSize;
    int size = 0;
    public ThemesAdapter(List<ThemesModal> list) {
        super();
        this.list = list;
    }
    
    @NonNull
    @Override
    public ThemesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemThemesBinding binding = ItemThemesBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemesAdapter.ViewHolder holder, int position) {
        ThemesModal modal = list.get(holder.getAdapterPosition());
        if(holder.getAdapterPosition() == currentPos){
            holder.binding.image.setImageResource(modal.getSelected_image());
        }else holder.binding.image.setImageResource(modal.getUnselected_image());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemThemesBinding binding;

        public ViewHolder(@NonNull ItemThemesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}*/


public class ThemesAdapter extends ListAdapter<ThemesModal, ThemesAdapter.ViewHolder> {

    public int currentPos = 0;

    public ThemesAdapter() {
        super(new ThemesDiffCallback());
    }

    @NonNull
    @Override
    public ThemesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemThemesBinding binding = ItemThemesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemesAdapter.ViewHolder holder, int position) {
        ThemesModal modal = getItem(position);
        if (position == currentPos) {
            holder.binding.image.setImageResource(modal.getSelected_image());
        } else {
            holder.binding.image.setImageResource(modal.getUnselected_image());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemThemesBinding binding;

        public ViewHolder(@NonNull ItemThemesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private static class ThemesDiffCallback extends DiffUtil.ItemCallback<ThemesModal> {
        @Override
        public boolean areItemsTheSame(@NonNull ThemesModal oldItem, @NonNull ThemesModal newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ThemesModal oldItem, @NonNull ThemesModal newItem) {
            return oldItem == newItem;
        }
    }
}
