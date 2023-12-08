package com.hdvideo.allformats.player.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hdvideo.allformats.player.Adapter.ThemesAdapter;
import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Models.ThemesModal;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentThemesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThemesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThemesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThemesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThemesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThemesFragment newInstance(String param1, String param2) {
        ThemesFragment fragment = new ThemesFragment();
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

    FragmentThemesBinding binding;

    ThemesAdapter themesAdapter;

    List<ThemesModal> themesModalList;

    private static final String TAG = "ThemesFragment";

    int themeId = R.style.Base_Theme_HDVideoPlayerAllFormats;

    SharePreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentThemesBinding.inflate(getLayoutInflater());

        preferences = new SharePreferences(requireContext());

        fillList();

        themesAdapter = new ThemesAdapter(themesModalList);
        binding.viewPager.setAdapter(themesAdapter);


        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);
        binding.viewPager.setOffscreenPageLimit(5);
        binding.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                themesAdapter.currentPos = binding.viewPager.getCurrentItem();
                themesAdapter.notifyDataSetChanged();
                switch (binding.viewPager.getCurrentItem()){
                    case 0:
                        binding.preview.setImageResource(R.drawable.theme_preview_one);
                        themeId = R.style.Base_Theme_HDVideoPlayerAllFormats;
                        break;
                    case 1:
                        binding.preview.setImageResource(R.drawable.theme_preview_two);
                        themeId = R.style.Base_Theme_HDVideoPlayerAllFormats_Second;
                        break;
                    case 2:
                        binding.preview.setImageResource(R.drawable.theme_preview_three);
                        themeId = R.style.Base_Theme_HDVideoPlayerAllFormats_Third;
                        break;
                    case 3:
                        binding.preview.setImageResource(R.drawable.theme_preview_four);
                        themeId = R.style.Base_Theme_HDVideoPlayerAllFormats_Fourth;
                        break;
                    case 4:
                        binding.preview.setImageResource(R.drawable.theme_preview_five);
                        themeId = R.style.Base_Theme_HDVideoPlayerAllFormats_Fifth;
                        break;
                }
            }
        });

        binding.applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.putInt(Constants.THEME_ID, themeId);
                requireActivity().recreate();
            }
        });

        return binding.getRoot();
    }

    private void fillList() {
        themesModalList = new ArrayList<>();
        themesModalList.add(new ThemesModal(R.drawable.selected_theme_one, R.drawable.unselected_theme_one));
        themesModalList.add(new ThemesModal(R.drawable.selected_theme_two, R.drawable.unselected_theme_two));
        themesModalList.add(new ThemesModal(R.drawable.selected_theme_three, R.drawable.unselected_theme_three));
        themesModalList.add(new ThemesModal(R.drawable.selected_theme_four, R.drawable.unselected_theme_four));
        themesModalList.add(new ThemesModal(R.drawable.selected_theme_five, R.drawable.unselected_theme_five));
    }
}