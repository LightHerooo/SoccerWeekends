package db.tables.game;

import db.table.DBTableItem;
import db.table.DbColumnValue;
import db.tables.game_type.DBGameType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DBGameItem extends DBTableItem<DBGame> {
    private DbColumnValue<Integer> idGame;
    private DbColumnValue<Integer> idGameType;
    private DbColumnValue<Date> idGameDate;

    public DBGameItem() {
        super(new DBGame());
        DBGame table = getTable();

        idGame = new DbColumnValue<>(table.getIdGame());
        idGameType = new DbColumnValue<>(table.getIdGameType());
        idGameDate = new DbColumnValue<>(table.getGameDate());
    }

    public DBGameItem(ResultSet rs) throws SQLException {
        super(new DBGame());
        DBGame table = getTable();

        int columnIndex = table.getIdGame().getIndex();
        idGame = new DbColumnValue<>(table.getIdGame());
        idGame.setValue(rs.getInt(columnIndex));

        columnIndex = table.getIdGameType().getIndex();
        idGameType = new DbColumnValue<>(table.getIdGameType());
        idGameType.setValue(rs.getInt(columnIndex));

        columnIndex = table.getGameDate().getIndex();
        idGameDate = new DbColumnValue<>(table.getGameDate());
        idGameDate.setValue(rs.getDate(columnIndex));
    }

    public DbColumnValue<Integer> getIdGame() {
        return idGame;
    }

    public DbColumnValue<Integer> getIdGameType() {
        return idGameType;
    }

    public DbColumnValue<Date> getGameDate() {
        return idGameDate;
    }
}
