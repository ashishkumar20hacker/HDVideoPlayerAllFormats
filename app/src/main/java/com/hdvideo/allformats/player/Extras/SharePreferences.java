package com.hdvideo.allformats.player.Extras;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.AudioPlaylistModel;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.Models.VideoPlaylistModel;
import com.hdvideo.allformats.player.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class SharePreferences {

    private Context applicationContext;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private static final String AUDIO_LIST_KEY = "AUDIO_LIST_KEY";
    private static final String VIDEO_LIST_KEY = "VIDEO_LIST_KEY";
    private static final String FAV_AUDIO_LIST_KEY = "FAV_AUDIO_LIST_KEY";
    private static final String FAV_VIDEO_LIST_KEY = "FAV_VIDEO_LIST_KEY";

    public SharePreferences(Context applicationContext) {
        this.applicationContext = applicationContext;
        gson = new Gson();
        String preferencesName = applicationContext.getString(R.string.app_name);
        sharedPreferences = applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }
    // Store a list of DataModels in SharedPreferences
    public void putAudioDataModelList(List<AudioInfo> voiceEffectList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dataModelListJson = gson.toJson(voiceEffectList);
        editor.putString(AUDIO_LIST_KEY, dataModelListJson);
        editor.apply();
    }

    // Retrieve the list of DataModels from SharedPreferences
    public List<AudioInfo> getAudioDataModelList() {
        String dataModelListJson = sharedPreferences.getString(AUDIO_LIST_KEY, null);
        if (dataModelListJson != null) {
            Type type = new TypeToken<ArrayList<AudioInfo>>() {
            }.getType();
            return gson.fromJson(dataModelListJson, type);
        } else {
            return new ArrayList<>();
        }
    }
    // Store a list of DataModels in SharedPreferences
    public void putVideoDataModelList(List<VideoInfo> voiceEffectList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dataModelListJson = gson.toJson(voiceEffectList);
        editor.putString(VIDEO_LIST_KEY, dataModelListJson);
        editor.apply();
    }

    // Retrieve the list of DataModels from SharedPreferences
    public List<VideoInfo> getVideoDataModelList() {
        String dataModelListJson = sharedPreferences.getString(VIDEO_LIST_KEY, null);
        if (dataModelListJson != null) {
            Type type = new TypeToken<ArrayList<VideoInfo>>() {
            }.getType();
            return gson.fromJson(dataModelListJson, type);
        } else {
            return new ArrayList<>();
        }
    }

    // Store a list of DataModels in SharedPreferences
    public void putFavAudioDataModelList(List<AudioInfo> voiceEffectList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dataModelListJson = gson.toJson(voiceEffectList);
        editor.putString(FAV_AUDIO_LIST_KEY, dataModelListJson);
        editor.apply();
    }

    // Retrieve the list of DataModels from SharedPreferences
    public List<AudioInfo> getFavAudioDataModelList() {
        String dataModelListJson = sharedPreferences.getString(FAV_AUDIO_LIST_KEY, null);
        if (dataModelListJson != null) {
            Type type = new TypeToken<ArrayList<AudioInfo>>() {
            }.getType();
            return gson.fromJson(dataModelListJson, type);
        } else {
            return new ArrayList<>();
        }
    }
    // Store a list of DataModels in SharedPreferences
    public void putFavVideoDataModelList(List<VideoInfo> voiceEffectList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dataModelListJson = gson.toJson(voiceEffectList);
        editor.putString(FAV_VIDEO_LIST_KEY, dataModelListJson);
        editor.apply();
    }

    // Retrieve the list of DataModels from SharedPreferences
    public List<VideoInfo> getFavVideoDataModelList() {
        String dataModelListJson = sharedPreferences.getString(FAV_VIDEO_LIST_KEY, null);
        if (dataModelListJson != null) {
            Type type = new TypeToken<ArrayList<VideoInfo>>() {
            }.getType();
            return gson.fromJson(dataModelListJson, type);
        } else {
            return new ArrayList<>();
        }
    }
    public static final String VIDEO_PLAYLIST_KEY = "VIDEO_PLAYLIST_KEY";
    public static final String AUDIO_PLAYLIST_KEY = "AUDIO_PLAYLIST_KEY";

    public void createEmptyPlaylist(String playlistName) {
        List<VideoPlaylistModel> playlists = getPlaylists();

        // Check if the playlist already exists
        for (VideoPlaylistModel playlist : playlists) {
            if (playlist.getPlaylistName().equals(playlistName)) {
                // Playlist already exists, return or perform appropriate action
                return;
            }
        }

        // Create a new empty playlist and add it to the list
        VideoPlaylistModel newPlaylist = new VideoPlaylistModel();
        newPlaylist.setPlaylistName(playlistName);
        newPlaylist.setVideoList(new ArrayList<>()); // Empty video list for a new playlist

        playlists.add(newPlaylist);

        // Save the updated list of playlists to SharedPreferences
        savePlaylists(playlists);
    }

   public void addItemsToPlaylist(String playlistName, List<VideoInfo> videosToAdd) {
       List<VideoPlaylistModel> playlists = getPlaylists();

       for (VideoPlaylistModel playlist : playlists) {
           if (playlist.getPlaylistName().equals(playlistName)) {
               Set<String> existingVideoPaths = new HashSet<>();
               for (VideoInfo videoInfo : playlist.getVideoList()) {
                   existingVideoPaths.add(videoInfo.getPath());
               }

               List<VideoInfo> videosToAddFiltered = new ArrayList<>();
               for (VideoInfo video : videosToAdd) {
                   if (!existingVideoPaths.contains(video.getPath())) {
                       videosToAddFiltered.add(video);
                   }
               }

               if (!videosToAddFiltered.isEmpty()) {
                   List<VideoInfo> videoList = playlist.getVideoList();
                   videoList.addAll(videosToAddFiltered);
                   playlist.setVideoList(videoList);

                   // Save the updated list of playlists to SharedPreferences
                   savePlaylists(playlists);
               }
               return;
           }
       }
   }

    public List<VideoPlaylistModel> getPlaylists() {
        String json = sharedPreferences.getString(VIDEO_PLAYLIST_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            TypeToken<List<VideoPlaylistModel>> typeToken = new TypeToken<List<VideoPlaylistModel>>() {};
            return gson.fromJson(json, typeToken.getType());
        }
    }
    public void savePlaylists(List<VideoPlaylistModel> playlists) {
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(VIDEO_PLAYLIST_KEY, json).apply();
    }

    public void deletePlaylist(String playlistName) {
        List<VideoPlaylistModel> playlists = getPlaylists();

        Iterator<VideoPlaylistModel> iterator = playlists.iterator();
        while (iterator.hasNext()) {
            VideoPlaylistModel playlist = iterator.next();
            if (playlist.getPlaylistName().equals(playlistName)) {
                iterator.remove();
                break;
            }
        }

        // Save the updated list of playlists to SharedPreferences
        savePlaylists(playlists);
    }

    public List<VideoInfo> getVideoListForPlaylist(String playlistName) {
        List<VideoPlaylistModel> playlists = getPlaylists();

        for (VideoPlaylistModel playlist : playlists) {
            if (playlist.getPlaylistName().equals(playlistName)) {
                return playlist.getVideoList();
            }
        }

        return new ArrayList<>(); // Return an empty list if the playlist doesn't exist
    }
    public void createEmptyAudioPlaylist(String playlistName) {
        List<AudioPlaylistModel> playlists = getAudioPlaylists();

        // Check if the playlist already exists
        for (AudioPlaylistModel playlist : playlists) {
            if (playlist.getPlaylistName().equals(playlistName)) {
                // Playlist already exists, return or perform appropriate action
                return;
            }
        }

        // Create a new empty playlist and add it to the list
        AudioPlaylistModel newPlaylist = new AudioPlaylistModel();
        newPlaylist.setPlaylistName(playlistName);
        newPlaylist.setAudioList(new ArrayList<>()); // Empty video list for a new playlist

        playlists.add(newPlaylist);

        // Save the updated list of playlists to SharedPreferences
        saveAudioPlaylists(playlists);
    }

   public void addItemsToAudioPlaylist(String playlistName, List<AudioInfo> videosToAdd) {
       List<AudioPlaylistModel> playlists = getAudioPlaylists();

       for (AudioPlaylistModel playlist : playlists) {
           if (playlist.getPlaylistName().equals(playlistName)) {
               Set<String> existingVideoPaths = new HashSet<>();
               for (AudioInfo videoInfo : playlist.getAudioList()) {
                   existingVideoPaths.add(videoInfo.getPath());
               }

               List<AudioInfo> videosToAddFiltered = new ArrayList<>();
               for (AudioInfo video : videosToAdd) {
                   if (!existingVideoPaths.contains(video.getPath())) {
                       videosToAddFiltered.add(video);
                   }
               }

               if (!videosToAddFiltered.isEmpty()) {
                   List<AudioInfo> videoList = playlist.getAudioList();
                   videoList.addAll(videosToAddFiltered);
                   playlist.setAudioList(videoList);

                   // Save the updated list of playlists to SharedPreferences
                   saveAudioPlaylists(playlists);
               }
               return;
           }
       }
   }

    public List<AudioPlaylistModel> getAudioPlaylists() {
        String json = sharedPreferences.getString(AUDIO_PLAYLIST_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            TypeToken<List<AudioPlaylistModel>> typeToken = new TypeToken<List<AudioPlaylistModel>>() {};
            return gson.fromJson(json, typeToken.getType());
        }
    }
    public void saveAudioPlaylists(List<AudioPlaylistModel> playlists) {
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(AUDIO_PLAYLIST_KEY, json).apply();
    }

    public void deleteAudioPlaylist(String playlistName) {
        List<AudioPlaylistModel> playlists = getAudioPlaylists();

        Iterator<AudioPlaylistModel> iterator = playlists.iterator();
        while (iterator.hasNext()) {
            AudioPlaylistModel playlist = iterator.next();
            if (playlist.getPlaylistName().equals(playlistName)) {
                iterator.remove();
                break;
            }
        }

        // Save the updated list of playlists to SharedPreferences
        saveAudioPlaylists(playlists);
    }

    public List<AudioInfo> getAudioListForPlaylist(String playlistName) {
        List<AudioPlaylistModel> playlists = getAudioPlaylists();

        for (AudioPlaylistModel playlist : playlists) {
            if (playlist.getPlaylistName().equals(playlistName)) {
                return playlist.getAudioList();
            }
        }

        return new ArrayList<>(); // Return an empty list if the playlist doesn't exist
    }

}
