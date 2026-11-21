package io.github.muhammedfidann06.moreorless;

import io.github.muhammedfidann06.moreorless.ui.GameFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.random.RandomGenerator;

/** Startpunkt: Das Fenster muss im Swing-Thread (EDT) erstellt werden. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Dann eben das Standard-Aussehen.
            }
            new GameFrame(RandomGenerator.getDefault()).setVisible(true);
        });
    }
}
