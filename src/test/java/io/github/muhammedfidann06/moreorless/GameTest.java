package io.github.muhammedfidann06.moreorless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    // Ein festes 4×4-Feld, damit die Tests immer gleich laufen.
    private static final int[][] FIELD = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 1, 2, 3},
            {4, 5, 6, 7},
    };

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(new Board(FIELD), new GameSettings(4, 3, 12));
    }

    @Test
    void ersterZugIstUeberallErlaubt() {
        assertEquals(16, game.allowedPositions().size());
    }

    @Test
    void zweiterZugZeileOderSpalteTeilbar() {
        game.select(new Position(1, 2)); // Ziffer 2
        // Zeilen 2 und 4 oder Spalten 2 und 4 – ohne den schon gewählten Knopf.
        assertTrue(game.isAllowed(new Position(2, 1)));
        assertTrue(game.isAllowed(new Position(3, 4)));
        assertFalse(game.isAllowed(new Position(3, 3)));
        assertFalse(game.isAllowed(new Position(1, 2)), "Knopf ist schon benutzt");
    }

    @Test
    void dritterZugNurSchnittpunkte() {
        game.select(new Position(1, 2)); // Current = 2
        game.select(new Position(2, 3)); // Previous = 2, Current = 7
        // Zeile durch 2 und Spalte durch 7 -> Spalte 7 gibt es nicht.
        // Zeile durch 7 und Spalte durch 2 -> Zeile 7 gibt es nicht. Also: kein Zug!
        assertTrue(game.allowedPositions().isEmpty());
        assertTrue(game.isStuck());
    }

    @Test
    void summeUndZuegeWerdenGezaehlt() {
        game.select(new Position(1, 1)); // 1
        game.select(new Position(2, 2)); // 6
        assertEquals(7, game.sum());
        assertEquals(1, game.movesLeft());
        assertEquals(1, game.previous().getAsInt());
        assertEquals(6, game.current().getAsInt());
        assertEquals(-5, game.deviation());
    }

    @Test
    void nachDemLetztenZugIstSchluss() {
        game.select(new Position(1, 1)); // 1
        game.select(new Position(3, 3)); // 2 (alles durch 1 teilbar)
        game.select(new Position(4, 4)); // 7 (Spalte 4 durch 2 teilbar)
        assertEquals(10, game.sum());
        assertTrue(game.isOver());
        assertFalse(game.isStuck());
        assertTrue(game.allowedPositions().isEmpty());
        assertThrows(IllegalStateException.class, () -> game.select(new Position(2, 2)));
    }

    @Test
    void rueckgaengigStelltAllesWiederHer() {
        game.select(new Position(1, 1)); // 1
        game.select(new Position(3, 3)); // 2
        game.undo();
        assertEquals(1, game.sum());
        assertEquals(2, game.movesLeft());
        assertTrue(game.previous().isEmpty());
        assertEquals(1, game.current().getAsInt());
        assertFalse(game.board().isUsed(new Position(3, 3)));
        assertTrue(game.isAllowed(new Position(3, 3)));
        game.undo();
        assertEquals(0, game.sum());
        assertTrue(game.current().isEmpty());
        assertFalse(game.canUndo());
        assertThrows(IllegalStateException.class, game::undo);
    }

    @Test
    void verbotenerZugWirftException() {
        game.select(new Position(1, 3)); // Current = 3
        assertThrows(IllegalStateException.class, () -> game.select(new Position(1, 1)));
    }

    @Test
    void vorschauZeigtNeueSumme() {
        game.select(new Position(1, 1)); // Summe 1
        assertEquals(7, game.previewSum(new Position(2, 2)).getAsInt());
    }

    @Test
    void vorschlagIstErlaubtUndPasstZumZiel() {
        // Ziel 12 in 3 Zügen -> ideal ungefähr 4 pro Zug.
        Position p = game.suggestMove().orElseThrow();
        assertTrue(game.isAllowed(p));
        assertEquals(4, game.board().digitAt(p));
    }

    @Test
    void vorschlagFuehrtNieZuVerbotenemZug() {
        // Ein ganzes Spiel nur mit Vorschlägen spielen.
        Board board = new Board(10, new SplittableRandom(42));
        Game g = new Game(board, new GameSettings(10, 10, 50));
        while (!g.isOver() && !g.isStuck()) {
            g.select(g.suggestMove().orElseThrow());
        }
        assertTrue(g.history().size() >= 1);
    }

    @Test
    void schwierigkeitBestimmtZuegeUndZiel() {
        SplittableRandom r = new SplittableRandom(1);
        for (Difficulty d : Difficulty.values()) {
            for (int i = 0; i < 50; i++) {
                GameSettings s = GameSettings.fromDifficulty(d, 10, r);
                assertEquals(d.moves(), s.moves());
                assertTrue(s.target() >= d.moves() && s.target() <= 9 * d.moves());
            }
        }
    }

    @Test
    void falscheEinstellungenWerdenAbgelehnt() {
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(1, 5, 10));
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(10, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Game(new Board(FIELD), new GameSettings(5, 3, 12)));
    }
}
