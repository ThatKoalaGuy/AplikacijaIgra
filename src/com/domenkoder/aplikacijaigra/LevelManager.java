package com.domenkoder.aplikacijaigra;

public class LevelManager {

    public static final int TOTAL_LEVELS = 3;
    private static int currentLevel = 1;

    public static void nextLevel() {
        currentLevel++;
        if (currentLevel > TOTAL_LEVELS) {
            currentLevel = 1;
        }
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public static void setLevel(int level) {
        currentLevel = level;
    }

    public static void reset() {
        currentLevel = 1;
    }
}
