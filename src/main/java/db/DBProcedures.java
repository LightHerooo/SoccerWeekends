package db;

public enum DBProcedures {
    GetGamesBetweenOpponents ("GetGamesBetweenOpponents", 1),
    GetGameRating ("GetGameRating", 1),
    GetOpponentStats("GetOpponentStats", 1);

    private String query;

    DBProcedures(String name, int numberOfInParameters) {
        if (numberOfInParameters > 0) {
            this.query = String.format("{ call %s(%s) }", name, QueryUtils.getParametersStr(numberOfInParameters));
        } else {
            this.query = String.format("{ call %s }", name);
        }
    }

    public String getQuery() {
        return query;
    }
}
