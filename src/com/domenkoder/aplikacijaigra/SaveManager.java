package com.domenkoder.aplikacijaigra;

import java.io.*;
import java.util.*;

public class SaveManager {

    private static final String SAVE_FILE = "rezultati.txt";

    // Razred za rezultat: raven, točke, datum
    public static class Result {

        int level, score;
        String date;

        Result(int level, int score, String date) {
            this.level = level;
            this.score = score;
            this.date = date;
        }

        @Override
        public String toString() {
            return String.format("%d\t%d\t%s", level, score, date);
        }
    }

    // sharni nov rezultat
    public static void saveResult(int level, int score) {
        String date = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE, true))) {
            out.println(level + "\t" + score + "\t" + date);
        } catch (IOException e) {
            System.err.println("Napaka pri shranjevanju: " + e.getMessage());
        }
    }

    // PREBERI IN RAZVRSTI VSE REZULTATE (po točkah padajoče)
    public static List<Result> getAllResultsSorted() {
        List<Result> results = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 3) {
                    int level = Integer.parseInt(parts[0]);
                    int score = Integer.parseInt(parts[1]);
                    String date = parts[2];
                    results.add(new Result(level, score, date));
                }
            }
        } catch (IOException e) {
            // Datoteka še ne obstaja
        }

        // RAZVRSTI po točkah (največje na vrhu)
        results.sort((r1, r2) -> Integer.compare(r2.score, r1.score));
        return results;
    }
}
