module main.soccerweekends {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    //opens main.soccerweekends to javafx.fxml;
    exports gui;
    opens gui;
    opens gui.versus;
    opens gui.versus.games;
    opens gui.versus.games.add_game;
    opens gui.versus.games.add_game.opponent_game_result;
    opens gui.versus.games.types.duel;
    opens gui.versus.games.types;

    opens opponent_images;
}