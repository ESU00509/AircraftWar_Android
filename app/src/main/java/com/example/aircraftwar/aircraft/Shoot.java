package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class Shoot implements Strategy {
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        if (aircraft instanceof MobEnemy) return new LinkedList<>();
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + aircraft.direction * 2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + aircraft.direction * 5;
        BaseBullet bullet;
        for (int i = 0; i < aircraft.shootNum; i++) {
            if (aircraft instanceof HeroAircraft) {
                bullet = new HeroBullet(x + (i * 2 - aircraft.shootNum + 1) * 10, y, speedX, speedY, aircraft.power);
            } else {
                bullet = new EnemyBullet(x + (i * 2 - aircraft.shootNum + 1) * 10, y, speedX, speedY, aircraft.power);
            }
            res.add(bullet);
        }
        return res;
    }
}

