package com.redhat.onetapsoccer;

public class Event {

    private String kind;
    private String player;
    private Integer score;
    private String user;

    public Event(String kind, String player, Integer score, String user) {
        this.kind = kind;
        this.player = player;
        this.score = score;
        this.user = user;
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
}
