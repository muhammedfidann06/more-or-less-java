package io.github.muhammedfidann06.moreorless.ui;

import io.github.muhammedfidann06.moreorless.Game;
import io.github.muhammedfidann06.moreorless.Position;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Das Spielfeld als Raster aus Knöpfen.
 * Erlaubte Knöpfe sind hell, gewählte grau, der Vorschlag ist grün umrandet.
 */
class BoardPanel extends JPanel {

    private static final Color ALLOWED = new Color(0xFFF3C4);
    private static final Color NORMAL = new Color(0xF5F5F5);
    private static final Color USED = new Color(0xBDBDBD);
    private static final Color HINT = new Color(0x2E7D32);

    private final Game game;
    private final JButton[][] buttons;
    private Optional<Position> hint = Optional.empty();

    /**
     * @param onSelect wird aufgerufen, wenn der Spieler einen Knopf klickt
     * @param onHover  wird beim Darüberfahren aufgerufen (null = Maus hat das Feld verlassen)
     */
    BoardPanel(Game game, Consumer<Position> onSelect, Consumer<Position> onHover) {
        this.game = game;
        int n = game.board().size();
        setLayout(new GridLayout(n, n, 3, 3));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buttons = new JButton[n][n];

        for (int r = 1; r <= n; r++) {
            for (int c = 1; c <= n; c++) {
                Position p = new Position(r, c);
                JButton b = new JButton(String.valueOf(game.board().digitAt(p)));
                b.setFont(b.getFont().deriveFont(Font.BOLD, 18f));
                b.setFocusPainted(false);
                b.addActionListener(e -> onSelect.accept(p));
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        onHover.accept(p);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        onHover.accept(null);
                    }
                });
                buttons[r - 1][c - 1] = b;
                add(b);
            }
        }
        refresh();
    }

    void showHint(Optional<Position> hint) {
        this.hint = hint;
        refresh();
    }

    /** Farben und Zustände aller Knöpfe neu setzen. */
    void refresh() {
        int n = game.board().size();
        for (int r = 1; r <= n; r++) {
            for (int c = 1; c <= n; c++) {
                Position p = new Position(r, c);
                JButton b = buttons[r - 1][c - 1];
                boolean used = game.board().isUsed(p);
                boolean allowed = game.isAllowed(p);
                b.setEnabled(allowed);
                b.setBackground(used ? USED : allowed ? ALLOWED : NORMAL);
                b.setBorder(hint.filter(p::equals).isPresent()
                        ? BorderFactory.createLineBorder(HINT, 3)
                        : BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }
    }
}
