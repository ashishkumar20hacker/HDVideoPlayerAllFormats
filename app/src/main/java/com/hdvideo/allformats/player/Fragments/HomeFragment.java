package com.hdvideo.allformats.player.Fragments;

import static com.hdvideo.allformats.player.Extras.Utils.nextActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.hdvideo.allformats.player.Activity.ResultActivity;
import com.hdvideo.allformats.player.Adapter.PlayListAdapter;
import com.hdvideo.allformats.player.Adapter.VideoAdapter;
import com.hdvideo.allformats.player.Extras.AppAsyncTask;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentHomeBinding;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    FragmentHomeBinding binding;
    SharePreferences preferences;
    int sort_by = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        preferences = new SharePreferences(requireContext());

        binding.allVideoRv.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        binding.foldersRv.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        binding.playlistRv.setLayoutManager(new GridLayoutManager(requireContext(),2));

        switchUi(0);

        binding.allVideosTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(0);
            }
        });
        binding.allVideoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(0);
            }
        });

        binding.playlistTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(1);
            }
        });

        binding.foldersTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(2);
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
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",4).putExtra("name",getString(R.string.recently_played)));
            }
        });

        binding.favoritesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",5).putExtra("name",getString(R.string.favorites)));
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
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",0).putExtra("name",folderName).putExtra("path",path));
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
                VideoAdapter adapter = new VideoAdapter(requireActivity(),allVideoList);
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

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        nameRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                nameRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                nameRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 0;
            }
        });

        dateRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                sizeRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRbIv.setImageResource(R.drawable.unselected_rb);
                dateRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                dateRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 1;
            }
        });

        sizeRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                sizeRbIv.setImageResource(R.drawable.selected_rb);
                sort_by = 2;
            }
        });

        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (sort_by == 0){
                    //TODO sort by name
                } else if (sort_by == 1){
                    //TODO sort by date
                } else {
                    //TODO sort by size
                }
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
                if (nameEd.getText().toString().isEmpty()){
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
        PlayListAdapter adapter = new PlayListAdapter(new PlayListAdapter.PlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",3).putExtra("name", playlistName));
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
                service.execute(()->{
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



}