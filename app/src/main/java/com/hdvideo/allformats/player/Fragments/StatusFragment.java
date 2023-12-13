package com.hdvideo.allformats.player.Fragments;

import static com.hdvideo.allformats.player.Extras.Utils.showRateApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.hdvideo.allformats.player.Activity.DashboardActivity;
import com.hdvideo.allformats.player.Adapter.StatusAdapter;
import com.hdvideo.allformats.player.Adapter.VideoAdapter;
import com.hdvideo.allformats.player.Extras.Constants;
import com.hdvideo.allformats.player.Extras.SharePreferences;
import com.hdvideo.allformats.player.Extras.Utils;
import com.hdvideo.allformats.player.Models.DataModel;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;
import com.hdvideo.allformats.player.databinding.FragmentStatusBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    SharePreferences preferences;
    int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(getLayoutInflater());
        preferences = new SharePreferences(requireContext());
        binding.statusRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.savedRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

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

        if (preferences.getString(Constants.IS_WHATSAPP_GRANTED).isEmpty()) {
            permissionDialog();
        } else {
            binding.statusRv.setVisibility(View.VISIBLE);
            binding.noStatus.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    private void permissionDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(requireActivity());

        View lay = inflater.inflate(R.layout.permission_nav_dialog, null);
        TextView exit;
        ImageView close;
        exit = lay.findViewById(R.id.tv_getit);
        close = lay.findViewById(R.id.tv_cancel);

        dialog.setContentView(lay);

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
                if (Utils.appInstalledOrNot(getActivity(), "com.whatsapp")) {

                    StorageManager sm = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);

                    String statusDir = getWhatsupFolder();
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

                        String scheme = uri.toString();

                        scheme = scheme.replace("/root/", "/document/");

                        scheme += "%3A" + statusDir;

                        uri = Uri.parse(scheme);

                        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                    } else {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + statusDir));
                    }


                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                    startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE);

                } else {
                    Toast.makeText(getActivity(), "Please Install WhatsApp To Download Status!!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    public String getWhatsupFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + ".Statuses").isDirectory()) {
            return "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        } else {
            return "WhatsApp%2FMedia%2F.Statuses";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("onActivityResult: ", "" + data.getData());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    requireContext().getContentResolver()
                            .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            preferences.putString(Constants.IS_WHATSAPP_GRANTED, uri.toString());

            populateGrid();
        }
    }

    private DocumentFile[] getFromSdcard() {
        try {
            String treeUri = preferences.getString(Constants.IS_WHATSAPP_GRANTED);
            DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(treeUri));
            if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory()
                    && fromTreeUri.canRead() && fromTreeUri.canWrite()) {

                return fromTreeUri.listFiles();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!preferences.getString(Constants.IS_WHATSAPP_GRANTED).isEmpty()) {
            populateGrid();
        }
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
            if (preferences.getString(Constants.IS_WHATSAPP_GRANTED).isEmpty()) {
                permissionDialog();
            } else {
                populateGrid();
            }
        } else {
            //TODO Saved
            binding.noStatus.setVisibility(View.GONE);
            binding.statusRv.setVisibility(View.GONE);
            loadSaved();
        }
    }

    private void loadSaved() {
        List<VideoInfo> videosInFolder = Utils.getVideosFromFolder(requireActivity(), String.valueOf(Constants.downloadWhatsAppDir));
        if (videosInFolder.size() != 0) {
            VideoAdapter adapter = new VideoAdapter(requireActivity(), videosInFolder);
            binding.savedRv.setAdapter(adapter);
            binding.noSaved.setVisibility(View.GONE);
            binding.savedRv.setVisibility(View.VISIBLE);
        } else {
            binding.savedRv.setVisibility(View.GONE);
            binding.noSaved.setVisibility(View.VISIBLE);
        }
    }

    loadDataAsync async;

    public void populateGrid() {
        async = new loadDataAsync();
        async.execute();
    }

    class loadDataAsync extends AsyncTask<Void, Void, Void> {
        DocumentFile[] allFiles;
        ArrayList<DataModel> videoList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            allFiles = null;
            allFiles = getFromSdcard();
//            Arrays.sort(allFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
            for (int i = 0; i < allFiles.length; i++) {
                if (!allFiles[i].getUri().toString().contains(".nomedia")) {
                    String fileName = allFiles[i].getName();
                    if (fileName.toLowerCase().endsWith(".mp4")) {
                        videoList.add(new DataModel(allFiles[i].getUri().toString(), fileName));
                    }
//                    statusImageList.add(new DataModel(allFiles[i].getUri().toString(), allFiles[i].getName()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    StatusAdapter mAdapter = new StatusAdapter(getActivity(), videoList, true);
                    binding.statusRv.setAdapter(mAdapter);
                    binding.statusRv.setVisibility(View.VISIBLE);
                }
            }, 300);

            if (videoList == null || videoList.size() == 0) {
                binding.noStatus.setVisibility(View.VISIBLE);
            } else {
                binding.noStatus.setVisibility(View.GONE);
            }
        }
    }

}
