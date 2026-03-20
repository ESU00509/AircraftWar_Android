package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.basic.AbstractFlyingObject;

public interface Prop {
    void forward();
    boolean crash(AbstractFlyingObject flyingObject);
    boolean notValid();
    void vanish();
}

