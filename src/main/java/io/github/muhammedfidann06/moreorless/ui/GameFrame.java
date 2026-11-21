package io.github.muhammedfidann06.moreorless.ui;

import io.github.muhammedfidann06.moreorless.Board;
import io.github.muhammedfidann06.moreorless.BoardStorage;
import io.github.muhammedfidann06.moreorless.Difficulty;
import io.github.muhammedfidann06.moreorless.Game;
import io.github.muhammedfidann06.moreorless.GameSettings;
import io.github.muhammedfidann06.moreorless.Position;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.OptionalInt;
import java.util.random.RandomGenerator;

/**
 * Hauptfenster: oben Ziel und Züge, in der Mitte das Feld, unten Summe und Hinweise.
 */
public class GameFrame extends JFrame {

    private final RandomGenerator random;
    private final BoardStorage storage = new BoardStorage();

    private Difficulty difficulty = Difficulty.MEDIUM;
    private int size = GameSettings.DEFAULT_SIZE;
    private Game game;
    private BoardPanel boardPanel;

    private final JLabel targetLabel = bigLabel();
    private final JLabel movesLabel = bigLabel();
    private final JLabel sumLabel = bigLabel();
    private final JLabel stateLabel = new JLabel(" ");
    private final JLabel infoLabel = new JLabel(" ");
    private final JCheckBoxMenuItem showHints = new JCheckBoxMenuItem("Zug vorschlagen");

    public GameFrame(RandomGenerator random) {
        super("More or Less – Less is More");
        this.random = random;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(createMenu());

        JPanel top = new JPanel(new GridLayout(1, 2));
        top.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        top.add(targetLabel);
        movesLabel.setHorizontalAlignment(JLabel.RIGHT);
        top.add(movesLabel);

        JPanel bottom = new JPanel(new GridLayout(3, 1));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        bottom.add(sumLabel);
        bottom.add(stateLabel);
        bottom.add(infoLabel);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);

        newGame(new Board(size, random), GameSettings.fromDifficulty(difficulty, size, random));
        setSize(620, 720);
        setLocationRelativeTo(null);
    }

    private static JLabel bigLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
        return l;
    }

    // ---------- Menü ----------

    private JMenuBar createMenu() {
        JMenu gameMenu = new JMenu("Spiel");
        gameMenu.add(item("Neues Spiel", () -> newGame(new Board(size, random),
                GameSettings.fromDifficulty(difficulty, size, random))));
        gameMenu.add(item("Neustart (gleiches Feld)", this::restart));
        gameMenu.addSeparator();
        gameMenu.add(item("Feld speichern …", this::saveBoard));
        gameMenu.add(item("Feld laden …", this::loadBoard));
        gameMenu.addSeparator();
        gameMenu.add(item("Beenden", this::dispose));

        JMenu settingsMenu = new JMenu("Einstellungen");
        settingsMenu.add(item("Feldgröße …", this::askSize));
        settingsMenu.add(item("Anzahl Züge …", this::askMoves));
        settingsMenu.add(item("Zielwert …", this::askTarget));
        settingsMenu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        for (Difficulty d : Difficulty.values()) {
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(d.label(), d == difficulty);
            rb.addActionListener(e -> {
                difficulty = d;
                newGame(new Board(size, random), GameSettings.fromDifficulty(d, size, random));
            });
            group.add(rb);
            settingsMenu.add(rb);
        }

        JMenu helpMenu = new JMenu("Hilfe");
        showHints.addActionListener(e -> updateView());
        helpMenu.add(showHints);
        helpMenu.add(item("Regeln", this::showRules));

        JMenuBar bar = new JMenuBar();
        bar.add(gameMenu);
        bar.add(settingsMenu);
        bar.add(helpMenu);
        return bar;
    }

    private JMenuItem item(String text, Runnable action) {
        JMenuItem i = new JMenuItem(text);
        i.addActionListener(e -> action.run());
        return i;
    }

    // ---------- Spielablauf ----------

    private void newGame(Board board, GameSettings settings) {
        game = new Game(board, settings);
        if (boardPanel != null) {
            remove(boardPanel);
        }
        boardPanel = new BoardPanel(game, this::onSelect, this::onHover);
        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        updateView();
    }

    private void restart() {
        game.board().resetUsed();
        newGame(game.board(), game.settings());
    }

    private void onSelect(Position p) {
        game.select(p);
        updateView();
        if (game.isOver()) {
            int dev = game.deviation();
            String quality = dev == 0 ? "Perfekt getroffen!" : "Abweichung: " + (dev > 0 ? "+" : "") + dev;
            JOptionPane.showMessageDialog(this, "Spiel vorbei. Summe " + game.sum() + ", Ziel "
                    + game.settings().target() + ".\n" + quality, "Ende", JOptionPane.INFORMATION_MESSAGE);
        } else if (game.isStuck()) {
            JOptionPane.showMessageDialog(this, "Kein Zug mehr möglich! Abweichung: " + game.deviation(),
                    "Keine Fortsetzung", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Hilfe: Beim Darüberfahren zeigen wir die neue Summe. */
    private void onHover(Position p) {
        if (p == null) {
            infoLabel.setText(" ");
            return;
        }
        OptionalInt preview = game.previewSum(p);
        infoLabel.setText(preview.isPresent()
                ? "Wenn du " + game.board().digitAt(p) + " wählst: neue Summe " + preview.getAsInt()
                : " ");
    }

    private void updateView() {
        targetLabel.setText("Ziel: " + game.settings().target());
        movesLabel.setText("Züge: " + game.movesLeft());
        sumLabel.setText("Summe: " + game.sum() + "   (Abweichung " + game.deviation() + ")");
        stateLabel.setText("Previous: " + text(game.previous()) + "   Current: " + text(game.current())
                + "   Schwierigkeit: " + difficulty.label());
        boardPanel.showHint(showHints.isSelected() ? game.suggestMove() : java.util.Optional.empty());
    }

    private String text(OptionalInt v) {
        return v.isPresent() ? String.valueOf(v.getAsInt()) : "–";
    }

    // ---------- Dialoge ----------

    private void askSize() {
        askNumber("Feldgröße (2–20):", size).ifPresent(n -> {
            try {
                GameSettings s = new GameSettings(n, game.settings().moves(), game.settings().target());
                size = n;
                newGame(new Board(n, random), s);
            } catch (IllegalArgumentException e) {
                error(e.getMessage());
            }
        });
    }

    private void askMoves() {
        askNumber("Anzahl Züge:", game.settings().moves()).ifPresent(n -> changeSettings(
                () -> new GameSettings(size, n, game.settings().target())));
    }

    private void askTarget() {
        askNumber("Zielwert:", game.settings().target()).ifPresent(n -> changeSettings(
                () -> new GameSettings(size, game.settings().moves(), n)));
    }

    private void changeSettings(java.util.function.Supplier<GameSettings> make) {
        try {
            GameSettings s = make.get();
            game.board().resetUsed();
            newGame(game.board(), s);
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private OptionalInt askNumber(String question, int current) {
        String input = JOptionPane.showInputDialog(this, question, current);
        if (input == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(input.strip()));
        } catch (NumberFormatException e) {
            error("Bitte eine ganze Zahl eingeben.");
            return OptionalInt.empty();
        }
    }

    private JFileChooser chooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Spielfeld (*.txt)", "txt"));
        return fc;
    }

    private void saveBoard() {
        JFileChooser fc = chooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                storage.save(game.board(), fc.getSelectedFile().toPath());
            } catch (IOException e) {
                error("Speichern fehlgeschlagen: " + e.getMessage());
            }
        }
    }

    private void loadBoard() {
        JFileChooser fc = chooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Board b = storage.load(fc.getSelectedFile().toPath());
                size = b.size();
                newGame(b, GameSettings.fromDifficulty(difficulty, size, random));
            } catch (IOException e) {
                error(e.getMessage());
            }
        }
    }

    private void showRules() {
        JOptionPane.showMessageDialog(this, """
                1. Erster Zug: Wähle einen beliebigen Knopf.
                2. Danach: Erlaubt sind Zeilen/Spalten, deren Nummer durch die Ziffer teilbar ist.
                3. Ab dem dritten Zug: nur Schnittpunkte von Previous und Current.
                4. Jede Ziffer kommt zur Summe dazu. Jeder Knopf geht nur einmal.
                Ziel: Am Ende soll die Summe so nah wie möglich am Zielwert sein.
                """, "Regeln", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}
