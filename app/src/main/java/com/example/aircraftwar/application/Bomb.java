package com.example.aircraftwar.application;

import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.ArrayList;
import java.util.List;

public class Bomb {
    private List<BombObserve> bombObserverList = new ArrayList<>();

    public void addBoomObserver(BombObserve bombObserver) {
        bombObserverList.add(bombObserver);
    }

    public void removeBoomObserver(BombObserve bombObserver) {
        bombObserverList.remove(bombObserver);
    }

    public int notifyObservers() {
        int score = 0;
        for (BombObserve bombObserver : bombObserverList) {
            if (bombObserver.notValid()) {
                continue;
            }
            bombObserver.update(bombObserver);
            if (bombObserver.notValid() && !(bombObserver instanceof EnemyBullet)) {
                score += 10;
            }
        }
        bombObserverList.clear();
        return score;
    }

    public int boomPlay() {
        return notifyObservers();
    }
}

