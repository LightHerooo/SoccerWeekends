package db.tables.opponent;

import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOpponentItem extends DBTableItem<DBOpponent> {
    private DbColumnValue<Integer> idOpponent;
    private DbColumnValue<String> name;
    private DbColumnValue<String> nameImage;

    public DBOpponentItem() {
        super(new DBOpponent());
        DBOpponent table = getTable();

        idOpponent = new DbColumnValue<>(table.getIdOpponent());
        name = new DbColumnValue<>(table.getName());
        nameImage = new DbColumnValue<>(table.getNameImage());
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

        columnIndex = table.getNameImage().getIndex();
        nameImage = new DbColumnValue<>(table.getNameImage());
        nameImage.setValue(rs.getString(columnIndex));
    }

    public DbColumnValue<Integer> getIdOpponent() {
        return idOpponent;
    }
    public DbColumnValue<String> getName() {
        return name;
    }
    public DbColumnValue<String> getNameImage() {
        return nameImage;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
