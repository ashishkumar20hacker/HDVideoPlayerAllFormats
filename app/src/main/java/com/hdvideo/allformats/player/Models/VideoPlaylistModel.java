package com.hdvideo.allformats.player.Models;

import java.util.List;

public class VideoPlaylistModel {
    private String playlistName;
    private List<VideoInfo> videoList;

 /*   public VideoPlaylistModel(String playlistName, List<VideoInfo> videoList) {
        this.playlistName = playlistName;
        this.videoList = videoList;
    }*/

    public VideoPlaylistModel() {
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public List<VideoInfo> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoInfo> videoList) {
        this.videoList = videoList;
    }
}