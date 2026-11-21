package io.github.muhammedfidann06.moreorless;

/**
 * Schwierigkeit: Sie bestimmt die Anzahl der Züge und wie "krumm" der Zielwert ist.
 * Leicht = viele Züge, Ziel nahe am Durchschnitt (5 pro Zug).
 * Schwer = wenige Züge, Ziel weit weg vom Durchschnitt.
 */
public enum Difficulty {
    EASY("Leicht", 15, 0.05),
    MEDIUM("Mittel", 10, 0.20),
    HARD("Schwer", 6, 0.35);

    private final String label;
    private final int moves;
    private final double spread; // wie weit das Ziel vom Durchschnitt weg sein darf

    Difficulty(String label, int moves, double spread) {
        this.label = label;
        this.moves = moves;
        this.spread = spread;
    }

    public String label() {
        return label;
    }

    public int moves() {
        return moves;
    }

    public double spread() {
        return spread;
    }

    @Override
    public String toString() {
        return label;
    }
}
