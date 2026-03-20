package com.example.aircraftwar.aircraft;

public class SuperFactory implements EnemyFactory {
    public Enemy createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new SuperEnemy(locationX, locationY, speedX, speedY, hp);
    }
}

