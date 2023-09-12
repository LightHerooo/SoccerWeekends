module main.soccerweekends {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    //opens main.soccerweekends to javafx.fxml;
    exports gui;
    opens gui;
    opens gui.contents;
    opens gui.contents.games;
    opens gui.contents.games.game;
    opens gui.contents.games.add_game;
    opens gui.contents.games.add_game.opponent_game_result;
    opens gui.contents.games.game.types;
    opens gui.contents.games.game.types.duel;
    opens gui.contents.games.game.types.big_game;
    opens gui.contents.games.select_opponent;
    opens gui.contents.games.select_opponent.statistics;
    opens gui.contents.opponents;
    opens gui.contents.opponents.opponent;
    opens gui.contents.opponents.add_opponent;
    opens gui.contents.game_types;
    opens gui.contents.game_types.game_type;
    opens gui.contents.game_types.add_game_type;

    opens images.opponent;
    opens images.game_type;
    opens images.opponent_result;
}