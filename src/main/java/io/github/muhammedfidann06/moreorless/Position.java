package io.github.muhammedfidann06.moreorless;

/**
 * Eine Zelle auf dem Spielfeld. Zeile und Spalte beginnen bei 1 (wie im Spiel beschrieben).
 */
public record Position(int row, int col) {

    public Position {
        if (row < 1 || col < 1) {
            throw new IllegalArgumentException("Zeile und Spalte beginnen bei 1");
        }
    }
}
