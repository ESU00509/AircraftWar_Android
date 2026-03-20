package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.application.BombObserve;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机，不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft implements Enemy, BombObserve {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Config.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public void update(BombObserve observer) {
        observer.vanish();
    }
}

