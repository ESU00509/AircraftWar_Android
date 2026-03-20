package com.example.aircraftwar.application;

public interface BombObserve {
    void update(BombObserve observer);
    void vanish();
    void decreaseHp(int decrease);
    boolean notValid();
}

