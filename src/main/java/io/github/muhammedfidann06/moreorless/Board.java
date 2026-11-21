package io.github.muhammedfidann06.moreorless;

import java.util.Arrays;
import java.util.random.RandomGenerator;

/**
 * Das Spielfeld: n × n Knöpfe mit Ziffern von 1 bis 9.
 * Ein Knopf kann nur einmal gewählt werden – danach ist er inaktiv.
 */
public class Board {

    private final int size;
    private final int[][] digits;
    private final boolean[][] used;

    /** Neues Feld mit zufälligen Ziffern. */
    public Board(int size, RandomGenerator random) {
        this(randomDigits(size, random));
    }

    /** Feld mit festen Ziffern (z. B. aus einer Datei oder für Tests). */
    public Board(int[][] digits) {
        if (digits.length < 2) {
            throw new IllegalArgumentException("Das Feld braucht mindestens 2 × 2 Knöpfe");
        }
        this.size = digits.length;
        this.digits = new int[size][];
        for (int r = 0; r < size; r++) {
            if (digits[r].length != size) {
                throw new IllegalArgumentException("Das Feld muss quadratisch sein");
            }
            for (int d : digits[r]) {
                if (d < 1 || d > 9) {
                    throw new IllegalArgumentException("Nur Ziffern von 1 bis 9 sind erlaubt: " + d);
                }
            }
            // Kopie, damit niemand das Feld von außen ändern kann.
            this.digits[r] = digits[r].clone();
        }
        this.used = new boolean[size][size];
    }

    private static int[][] randomDigits(int size, RandomGenerator random) {
        int[][] d = new int[size][size];
        for (int[] row : d) {
            for (int c = 0; c < size; c++) {
                row[c] = random.nextInt(1, 10); // 1 bis 9
            }
        }
        return d;
    }

    public int size() {
        return size;
    }

    public int digitAt(Position p) {
        return digits[p.row() - 1][p.col() - 1];
    }

    public boolean isUsed(Position p) {
        return used[p.row() - 1][p.col() - 1];
    }

    public void markUsed(Position p) {
        used[p.row() - 1][p.col() - 1] = true;
    }

    /** Für "Rückgängig": Knopf wieder aktiv machen. */
    public void markUnused(Position p) {
        used[p.row() - 1][p.col() - 1] = false;
    }

    public boolean contains(Position p) {
        return p.row() <= size && p.col() <= size;
    }

    /** Alle Knöpfe wieder aktiv machen (Neustart mit dem gleichen Feld). */
    public void resetUsed() {
        for (boolean[] row : used) {
            Arrays.fill(row, false);
        }
    }

    /** Kopie der Ziffern – zum Speichern. */
    public int[][] digits() {
        int[][] copy = new int[size][];
        for (int r = 0; r < size; r++) {
            copy[r] = digits[r].clone();
        }
        return copy;
    }
}
