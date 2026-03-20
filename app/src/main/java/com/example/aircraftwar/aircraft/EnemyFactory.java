package com.example.aircraftwar.aircraft;

public interface EnemyFactory {
    Enemy createEnemy(int locationX, int locationY, int speedX, int speedY, int hp);
}

