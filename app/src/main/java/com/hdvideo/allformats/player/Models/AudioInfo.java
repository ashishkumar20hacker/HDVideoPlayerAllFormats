package com.hdvideo.allformats.player.Models;

public class AudioInfo {
    private String path;
    private String name;
    private double sizeInMB;

    public AudioInfo(String path, String name, double sizeInMB) {
        this.path = path;
        this.name = name;
        this.sizeInMB = sizeInMB;
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