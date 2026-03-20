package com.example.aircraftwar.aircraft;

public class BossFactory implements EnemyFactory {
    public Enemy createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new BossEnemy(locationX, locationY, speedX, speedY, hp);
    }
}

