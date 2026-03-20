package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.List;

public interface Enemy {
    void forward();
    List<BaseBullet> shoot();
    boolean crash(AbstractFlyingObject flyingObject);
    boolean notValid();
    void vanish();
    int getLocationX();
    int getLocationY();
    void decreaseHp(int decrease);
    int getWidth();
    int getHeight();
    void setStrategy(Strategy strategy);
    List<BaseBullet> executeStrategy(AbstractAircraft aircraft);
}

