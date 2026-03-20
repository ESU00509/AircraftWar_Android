package com.example.aircraftwar.application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataDaoImpl implements DataDao {
    private List<Data> database;
    private File dataFile;

    /**
     * @param filesDir 应用内部存储目录，如 context.getFilesDir()
     */
    public DataDaoImpl(File filesDir) {
        database = new ArrayList<>();
        dataFile = new File(filesDir, "game_data.dat");
        loadFromFile();
    }

    public void doAdd(Data newdata) {
        if (newdata == null) return;

        int newScore = newdata.getScore();
        int insertIndex = 0;

        while (insertIndex < database.size()) {
            Data current = database.get(insertIndex);
            if (current.getScore() < newScore) {
                break;
            }
            insertIndex++;
        }

        database.add(insertIndex, newdata);
        saveToFile();
    }

    public List<Data> getAll() {
        return database;
    }

    public void dodelete(int index) {
        if (index >= 0 && index < database.size()) {
            database.remove(index);
            saveToFile();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        if (!dataFile.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            database = (List<Data>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            database = new ArrayList<>();
        }
    }
}

