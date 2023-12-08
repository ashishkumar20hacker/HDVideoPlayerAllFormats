package com.hdvideo.allformats.player.Models;

public class VideoInfo {
    private String name;
    private double sizeInMB;
    private String path;

    public VideoInfo(String name, double sizeInMB, String path) {
        this.name = name;
        this.sizeInMB = sizeInMB;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public double getSizeInMB() {
        return sizeInMB;
    }

    public String getPath() {
        return path;
    }
}