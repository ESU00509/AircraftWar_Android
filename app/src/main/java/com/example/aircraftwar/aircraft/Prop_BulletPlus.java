package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class Prop_BulletPlus extends AbstractAircraft implements Prop {
    public Prop_BulletPlus(int locationX, int locationY, int speedX, int speedY, int hp) {
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
}

