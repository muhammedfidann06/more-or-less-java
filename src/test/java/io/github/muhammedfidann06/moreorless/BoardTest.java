package io.github.muhammedfidann06.moreorless;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void zufaelligeZiffernSindZwischenEinsUndNeun() {
        Board b = new Board(10, new SplittableRandom(7));
        for (int[] row : b.digits()) {
            for (int d : row) {
                assertTrue(d >= 1 && d <= 9);
            }
        }
    }

    @Test
    void benutztUndZuruecksetzen() {
        Board b = new Board(3, new SplittableRandom(1));
        Position p = new Position(2, 3);
        b.markUsed(p);
        assertTrue(b.isUsed(p));
        b.resetUsed();
        assertFalse(b.isUsed(p));
    }

    @Test
    void ungueltigeFelderWerdenAbgelehnt() {
        assertThrows(IllegalArgumentException.class, () -> new Board(new int[][]{{1, 2}, {3}}));
        assertThrows(IllegalArgumentException.class, () -> new Board(new int[][]{{0, 2}, {3, 4}}));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, 1));
    }

    @Test
    void speichernUndLaden(@TempDir Path dir) throws IOException {
        Board original = new Board(5, new SplittableRandom(3));
        Path file = dir.resolve("feld.txt");
        BoardStorage storage = new BoardStorage();

        storage.save(original, file);
        Board loaded = storage.load(file);

        assertEquals(5, loaded.size());
        assertArrayEquals(original.digits(), loaded.digits());
    }

    @Test
    void kaputteDateiGibtIOException(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("kaputt.txt");
        Files.writeString(file, "# Test\n12\n3x\n");
        assertThrows(IOException.class, () -> new BoardStorage().load(file));
    }
}
