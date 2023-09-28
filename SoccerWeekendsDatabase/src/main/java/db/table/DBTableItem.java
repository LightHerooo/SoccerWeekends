package db.table;

public abstract class DBTableItem<T extends DBTable> {
    private T table;

    public DBTableItem(T table) {
        this.table = table;
    }

    public T getTable() {
        return table;
    }
}
