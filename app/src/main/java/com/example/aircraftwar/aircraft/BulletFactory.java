package com.example.aircraftwar.aircraft;

public class BulletFactory implements PropFactory {
    @Override
    public Prop createProp(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new Prop_Bullet(locationX, locationY, speedX, speedY, hp);
    }
}

