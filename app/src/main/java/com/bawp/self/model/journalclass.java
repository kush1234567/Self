package com.bawp.self.model;

import com.google.firebase.Timestamp;

public class journalclass {
   private String tile;
    private String thought;
    private String imageUrl;
    private String userId;
    private com.google.firebase.Timestamp timeadded;
    private String username;

    public journalclass() {
    }

    public journalclass(String tile, String thought, String imageUrl, String userId, com.google.firebase.Timestamp timeadded, String username) {
        this.tile = tile;
        this.thought = thought;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeadded = timeadded;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(Timestamp timeadded) {
        this.timeadded = timeadded;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }
}
