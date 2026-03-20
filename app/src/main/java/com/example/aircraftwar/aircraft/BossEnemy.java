package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft implements Enemy {

    private int shootNum = 20;
    private int power = 30;
    private int bulletSpeed = 10;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
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
        List<BaseBullet> res = new LinkedList<>();
        int centerX = this.getLocationX();
        int centerY = this.getLocationY();

        for (int i = 0; i < shootNum; i++) {
            double angleDeg = i * (360.0 / shootNum);
            double angleRad = Math.toRadians(angleDeg);

            int speedX = (int) (bulletSpeed * Math.cos(angleRad));
            int speedY = (int) (-bulletSpeed * Math.sin(angleRad));

            BaseBullet bullet = new EnemyBullet(centerX, centerY, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}

