package com.hdvideo.allformats.player.Models;

public class VideoInfo {
    private long id;
    private String name;
    private double sizeInMB;
    private String path;

    public VideoInfo(long id, String name, double sizeInMB, String path) {
        this.id = id;
        this.name = name;
        this.sizeInMB = sizeInMB;
        this.path = path;
    }

    public long getId() {
        return id;
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