package com.example.aircraftwar.aircraft;

public class PlusFactory implements EnemyFactory {
    public Enemy createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new PlusEnemy(locationX, locationY, speedX, speedY, hp);
    }
}

