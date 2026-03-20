package com.example.aircraftwar.aircraft;

public class MobFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }
}

