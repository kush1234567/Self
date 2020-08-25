package com.bawp.self.Util;

import android.app.Application;
//we have make it singleton class
//we want to use this in complete application so go in manifest file and add it in application
public class journalApi extends Application {
    private String username;
    private  String userId;
    private static journalApi instance;
    public static journalApi getInstance()
    {
        if(instance==null)
        {
            instance=new journalApi();
        }
        return instance;
    }
    public journalApi()
    {

    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
