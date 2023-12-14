package com.hdvideo.allformats.player.Activity;

import static com.hdvideo.allformats.player.Extras.Utils.showRateApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Fragments.HomeFragment;
import com.hdvideo.allformats.player.Fragments.MusicFragment;
import com.hdvideo.allformats.player.Fragments.SettingsFragment;
import com.hdvideo.allformats.player.Fragments.StatusFragment;
import com.hdvideo.allformats.player.Fragments.ThemesFragment;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.ActivityDashboardBinding;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    ActivityDashboardBinding binding;

    String fragmentName;

    SharePreferences preferences;
    public static List<VideoInfo> mainVideoInfoList;
    public static List<AudioInfo> mainAudioInfoList;
    public static String mainOldFilePath = "";
    public static String mainNewFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeStatusBarTransparent2(this);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new SharePreferences(this);
        preferences.putBoolean(Constants.isFirstRun, false);

        setTheme(preferences.getInt(Constants.THEME_ID, R.style.Base_Theme_HDVideoPlayerAllFormats));

        switchUi(2);

        binding.statusNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(0);
            }
        });

        binding.musicNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(1);
            }
        });

        binding.homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(2);
            }
        });

        binding.themesNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(3);
            }
        });

        binding.settingsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(4);
            }
        });

    }

    private void changeFragment(Fragment explorefragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, explorefragment);
        fragmentTransaction.commit();

        Fragment currentFragment = explorefragment;

        fragmentName = currentFragment.getClass().getSimpleName();
        Log.d("Current Fragment", "Fragment Name: " + fragmentName);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (fragmentName.contains("HomeFragment")) {
            exitDialog();
        } else {
            switchUi(2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                Utils.renameAudioFile(mainOldFilePath, mainNewFileName);
                //TODO need to update ui
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult:  RESULT_CANCELED");
            }
        } else if (requestCode == 124) {
            if (resultCode == Activity.RESULT_OK) {
                Utils.deleteFile(mainOldFilePath);
                //TODO need to update ui
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult:  RESULT_CANCELED");
            }
        }
    }

    private void exitDialog() {

        Dialog dialog = new Dialog(DashboardActivity.this, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        View lay = inflater.inflate(R.layout.exit_dialog, null);
        TextView exit;
        ImageView rateUs, gif_view, close;
        exit = lay.findViewById(R.id.exit);
        close = lay.findViewById(R.id.close);
        rateUs = lay.findViewById(R.id.rate_us);
        gif_view = lay.findViewById(R.id.gif_view);

        dialog.setContentView(lay);

        Glide.with(this).asGif().load(R.drawable.star_gif).into(gif_view);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRateApp(DashboardActivity.this);
            }
        });

        dialog.show();

    }


    private void switchUi(int i) {
        switch (i) {
            case 0 :
                binding.settingsNav.setImageResource(R.drawable.nav_unselected_settings);
                binding.themesNav.setImageResource(R.drawable.nav_unselected_themes);
                binding.homeNav.setImageResource(R.drawable.nav_unselected_home);
                binding.musicNav.setImageResource(R.drawable.nav_unselected_music);
                binding.statusNav.setImageResource(R.drawable.nav_selected_status);
                changeFragment(new StatusFragment());
                break;
            case 1 :
                changeFragment(new MusicFragment());
                binding.settingsNav.setImageResource(R.drawable.nav_unselected_settings);
                binding.themesNav.setImageResource(R.drawable.nav_unselected_themes);
                binding.homeNav.setImageResource(R.drawable.nav_unselected_home);
                binding.statusNav.setImageResource(R.drawable.nav_unselected_status);
                binding.musicNav.setImageResource(R.drawable.nav_selected_music);
                break;
            case 2 :
                changeFragment(new HomeFragment());
                binding.settingsNav.setImageResource(R.drawable.nav_unselected_settings);
                binding.themesNav.setImageResource(R.drawable.nav_unselected_themes);
                binding.musicNav.setImageResource(R.drawable.nav_unselected_music);
                binding.statusNav.setImageResource(R.drawable.nav_unselected_status);
                binding.homeNav.setImageResource(R.drawable.nav_selected_home);
                break;
            case 3 :
                binding.settingsNav.setImageResource(R.drawable.nav_unselected_settings);
                binding.homeNav.setImageResource(R.drawable.nav_unselected_home);
                binding.musicNav.setImageResource(R.drawable.nav_unselected_music);
                binding.statusNav.setImageResource(R.drawable.nav_unselected_status);
                binding.themesNav.setImageResource(R.drawable.nav_selected_themes);
                changeFragment(new ThemesFragment());
                break;
            case 4 :
                binding.themesNav.setImageResource(R.drawable.nav_unselected_themes);
                binding.homeNav.setImageResource(R.drawable.nav_unselected_home);
                binding.musicNav.setImageResource(R.drawable.nav_unselected_music);
                binding.statusNav.setImageResource(R.drawable.nav_unselected_status);
                binding.settingsNav.setImageResource(R.drawable.nav_selected_settings);
                changeFragment(new SettingsFragment());
                break;
        }
    }
}