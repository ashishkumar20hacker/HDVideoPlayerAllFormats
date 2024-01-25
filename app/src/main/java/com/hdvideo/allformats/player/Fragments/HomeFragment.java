package com.hdvideo.allformats.player.Fragments;

import static com.hdvideo.allformats.player.Extras.Utils.nextActivity;
import static com.hdvideo.allformats.player.Extras.Utils.openMenuDialog;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoInfoList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.google.android.material.card.MaterialCardView;
import com.hdvideo.allformats.player.Activity.ResultActivity;
import com.hdvideo.allformats.player.Adapter.PlayListAdapter;
import com.hdvideo.allformats.player.Adapter.VideoAdapter;
import com.hdvideo.allformats.player.Adapter.VideoFoldersAdapter;
import com.hdvideo.allformats.player.Extras.AppAsyncTask;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentHomeBinding;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    int type = 111;

    public HomeFragment(int type) {
        this.type = type;
        // Required empty public constructor
    }

    int sortType = 0;
    int mainSort = 0;

    FragmentHomeBinding binding;
    SharePreferences preferences;

    List<VideoInfo> mainList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        preferences = new SharePreferences(requireContext());

        binding.allVideoRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.foldersRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.playlistRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        if (type == 0) {
            switchUi(2);
        } else if (type == 3 || type == 4 || type == 5) {
            switchUi(1);
        } else {
            switchUi(0);
        }

        binding.allVideosTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    switchUi(0);
                });
            }
        });
        binding.allVideoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    switchUi(0);
                });
            }
        });

        binding.playlistTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    switchUi(1);
                });
            }
        });

        binding.foldersTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    switchUi(2);
                });
            }
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlaylistDialog();
            }
        });

        binding.sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog();
            }
        });

        binding.recentsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 4).putExtra("name", getString(R.string.recently_played)));
                });
            }
        });

        binding.favoritesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(requireActivity(), isLoaded -> {
                    startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 5).putExtra("name", getString(R.string.favorites)));
                });
            }
        });

        binding.searchEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (binding.searchEd.getText().toString().isEmpty()) {
                        binding.searchEd.setError(getString(R.string.please_enter_something_to_search));
                        binding.searchEd.requestFocus();
                    } else {
                        mainVideoInfoList = mainList;
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 9).putExtra("name", binding.searchEd.getText().toString().toLowerCase().trim()));
                        binding.searchEd.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void setAdapterForFolders() {
        AppAsyncTask.VideoFolders videoFolders = new AppAsyncTask.VideoFolders(requireActivity(), new AppInterfaces.VideosFolderListener() {
            @Override
            public void getVideosFolder(Map<String, Integer> folderList) {
                VideoFoldersAdapter foldersAdapter = new VideoFoldersAdapter(requireActivity(), folderList, new VideoFoldersAdapter.OnFolderClickListener() {
                    @Override
                    public void onFolderClick(String folderName, String path) {
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 0).putExtra("name", folderName).putExtra("path", path));
                    }
                });
                binding.foldersRv.setAdapter(foldersAdapter);
            }
        });
        videoFolders.execute();
    }

    private void setAdapterForAll() {
        AppAsyncTask.AllVideos allVideos = new AppAsyncTask.AllVideos(requireActivity(), new AppInterfaces.AllVideosListener() {
            @Override
            public void getAllVideos(List<VideoInfo> allVideoList) {
                mainList = allVideoList;
                VideoAdapter adapter = new VideoAdapter(requireActivity(), allVideoList, new AppInterfaces.OnMoreListener() {
                    @Override
                    public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                        openMenuDialog(requireActivity(), id, name, path, size, true, more, "");
                    }
                });
                binding.allVideoRv.setAdapter(adapter);
            }
        });
        allVideos.execute();
    }

    private void sortDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        View lay = inflater.inflate(R.layout.dialog_sort, null);
        ImageView closeBt = lay.findViewById(R.id.close);
        MaterialCardView nameRb = lay.findViewById(R.id.name_rb);
        MaterialCardView dateRb = lay.findViewById(R.id.date_rb);
        MaterialCardView sizeRb = lay.findViewById(R.id.size_rb);
        ImageView nameRbIv = lay.findViewById(R.id.name_rb_iv);
        ImageView dateRbIv = lay.findViewById(R.id.date_rb_iv);
        ImageView sizeRbIv = lay.findViewById(R.id.size_rb_iv);
        TextView applyBt = lay.findViewById(R.id.apply_bt);

        dialog.setContentView(lay);

        if (mainSort == 0) {
            nameRb.setStrokeColor(Color.parseColor("#264E73"));
            dateRb.setStrokeColor(Color.parseColor("#264E73"));
            sizeRb.setStrokeColor(Color.parseColor("#264E73"));
            nameRbIv.setImageResource(R.drawable.unselected_rb);
            dateRbIv.setImageResource(R.drawable.unselected_rb);
            sizeRbIv.setImageResource(R.drawable.unselected_rb);
        } else if (mainSort == 1) {
            dateRb.setStrokeColor(Color.parseColor("#264E73"));
            sizeRb.setStrokeColor(Color.parseColor("#264E73"));
            dateRbIv.setImageResource(R.drawable.unselected_rb);
            sizeRbIv.setImageResource(R.drawable.unselected_rb);
            nameRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
            nameRbIv.setImageResource(R.drawable.selected_rb);
        } else if (mainSort == 2) {
            nameRb.setStrokeColor(Color.parseColor("#264E73"));
            sizeRb.setStrokeColor(Color.parseColor("#264E73"));
            nameRbIv.setImageResource(R.drawable.unselected_rb);
            sizeRbIv.setImageResource(R.drawable.unselected_rb);
            dateRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
            dateRbIv.setImageResource(R.drawable.selected_rb);
        } else if (mainSort == 3){
            dateRb.setStrokeColor(Color.parseColor("#264E73"));
            nameRb.setStrokeColor(Color.parseColor("#264E73"));
            dateRbIv.setImageResource(R.drawable.unselected_rb);
            nameRbIv.setImageResource(R.drawable.unselected_rb);
            sizeRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
            sizeRbIv.setImageResource(R.drawable.selected_rb);            
        }

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Collections.sort(mainList, new Comparator<VideoInfo>() {
            @Override
            public int compare(VideoInfo model1, VideoInfo model2) {
                return model1.getName().compareTo(model2.getName());
            }
        });

        nameRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType = 1;
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                nameRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                nameRbIv.setImageResource(R.drawable.selected_rb);
                Collections.sort(mainList, new Comparator<VideoInfo>() {
                    @Override
                    public int compare(VideoInfo model1, VideoInfo model2) {
                        return model1.getName().compareTo(model2.getName());
                    }
                });
            }
        });

        dateRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType = 2;
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                dateRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                dateRbIv.setImageResource(R.drawable.selected_rb);
                Collections.sort(mainList, new Comparator<VideoInfo>() {
                    @Override
                    public int compare(VideoInfo model1, VideoInfo model2) {
                        File file = new File(model1.getPath());
                        Date lastModDate = new Date(file.lastModified());
                        File file2 = new File(model2.getPath());
                        Date lastModDate2 = new Date(file2.lastModified());
                        return lastModDate.compareTo(lastModDate2);
                    }
                });
            }
        });

        sizeRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType = 3;
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                sizeRbIv.setImageResource(R.drawable.selected_rb);
                Collections.sort(mainList, new Comparator<VideoInfo>() {
                    @Override
                    public int compare(VideoInfo model1, VideoInfo model2) {
                        return Double.compare(model1.getSizeInMB(), model2.getSizeInMB());
                    }
                });
            }
        });

        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mainSort = sortType;
                VideoAdapter adapter = new VideoAdapter(requireActivity(), mainList, new AppInterfaces.OnMoreListener() {
                    @Override
                    public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                        openMenuDialog(requireActivity(), id, name, path, size, true, more, "");
                    }
                });
                binding.allVideoRv.setAdapter(adapter);
            }
        });

        dialog.show();
    }


    private void createPlaylistDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        View lay = inflater.inflate(R.layout.dialog_playlist, null);
        ImageView closeBt = lay.findViewById(R.id.close);
        EditText nameEd = lay.findViewById(R.id.name_ed);
        TextView createBt = lay.findViewById(R.id.create_bt);

        dialog.setContentView(lay);

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        createBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEd.getText().toString().isEmpty()) {
                    nameEd.setError("Please enter playlist name");
                    nameEd.requestFocus();
                } else {
                    preferences.createEmptyPlaylist(nameEd.getText().toString().trim());
                    setAdapterForPlaylist();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void setAdapterForPlaylist() {
        PlayListAdapter adapter = new PlayListAdapter(false, new PlayListAdapter.PlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 3).putExtra("name", playlistName));
            }
        });
        binding.playlistRv.setAdapter(adapter);
        adapter.submitList(preferences.getPlaylists());
    }

    private void switchUi(int i) {
        binding.allVideosTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.playlistTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.foldersTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.playlistLl.setVisibility(View.GONE);
        binding.foldersRv.setVisibility(View.GONE);
        binding.allVideoRv.setVisibility(View.GONE);
        binding.add.setVisibility(View.GONE);
        binding.sort.setVisibility(View.GONE);
        switch (i) {
            case 0:
                binding.allVideosTab.setBackgroundResource(R.drawable.tab_selected);
                binding.allVideoRv.setVisibility(View.VISIBLE);
                binding.sort.setVisibility(View.VISIBLE);
                setAdapterForAll();
                break;
            case 1:
                binding.playlistTab.setBackgroundResource(R.drawable.tab_selected);
                binding.playlistLl.setVisibility(View.VISIBLE);
                binding.add.setVisibility(View.VISIBLE);
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    binding.countAll.setText(Utils.getVideoList(requireContext()).size() + " Videos");
                    binding.countRecents.setText(preferences.getVideoDataModelList().size() + " Videos");
                    binding.countFav.setText(preferences.getFavVideoDataModelList().size() + " Videos");
                });
                setAdapterForPlaylist();
                break;
            case 2:
                binding.foldersTab.setBackgroundResource(R.drawable.tab_selected);
                binding.foldersRv.setVisibility(View.VISIBLE);
                binding.sort.setVisibility(View.VISIBLE);
                setAdapterForFolders();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();


/*        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            String countAll = Utils.getVideoList(requireContext()).size() + " Videos";
            String countRecents = preferences.getVideoDataModelList().size() + " Videos";
            String countFav = preferences.getFavVideoDataModelList().size() + " Videos";
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.countAll.setText(countAll);
                    binding.countRecents.setText(countRecents);
                    binding.countFav.setText(countFav);
                }
            });
        });*/
    }


}