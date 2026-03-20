package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.application.BombObserve;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机，可以射击
 *
 * @author hitsz
 */
public class SuperEnemy extends AbstractAircraft implements Enemy, BombObserve {

    public SuperEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 1;
        this.power = 30;
        this.direction = 1;
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
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 3;
        BaseBullet bullet;
        for (int i = 0; i < shootNum; i++) {
            bullet = new EnemyBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    public void update(BombObserve observer) {
        observer.vanish();
    }
}

