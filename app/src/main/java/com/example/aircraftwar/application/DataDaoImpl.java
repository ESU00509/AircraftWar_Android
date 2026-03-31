package com.example.aircraftwar.application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataDaoImpl implements DataDao {
    private static final String DEFAULT_DATA_FILE = "game_data.dat";
    private static final String EASY_DATA_FILE = "game_data_easy.dat";
    private static final String COMMON_DATA_FILE = "game_data_common.dat";
    private static final String INFERNO_DATA_FILE = "game_data_inferno.dat";

    private List<Data> database;
    private File dataFile;

    /**
     * @param filesDir 应用内部存储目录，如 context.getFilesDir()
     */
    public DataDaoImpl(File filesDir) {
        this(filesDir, DEFAULT_DATA_FILE);
    }

    /**
     * @param filesDir 应用内部存储目录，如 context.getFilesDir()
     * @param difficulty 难度：0=EASY, 1=COMMON, 2=INFERNO
     */
    public DataDaoImpl(File filesDir, int difficulty) {
        this(filesDir, resolveDataFileNameByDifficulty(difficulty));
    }

    private DataDaoImpl(File filesDir, String fileName) {
        database = new ArrayList<>();
        dataFile = new File(filesDir, fileName);
        loadFromFile();
    }

    private static String resolveDataFileNameByDifficulty(int difficulty) {
        switch (difficulty) {
            case 1:
                return COMMON_DATA_FILE;
            case 2:
                return INFERNO_DATA_FILE;
            default:
                return EASY_DATA_FILE;
        }
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

