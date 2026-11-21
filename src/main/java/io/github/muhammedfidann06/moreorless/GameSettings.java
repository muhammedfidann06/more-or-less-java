package io.github.muhammedfidann06.moreorless;

import java.util.random.RandomGenerator;

/**
 * Einstellungen für ein Spiel: Feldgröße, Züge und Zielwert.
 */
public record GameSettings(int size, int moves, int target) {

    public static final int DEFAULT_SIZE = 10;

    public GameSettings {
        if (size < 2 || size > 20) {
            throw new IllegalArgumentException("Feldgröße muss zwischen 2 und 20 liegen");
        }
        if (moves < 1) {
            throw new IllegalArgumentException("Es braucht mindestens einen Zug");
        }
        if (target < 1) {
            throw new IllegalArgumentException("Der Zielwert muss positiv sein");
        }
    }

    /**
     * Einstellungen aus der Schwierigkeit berechnen.
     * Der Durchschnitt einer Ziffer 1–9 ist 5. Das Ziel liegt also bei ungefähr 5 × Züge,
     * plus/minus einen zufälligen Teil (je schwerer, desto mehr).
     */
    public static GameSettings fromDifficulty(Difficulty d, int size, RandomGenerator random) {
        int average = 5 * d.moves();
        int maxShift = (int) Math.round(average * d.spread());
        int shift = maxShift == 0 ? 0 : random.nextInt(-maxShift, maxShift + 1);
        // Das Ziel muss erreichbar sein: zwischen 1 × Züge und 9 × Züge.
        int target = Math.clamp(average + shift, d.moves(), 9 * d.moves());
        return new GameSettings(size, d.moves(), target);
    }
}
