package db.table;

public class DbColumnValue<T> {
    private DBColumn<T> dbColumn;
    private T oldValue;
    private T value;

    public DbColumnValue(DBColumn<T> dbColumn) {
        this.dbColumn = dbColumn;
    }

    public DBColumn<T> getDbColumn() {
        return dbColumn;
    }

    public T getOldValue() {return oldValue; }
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.oldValue = oldValue != null ? this.value : value;
        this.value = value;
    }
}
