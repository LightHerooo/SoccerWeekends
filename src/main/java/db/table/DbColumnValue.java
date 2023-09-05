package db.table;

public class DbColumnValue<T> {
    private DBColumn<T> dbColumn;
    private T value;

    public DbColumnValue(DBColumn<T> dbColumn) {
        this.dbColumn = dbColumn;
    }

    public DBColumn<T> getDbColumn() {
        return dbColumn;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
