package com.hdvideo.allformats.player.Fragments;

import static com.hdvideo.allformats.player.Extras.Utils.openMenuDialog;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioInfoList;
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

import com.google.android.material.card.MaterialCardView;
import com.hdvideo.allformats.player.Activity.ResultActivity;
import com.hdvideo.allformats.player.Adapter.ArtistsAdapter;
import com.hdvideo.allformats.player.Adapter.AudioAlbumsAdapter;
import com.hdvideo.allformats.player.Adapter.AudioPlayListAdapter;
import com.hdvideo.allformats.player.Adapter.MusicAdapter;
import com.hdvideo.allformats.player.Extras.AppAsyncTask;
import com.hdvideo.allformats.player.Extras.AppInterfaces;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentMusicBinding;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
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

    FragmentMusicBinding binding;
    SharePreferences preferences;
    int sort_by = 0;
    List<AudioInfo> mainList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMusicBinding.inflate(getLayoutInflater());
        preferences = new SharePreferences(requireContext());
        binding.allSongsRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.albumRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.artistRv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.playlistRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));


        switchUi(0);

        binding.allSongsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(0);
            }
        });

        binding.allSongsBt.setOnClickListener(new View.OnClickListener() {
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

        binding.albumTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(2);
            }
        });

        binding.artistTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUi(3);
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
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",7).putExtra("name",getString(R.string.recently_played)));
            }
        });

        binding.favoritesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",8).putExtra("name",getString(R.string.favorites)));
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
                        mainAudioInfoList = mainList;
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",10).putExtra("name",binding.searchEd.getText().toString().toLowerCase().trim()));
                        binding.searchEd.setText("");
                    }
                    return true;
                }
                return false;
            }
        });


        return binding.getRoot();
    }

    private void setAdapterForAlbum() {
        AppAsyncTask.Albums albums = new AppAsyncTask.Albums(requireActivity(), new AppInterfaces.AlbumsListener() {
            @Override
            public void getAlbums(Map<String, Integer> albumList) {
                AudioAlbumsAdapter albumsAdapter = new AudioAlbumsAdapter(requireActivity(), albumList, new AudioAlbumsAdapter.OnAlbumClickListener() {
                    @Override
                    public void onAlbumClick(String albumName) {
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 1).putExtra("name", albumName));
                    }
                });
                binding.albumRv.setAdapter(albumsAdapter);
            }
        });
        albums.execute();
    }

    private void setAdapterForPlaylist() {
        AudioPlayListAdapter adapter = new AudioPlayListAdapter(false, new AudioPlayListAdapter.AudioPlayListClickListener() {
            @Override
            public void onDelete() {
                setAdapterForPlaylist();
            }

            @Override
            public void onItemClick(String playlistName) {
                startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type",6).putExtra("name", playlistName));
            }
        });
        binding.playlistRv.setAdapter(adapter);
        adapter.submitList(preferences.getAudioPlaylists());
    }

    private void setAdapterForArtist() {
        AppAsyncTask.Artists artists = new AppAsyncTask.Artists(requireActivity(), new AppInterfaces.ArtistsListener() {
            @Override
            public void getArtists(Map<String, Integer> artistsList) {
                ArtistsAdapter artistsAdapter = new ArtistsAdapter(requireActivity(), artistsList, new ArtistsAdapter.OnArtistClickListener() {
                    @Override
                    public void onArtistClick(String artistName) {
                        startActivity(new Intent(requireActivity(), ResultActivity.class).putExtra("type", 2).putExtra("name", artistName));
                    }
                });
                binding.artistRv.setAdapter(artistsAdapter);
            }
        });
        artists.execute();
    }

    private void setAdapterForAll() {
        AppAsyncTask.AllSongs allSongs = new AppAsyncTask.AllSongs(requireActivity(), new AppInterfaces.AllAudiosListener() {
            @Override
            public void getAllAudios(List<AudioInfo> allAudioList) {
                mainList = allAudioList;
                MusicAdapter adapter = new MusicAdapter(requireActivity(), allAudioList, new AppInterfaces.OnMoreListener() {
                    @Override
                    public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                        openMenuDialog(requireActivity(),id,name, path, size, false, more, "");
                    }
                });
                binding.allSongsRv.setAdapter(adapter);
            }
        });
        allSongs.execute();
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

        Collections.sort(mainList, new Comparator<AudioInfo>() {
            @Override
            public int compare(AudioInfo model1, AudioInfo model2) {
                return model1.getName().compareTo(model2.getName());
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
                Collections.sort(mainList, new Comparator<AudioInfo>() {
                    @Override
                    public int compare(AudioInfo model1, AudioInfo model2) {
                        return model1.getName().compareTo(model2.getName());
                    }
                });
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
                Collections.sort(mainList, new Comparator<AudioInfo>() {
                    @Override
                    public int compare(AudioInfo model1, AudioInfo model2) {
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
                dateRb.setStrokeColor(Color.parseColor("#264E73"));
                nameRb.setStrokeColor(Color.parseColor("#264E73"));
                dateRbIv.setImageResource(R.drawable.unselected_rb);
                nameRbIv.setImageResource(R.drawable.unselected_rb);
                sizeRb.setStrokeColor(Utils.setColorFromAttribute(requireActivity(), R.attr.light_color, R.color.light_blue));
                sizeRbIv.setImageResource(R.drawable.selected_rb);
                Collections.sort(mainList, new Comparator<AudioInfo>() {
                    @Override
                    public int compare(AudioInfo model1, AudioInfo model2) {
                        return Double.compare(model1.getSizeInMB(), model2.getSizeInMB());
                    }
                });
            }
        });

        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MusicAdapter adapter = new MusicAdapter(requireActivity(), mainList, new AppInterfaces.OnMoreListener() {
                    @Override
                    public void onMoreClick(long id, String name, String path, String size, ImageView more) {
                        openMenuDialog(requireActivity(),id,name, path, size, false, more, "");
                    }
                });
                binding.allSongsRv.setAdapter(adapter);
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
                    preferences.createEmptyAudioPlaylist(nameEd.getText().toString().trim());
                    setAdapterForPlaylist();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void switchUi(int i) {
        binding.allSongsTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.playlistTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.albumTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.artistTab.setBackgroundResource(R.drawable.tab_unselected);
        binding.playlistLl.setVisibility(View.GONE);
        binding.albumRv.setVisibility(View.GONE);
        binding.artistRv.setVisibility(View.GONE);
        binding.allSongsRv.setVisibility(View.GONE);
        binding.add.setVisibility(View.GONE);
        binding.sort.setVisibility(View.GONE);
        switch (i) {
            case 0:
                binding.allSongsTab.setBackgroundResource(R.drawable.tab_selected);
                binding.allSongsRv.setVisibility(View.VISIBLE);
                binding.sort.setVisibility(View.VISIBLE);
                setAdapterForAll();
                break;
            case 1:
                binding.playlistTab.setBackgroundResource(R.drawable.tab_selected);
                binding.playlistLl.setVisibility(View.VISIBLE);
                binding.add.setVisibility(View.VISIBLE);
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    binding.countAll.setText(Utils.getAllAudioFiles(requireContext()).size() + " Songs");
                    binding.countRecents.setText(preferences.getAudioDataModelList().size() + " Songs");
                    binding.countFav.setText(preferences.getFavAudioDataModelList().size() + " Songs");
                });
                setAdapterForPlaylist();
                break;
            case 2:
                binding.albumTab.setBackgroundResource(R.drawable.tab_selected);
                binding.albumRv.setVisibility(View.VISIBLE);
                binding.sort.setVisibility(View.VISIBLE);
                setAdapterForAlbum();
                break;
            case 3:
                binding.artistTab.setBackgroundResource(R.drawable.tab_selected);
                binding.artistRv.setVisibility(View.VISIBLE);
                binding.sort.setVisibility(View.VISIBLE);
                setAdapterForArtist();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            String countAll = Utils.getAllAudioFiles(requireContext()).size() + " Songs";
            String countRecents = preferences.getAudioDataModelList().size() + " Songs";
            String countFav = preferences.getFavAudioDataModelList().size() + " Songs";
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