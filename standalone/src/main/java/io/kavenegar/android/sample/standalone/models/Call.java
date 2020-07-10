package io.kavenegar.android.sample.standalone.models;


import com.google.gson.JsonObject;

import org.threeten.bp.LocalDateTime;

import io.kavenegar.sdk.call.enums.CallFinishedReason;



public class Call {


    String id;
    LocalDateTime createdAt;
    LocalDateTime finishedAt;
    LocalDateTime startedAt;
    Endpoint caller;
    Endpoint receptor;
    String remoteId;
    CallFinishedReason endType;
    Integer duration;
    boolean logSent;

    public Call() {

    }


    public Call(JsonObject json) {
        this.id = json.get("id").getAsString();
        if (json.has("caller")) {
            this.caller = new Endpoint(json.get("caller").getAsJsonObject());
        }
        if (json.has("receptor")) {
            this.receptor = new Endpoint(json.get("receptor").getAsJsonObject());
        }
        if (json.has("startedAt")) {
            this.startedAt = LocalDateTime.parse(json.get("startedAt").getAsString());
        }
        if (json.has("finishedAt")) {
            this.finishedAt = LocalDateTime.parse(json.get("finishedAt").getAsString());
        }
        if (json.has("createdAt")) {
            this.createdAt = LocalDateTime.parse(json.get("createdAt").getAsString());
        }
        if (json.has("endType")) {
            this.endType = CallFinishedReason.find(json.get("endType").getAsString());
        } else {
            this.endType = CallFinishedReason.HANGUP;
        }

        if (json.has("duration")) {
            this.duration = json.get("duration").getAsInt();
        }

        if (json.has("remoteId")) {
            this.remoteId = json.get("remoteId").getAsString();
        }

        this.logSent = false;
    }


    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setLogSent(boolean logSent) {
        this.logSent = logSent;
    }

    public boolean isLogSent() {
        return logSent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public Endpoint getCaller() {
        return caller;
    }

    public void setCaller(Endpoint caller) {
        this.caller = caller;
    }

    public Endpoint getReceptor() {
        return receptor;
    }

    public void setReceptor(Endpoint receptor) {
        this.receptor = receptor;
    }

    public CallFinishedReason getEndType() {
        return endType;
    }

    public void setEndType(CallFinishedReason endType) {
        this.endType = endType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

}
