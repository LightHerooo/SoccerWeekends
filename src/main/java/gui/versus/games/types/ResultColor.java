package gui.versus.games.types;

import javafx.scene.paint.Color;

public enum ResultColor {
    WIN (Color.GREEN),
    DRAW (Color.LIGHTGOLDENRODYELLOW),
    LOSE (Color.DARKRED);

    private Color color;

    ResultColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
