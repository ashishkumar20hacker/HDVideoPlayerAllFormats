package com.hdvideo.allformats.player.Models;

public class ThemesModal {

    int selected_image;
    int unselected_image;

    public ThemesModal(int selected_image, int unselected_image) {
        this.selected_image = selected_image;
        this.unselected_image = unselected_image;
    }

    public int getSelected_image() {
        return selected_image;
    }

    public void setSelected_image(int selected_image) {
        this.selected_image = selected_image;
    }

    public int getUnselected_image() {
        return unselected_image;
    }

    public void setUnselected_image(int unselected_image) {
        this.unselected_image = unselected_image;
    }
}
