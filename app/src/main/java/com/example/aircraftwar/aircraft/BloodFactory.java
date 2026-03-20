package com.example.aircraftwar.aircraft;

public class BloodFactory implements PropFactory {
    @Override
    public Prop createProp(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new Prop_Blood(locationX, locationY, speedX, speedY, hp);
    }
}

