package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.makeStatusBarTransparent2;
import static com.hdvideo.allformats.player.Extras.Utils.nextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.hdvideo.allformats.player.Adapter.OnBoardingAdapter;
import com.hdvideo.allformats.player.Models.ObDataModel;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityOnboardingBinding;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    ActivityOnboardingBinding binding;
    OnBoardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent2(OnboardingActivity.this);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new OnBoardingAdapter(new OnBoardingAdapter.OnNextBtnClickListener() {
            @Override
            public void onClick() {
                if (binding.viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
                } else {
                    nextActivity(OnboardingActivity.this, TermsOfUseActivity.class);
                }
            }
        });
        adapter.submitList(getBoardList());
        binding.viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

    }

    private List<ObDataModel> getBoardList() {
        List<ObDataModel> list = new ArrayList<>();


        list.add(new ObDataModel(getString(R.string.title_1), getString(R.string.desc_1), R.drawable.ob_one_img, R.drawable.ob_one_dots));
        list.add(new ObDataModel(getString(R.string.title_2), getString(R.string.desc_2), R.drawable.ob_two_img, R.drawable.ob_two_dots));

        return list;
    }
}