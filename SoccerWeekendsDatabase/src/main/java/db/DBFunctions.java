package db;

public enum DBFunctions {
    GetNumberOfPlayersPlayingTheGame ("GetNumberOfPlayersPlayingTheGame", 1, 1);
    private String query;

    DBFunctions(String name, int numberOfInParameters, int numberOfOutParameters) {
        String str;
        if (numberOfInParameters > 0) {
            str = String.format("call %s(%s)", name, QueryUtils.getParametersStr(numberOfInParameters));
        } else {
            str = String.format("call %s", name);
        }

        if (numberOfOutParameters > 0) {
            this.query = String.format("{ %s = %s }", QueryUtils.getParametersStr(numberOfOutParameters), str);
        } else {
            this.query = String.format("{ %s }", str);
        }
    }

    public String getQuery() {
        return query;
    }
}
