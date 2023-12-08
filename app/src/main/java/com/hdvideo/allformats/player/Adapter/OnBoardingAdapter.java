package com.hdvideo.allformats.player.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideo.allformats.player.Models.ObDataModel;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ViewOnboardScreenBinding;

import java.util.Objects;

public class OnBoardingAdapter extends ListAdapter<ObDataModel, OnBoardingAdapter.ViewHolder> {

    static DiffUtil.ItemCallback<ObDataModel> diffCallback = new DiffUtil.ItemCallback<ObDataModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ObDataModel oldItem, @NonNull ObDataModel newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ObDataModel oldItem, @NonNull ObDataModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    private OnNextBtnClickListener onNextBtnClickListener;

    public OnBoardingAdapter(OnNextBtnClickListener onNextBtnClickListener) {
        super(diffCallback);
        this.onNextBtnClickListener = onNextBtnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewOnboardScreenBinding binding = ViewOnboardScreenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObDataModel model = getItem(position);

        holder.binding.image.setImageResource(model.getIcon());
        holder.binding.dots.setImageResource(model.getGifImage());

        holder.binding.title.setText(model.getName());

        holder.binding.desc.setText(model.getValue());

        holder.binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextBtnClickListener.onClick();
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewOnboardScreenBinding binding;

        public ViewHolder(@NonNull ViewOnboardScreenBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface OnNextBtnClickListener {
        void onClick();
    }

}
