package io.github.muhammedfidann06.moreorless;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Speichert ein Spielfeld als Textdatei und lädt es wieder.
 *
 * Format: eine Zeile pro Feldzeile, Ziffern ohne Trennzeichen. Zeilen mit # sind Kommentare.
 * <pre>
 * # More or Less
 * 3719
 * 2486
 * ...
 * </pre>
 */
public class BoardStorage {

    public void save(Board board, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# More or Less - Spielfeld " + board.size() + "x" + board.size());
        for (int[] row : board.digits()) {
            StringBuilder sb = new StringBuilder();
            for (int d : row) {
                sb.append(d);
            }
            lines.add(sb.toString());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public Board load(Path file) throws IOException {
        List<int[]> rows = new ArrayList<>();
        for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // leere Zeilen und Kommentare überspringen
            }
            int[] row = new int[line.length()];
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch < '1' || ch > '9') {
                    throw new IOException("Ungültiges Zeichen '" + ch + "' in Zeile: " + line);
                }
                row[i] = ch - '0';
            }
            rows.add(row);
        }
        try {
            return new Board(rows.toArray(int[][]::new));
        } catch (IllegalArgumentException e) {
            // Für den Aufrufer ist das ein Dateifehler.
            throw new IOException("Ungültiges Spielfeld: " + e.getMessage(), e);
        }
    }
}
