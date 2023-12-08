package com.hdvideo.allformats.player.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentStatusBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentStatusBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(getLayoutInflater());

        binding.statusRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.savedRv.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));

        binding.videosTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(0);
            }
        });

        binding.savedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(1);
            }
        });

        return binding.getRoot();
    }

    private void switchUi(int i) {
        if (i == 0) {
            binding.savedTab.setBackgroundResource(R.drawable.tab_unselected);
            binding.videosTab.setBackgroundResource(R.drawable.tab_selected);
            setAdapter(0);
        } else {
            binding.videosTab.setBackgroundResource(R.drawable.tab_unselected);
            binding.savedTab.setBackgroundResource(R.drawable.tab_selected);
            setAdapter(1);
        }
    }

    private void setAdapter(int i) {
        if (i == 0) {
            //TODO Status
            binding.noSaved.setVisibility(View.GONE);
            binding.savedRv.setVisibility(View.GONE);
            binding.noStatus.setVisibility(View.VISIBLE);
        } else {
            //TODO Saved
            binding.noStatus.setVisibility(View.GONE);
            binding.statusRv.setVisibility(View.GONE);
            binding.noSaved.setVisibility(View.VISIBLE);
        }
    }
}