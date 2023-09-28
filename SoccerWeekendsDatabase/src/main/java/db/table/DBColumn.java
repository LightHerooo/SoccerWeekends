package db.table;

public class DBColumn<T> {
    private String columnName;
    private int index;

    public DBColumn(String columnName, int index) {
        this.columnName = columnName;
        this.index = index;
    }

    public String getColumnName() {
        return columnName;
    }
    public int getIndex() {
        return index;
    }
}
