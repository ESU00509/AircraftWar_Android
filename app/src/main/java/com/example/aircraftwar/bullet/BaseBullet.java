package com.example.aircraftwar.bullet;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.basic.AbstractFlyingObject;

/**
 * 子弹类
 *
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject {

    private int power = 10;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        if (locationX <= 0 || locationX >= Config.WINDOW_WIDTH) {
            vanish();
        }

        if (speedY > 0 && locationY >= Config.WINDOW_HEIGHT) {
            vanish();
        } else if (locationY <= 0) {
            vanish();
        }
    }

    public int getPower() {
        return power;
    }
}

