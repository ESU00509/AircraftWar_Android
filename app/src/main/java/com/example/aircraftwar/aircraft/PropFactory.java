package com.example.aircraftwar.aircraft;

public interface PropFactory {
    Prop createProp(int locationX, int locationY, int speedX, int speedY, int hp);
}

