package db.tables.game_result;

import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBGameResultItem extends DBTableItem<DBGameResult> {
    private DbColumnValue<Integer> idGameResult;
    private DbColumnValue<Integer> idGame;
    private DbColumnValue<Integer> idOpponent;
    private DbColumnValue<Integer> score;

    public DBGameResultItem() {
        super(new DBGameResult());
        DBGameResult table = getTable();

        idGameResult = new DbColumnValue<>(table.getIdGameResult());
        idGame = new DbColumnValue<>(table.getIdGame());
        idOpponent = new DbColumnValue<>(table.getIdOpponent());
        score = new DbColumnValue<>(table.getScore());
    }

    public DBGameResultItem(ResultSet rs) throws SQLException {
        super(new DBGameResult());
        DBGameResult table = getTable();

        int columnIndex = table.getIdGameResult().getIndex();
        idGameResult = new DbColumnValue<>(table.getIdGameResult());
        idGameResult.setValue(rs.getInt(columnIndex));

        columnIndex = table.getIdGame().getIndex();
        idGame = new DbColumnValue<>(table.getIdGame());
        idGame.setValue(rs.getInt(columnIndex));

        columnIndex = table.getIdOpponent().getIndex();
        idOpponent = new DbColumnValue<>(table.getIdOpponent());
        idOpponent.setValue(rs.getInt(columnIndex));

        columnIndex = table.getScore().getIndex();
        score = new DbColumnValue<>(table.getScore());
        score.setValue(rs.getInt(columnIndex));
    }

    public DbColumnValue<Integer> getIdGameResult() {
        return idGameResult;
    }

    public DbColumnValue<Integer> getIdGame() {
        return idGame;
    }

    public DbColumnValue<Integer> getIdOpponent() {
        return idOpponent;
    }

    public DbColumnValue<Integer> getScore() {
        return score;
    }
}
