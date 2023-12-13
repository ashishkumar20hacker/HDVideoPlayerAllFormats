package com.hdvideo.allformats.player.Models;

import java.util.List;

public class AudioPlaylistModel {
    private String playlistName;
    private List<AudioInfo> audioList;

    public AudioPlaylistModel() {
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public List<AudioInfo> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<AudioInfo> audioList) {
        this.audioList = audioList;
    }
}
