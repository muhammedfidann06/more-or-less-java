package io.github.muhammedfidann06.moreorless;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Die Spielregeln von "More or less, less is more".
 *
 * <ul>
 *   <li>Erster Zug: Jeder Knopf ist erlaubt. Seine Ziffer wird "Current".</li>
 *   <li>Zweiter Zug: Erlaubt sind alle Zeilen ODER Spalten, deren Nummer durch Current teilbar ist.</li>
 *   <li>Danach: Erlaubt sind die Schnittpunkte – Zeile durch Previous und Spalte durch Current teilbar
 *       (oder umgekehrt).</li>
 *   <li>Nach jedem Zug: Previous = Current, Current = neue Ziffer, Summe += Current, Züge − 1.</li>
 * </ul>
 * Ziel: Die Summe soll am Ende so nah wie möglich am Zielwert sein.
 */
public class Game {

    private final Board board;
    private final GameSettings settings;

    private OptionalInt previous = OptionalInt.empty(); // am Anfang ungültig
    private OptionalInt current = OptionalInt.empty();
    private int sum;
    private int movesLeft;
    private final List<Position> history = new ArrayList<>();

    public Game(Board board, GameSettings settings) {
        if (board.size() != settings.size()) {
            throw new IllegalArgumentException("Feldgröße passt nicht zu den Einstellungen");
        }
        this.board = board;
        this.settings = settings;
        this.movesLeft = settings.moves();
    }

    // ---------- Regeln ----------

    /** Darf der Spieler diesen Knopf jetzt wählen? */
    public boolean isAllowed(Position p) {
        if (isOver() || !board.contains(p) || board.isUsed(p)) {
            return false;
        }
        if (current.isEmpty()) {
            return true; // erster Zug: alles erlaubt
        }
        int cur = current.getAsInt();
        if (previous.isEmpty()) {
            // zweiter Zug: Zeile ODER Spalte teilbar durch Current
            return p.row() % cur == 0 || p.col() % cur == 0;
        }
        int prev = previous.getAsInt();
        // Schnittpunkt der möglichen Zeilen und Spalten von Previous und Current
        return (p.row() % prev == 0 && p.col() % cur == 0)
                || (p.row() % cur == 0 && p.col() % prev == 0);
    }

    /** Alle Knöpfe, die gerade erlaubt sind. */
    public List<Position> allowedPositions() {
        List<Position> result = new ArrayList<>();
        for (int r = 1; r <= board.size(); r++) {
            for (int c = 1; c <= board.size(); c++) {
                Position p = new Position(r, c);
                if (isAllowed(p)) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    /** Einen Knopf wählen. Wirft eine Exception, wenn der Zug nicht erlaubt ist. */
    public void select(Position p) {
        if (!isAllowed(p)) {
            throw new IllegalStateException("Dieser Zug ist nicht erlaubt: " + p);
        }
        int value = board.digitAt(p);
        previous = current;
        current = OptionalInt.of(value);
        sum += value;
        movesLeft--;
        board.markUsed(p);
        history.add(p);
    }

    /** Spiel vorbei, wenn keine Züge mehr übrig sind. */
    public boolean isOver() {
        return movesLeft <= 0;
    }

    /** Es gibt noch Züge, aber keinen erlaubten Knopf mehr. */
    public boolean isStuck() {
        return !isOver() && allowedPositions().isEmpty();
    }

    /** Abweichung vom Ziel: positiv = zu viel, negativ = zu wenig. */
    public int deviation() {
        return sum - settings.target();
    }

    /** Wie würde die Summe aussehen, wenn man diesen Knopf wählt? (Hilfe in der UI) */
    public OptionalInt previewSum(Position p) {
        return isAllowed(p) ? OptionalInt.of(sum + board.digitAt(p)) : OptionalInt.empty();
    }

    /**
     * Vorschlag für einen guten Zug.
     * Idee: Pro restlichem Zug sollten wir ungefähr (Ziel − Summe) / Züge bekommen.
     * Wir nehmen den Knopf, dessen Ziffer diesem Wert am nächsten ist.
     * Bei Gleichstand bevorzugen wir Knöpfe, nach denen es noch viele Möglichkeiten gibt.
     */
    public Optional<Position> suggestMove() {
        if (isOver()) {
            return Optional.empty();
        }
        double ideal = (double) (settings.target() - sum) / movesLeft;
        return allowedPositions().stream()
                .min(Comparator.<Position>comparingDouble(p -> Math.abs(board.digitAt(p) - ideal))
                        .thenComparing(p -> -followUpCount(p)));
    }

    /** Wie viele Züge wären nach diesem Zug noch möglich? (einfacher Blick in die Zukunft) */
    private int followUpCount(Position p) {
        int prev = current.isEmpty() ? board.digitAt(p) : current.getAsInt();
        int cur = board.digitAt(p);
        int count = 0;
        for (int r = 1; r <= board.size(); r++) {
            for (int c = 1; c <= board.size(); c++) {
                Position q = new Position(r, c);
                if (q.equals(p) || board.isUsed(q)) {
                    continue;
                }
                boolean ok = current.isEmpty()
                        ? r % cur == 0 || c % cur == 0
                        : (r % prev == 0 && c % cur == 0) || (r % cur == 0 && c % prev == 0);
                if (ok) {
                    count++;
                }
            }
        }
        return count;
    }

    // ---------- Getter ----------

    public Board board() {
        return board;
    }

    public GameSettings settings() {
        return settings;
    }

    public int sum() {
        return sum;
    }

    public int movesLeft() {
        return movesLeft;
    }

    public OptionalInt previous() {
        return previous;
    }

    public OptionalInt current() {
        return current;
    }

    public List<Position> history() {
        return List.copyOf(history);
    }
}
