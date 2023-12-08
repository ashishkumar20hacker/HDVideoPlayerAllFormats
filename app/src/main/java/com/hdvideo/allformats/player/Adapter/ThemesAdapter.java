package com.hdvideo.allformats.player.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.Models.ThemesModal;
import com.hdvideo.allformats.player.databinding.ItemThemesBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    public int currentPos = 0;
    List<ThemesModal> list;

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
}
