package com.firebaseplayer.model;

/**
 * Created by Yudiz on 23/08/16.
 */
public class NavItem {
    private String title;
    private int selectedImageId;


    public NavItem(String title, int selectedImageId) {
        this.title = title;
        this.selectedImageId = selectedImageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSelectedImageId() {
        return selectedImageId;
    }

    public void setSelectedImageId(int selectedImageId) {
        this.selectedImageId = selectedImageId;
    }

}
