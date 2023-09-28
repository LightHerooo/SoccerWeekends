package db.tables.opponent;

import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOpponentItem extends DBTableItem<DBOpponent> {
    private DbColumnValue<Integer> idOpponent;
    private DbColumnValue<String> name;
    private DbColumnValue<String> imageName;

    public DBOpponentItem() {
        super(new DBOpponent());
        DBOpponent table = getTable();

        idOpponent = new DbColumnValue<>(table.getIdOpponent());
        name = new DbColumnValue<>(table.getName());
        imageName = new DbColumnValue<>(table.getImageName());
    }

    public DBOpponentItem(ResultSet rs) throws SQLException {
        super(new DBOpponent());
        DBOpponent table = getTable();

        int columnIndex = table.getIdOpponent().getIndex();
        idOpponent = new DbColumnValue<>(table.getIdOpponent());
        idOpponent.setValue(rs.getInt(columnIndex));

        columnIndex = table.getName().getIndex();
        name = new DbColumnValue<>(table.getName());
        name.setValue(rs.getString(columnIndex));

        columnIndex = table.getImageName().getIndex();
        imageName = new DbColumnValue<>(table.getImageName());
        imageName.setValue(rs.getString(columnIndex));
    }

    public DbColumnValue<Integer> getIdOpponent() {
        return idOpponent;
    }
    public DbColumnValue<String> getName() {
        return name;
    }
    public DbColumnValue<String> getImageName() {
        return imageName;
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        DBOpponentItem item = (DBOpponentItem) obj;
        return this.getIdOpponent().getValue().intValue() == item.getIdOpponent().getValue().intValue();
    }
}
