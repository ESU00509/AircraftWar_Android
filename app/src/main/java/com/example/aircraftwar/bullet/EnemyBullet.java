package com.example.aircraftwar.bullet;

import com.example.aircraftwar.application.BombObserve;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet implements BombObserve {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    public void update(BombObserve observer) {
        observer.vanish();
    }

    public void decreaseHp(int decrease) {
    }
}

