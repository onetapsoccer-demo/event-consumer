package com.redhat.onetapsoccer;

public class Event {

    private String kind;
    private String player;
    private Integer score;

    public Event(String kind, String player, Integer score) {
        this.kind = kind;
        this.player = player;
        this.score = score;
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
