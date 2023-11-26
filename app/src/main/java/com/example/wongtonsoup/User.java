package com.example.wongtonsoup;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String UID;
    private String joined;
    private String name;

    public User(String UID, String joined, String name) {
        this.UID = UID;
        this.joined = joined;
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
//        result.put("UID", UID);
        result.put("joined", joined);
        result.put("name", name);
        return result;
    }

}
