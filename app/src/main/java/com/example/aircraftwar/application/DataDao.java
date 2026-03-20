package com.example.aircraftwar.application;

import java.util.List;

public interface DataDao {
    void doAdd(Data newdata);
    void dodelete(int index);
    List<Data> getAll();
}

