package gui.versus.games.game.types.duel;

import javafx.scene.paint.Color;

public enum ResultColor {
    WIN (Color.GREEN),
    DRAW (Color.YELLOW),
    LOSE (Color.DARKRED);

    private Color color;

    ResultColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
