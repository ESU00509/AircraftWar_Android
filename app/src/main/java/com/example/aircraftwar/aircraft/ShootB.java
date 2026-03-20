package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.HeroBullet;
import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class ShootB implements Strategy {
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int centerX = aircraft.getLocationX();
        int centerY = aircraft.getLocationY();
        BaseBullet bullet;

        for (int i = 0; i < 20; i++) {
            double angleDeg = i * (360.0 / 20);
            double angleRad = Math.toRadians(angleDeg);

            int speedX = (int) (10 * Math.cos(angleRad));
            int speedY = (int) (-10 * Math.sin(angleRad));

            if (aircraft instanceof HeroAircraft)
                bullet = new HeroBullet(centerX, centerY, speedX, speedY, aircraft.power);
            else
                bullet = new EnemyBullet(centerX, centerY, speedX, speedY, aircraft.power);
            res.add(bullet);
        }
        return res;
    }
}

