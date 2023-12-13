package com.hdvideo.allformats.player.Extras;

import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;

import java.util.List;
import java.util.Map;

public class AppInterfaces {

    public interface AllVideosListener {
        void getAllVideos(List<VideoInfo> allVideoList);
    }
    public interface AllAudiosListener {
        void getAllAudios(List<AudioInfo> allAudioList);
    }
    public interface VideosFolderListener {
        void getVideosFolder(Map<String, Integer> folderList);
    }
    public interface AlbumsListener {
        void getAlbums(Map<String, Integer> albumList);
    }
    public interface ArtistsListener {
        void getArtists(Map<String, Integer> artistsList);
    }

}
