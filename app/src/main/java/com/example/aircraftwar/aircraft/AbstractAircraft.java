package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.basic.AbstractFlyingObject;

import java.util.List;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {

    protected int maxHp;
    protected int hp;
    public int shootNum;
    public int power;
    public int direction;
    public Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<BaseBullet> executeStrategy(AbstractAircraft aircraft) {
        return strategy.shoot(aircraft);
    }

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }

    public void increaseHp(int increase) {
        hp = increase;
    }

    public abstract List<BaseBullet> shoot();
}

