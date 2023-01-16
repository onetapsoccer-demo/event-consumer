package com.redhat.onetapsoccer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Event {

    private String kind;
    private String player;
    private Integer score;
    private String user;
    private String userName;

    public Event(String kind, String player, Integer score, String user, String userName) {
        this.kind = kind;
        this.player = player;
        this.score = score;
        this.user = user;
        this.userName = userName;
    }

    public String getUser() {
        return user;
    }

    public String getKind() {
        return kind;
    }

    public String getPlayer() {
        return player;
    }

    public Integer getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }
}
