package io.kavenegar.android.sample.standalone.models;


import com.google.gson.JsonObject;

import java.io.Serializable;


public class Endpoint implements Serializable {

    Integer id;


    String username;

    String fullName;

    String accessToken;

    public Endpoint() {
    }


    public Endpoint(JsonObject json) {
        if (json.has("displayName")) {
            this.fullName = json.get("displayName").getAsString();
        }

        this.id = json.get("id").getAsInt();
        if (json.has("username")) {
            this.username = json.get("username").getAsString();
        }

        this.accessToken = json.get("accessToken").getAsString();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
