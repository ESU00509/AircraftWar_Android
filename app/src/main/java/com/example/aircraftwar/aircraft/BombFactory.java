package com.example.aircraftwar.aircraft;

public class BombFactory implements PropFactory {
    @Override
    public Prop createProp(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new Prop_Bomb(locationX, locationY, speedX, speedY, hp);
    }
}

