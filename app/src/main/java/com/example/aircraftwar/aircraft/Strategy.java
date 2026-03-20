package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.BaseBullet;

import java.util.List;

public interface Strategy {
    List<BaseBullet> shoot(AbstractAircraft aircraft);
}

