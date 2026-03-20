package com.example.aircraftwar.application;

public class Data implements java.io.Serializable {
    public String playerName;
    public int score;
    public long time;

    public Data(String playerName, int score, long time) {
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public int getScore() {
        return this.score;
    }

    public String getName() {
        return this.playerName;
    }

    public long getTime() {
        return this.time;
    }
}

