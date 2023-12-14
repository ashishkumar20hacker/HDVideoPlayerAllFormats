package com.hdvideo.allformats.player.Models;

public class AudioInfo {
    private long id;
    private String path;
    private String name;
    private double sizeInMB;

    public AudioInfo(long id, String path, String name, double sizeInMB) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.sizeInMB = sizeInMB;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public double getSizeInMB() {
        return sizeInMB;
    }
}