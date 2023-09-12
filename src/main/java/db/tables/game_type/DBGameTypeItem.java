package db.tables.game_type;

import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBGameTypeItem extends DBTableItem<DBGameType> {
    private DbColumnValue<Integer> idGameType;
    private DbColumnValue<String> title;
    private DbColumnValue<String> imageName;

    public DBGameTypeItem() {
        super(new DBGameType());
        DBGameType table = getTable();

        idGameType = new DbColumnValue<>(table.getIdGameType());
        title = new DbColumnValue<>(table.getTitle());
        imageName = new DbColumnValue<>(table.getImageName());
    }

    public DBGameTypeItem(ResultSet rs) throws SQLException {
        super(new DBGameType());
        DBGameType table = getTable();

        int columnIndex = table.getIdGameType().getIndex();
        idGameType = new DbColumnValue<>(table.getIdGameType());
        idGameType.setValue(rs.getInt(columnIndex));

        columnIndex = table.getTitle().getIndex();
        title = new DbColumnValue<>(table.getTitle());
        title.setValue(rs.getString(columnIndex));

        columnIndex = table.getImageName().getIndex();
        imageName = new DbColumnValue<>(table.getImageName());
        imageName.setValue(rs.getString(columnIndex));
    }

    public DbColumnValue<Integer> getIdGameType() {
        return idGameType;
    }

    public DbColumnValue<String> getTitle() {
        return title;
    }
    public DbColumnValue<String> getImageName() {
        return imageName;
    }

    @Override
    public String toString() {
        return title.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        DBGameTypeItem item = (DBGameTypeItem) obj;
        return this.getIdGameType().getValue().intValue() == item.getIdGameType().getValue().intValue();
    }
}
