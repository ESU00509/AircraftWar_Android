package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class ShootA implements Strategy {
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + aircraft.direction * 2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + aircraft.direction * 3;
        BaseBullet bullet;
        for (int i = 0; i < 3; i++) {
            if (i == 0) speedX = -2;
            else if (i == 2) speedX = 2;
            if (aircraft instanceof HeroAircraft) {
                bullet = new HeroBullet(x + (i * 2 - aircraft.shootNum + 1) * 10, y, speedX, speedY, aircraft.power);
            } else {
                bullet = new EnemyBullet(x + (i * 2 - aircraft.shootNum + 1) * 10, y, speedX, speedY, aircraft.power);
            }
            res.add(bullet);
            speedX = 0;
        }
        return res;
    }
}

