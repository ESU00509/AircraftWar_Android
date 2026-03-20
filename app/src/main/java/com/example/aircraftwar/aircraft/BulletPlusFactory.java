package com.example.aircraftwar.aircraft;

public class BulletPlusFactory implements PropFactory {
    @Override
    public Prop createProp(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new Prop_BulletPlus(locationX, locationY, speedX, speedY, hp);
    }
}

