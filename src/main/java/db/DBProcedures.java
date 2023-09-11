package db;

public enum DBProcedures {
    GetGamesBetweenOpponents ("GetGamesBetweenOpponents(?)"),
    GetGameRating ("GetGameRating(?)");

    private String query;

    DBProcedures(String query) {
        this.query = "{ call " + query + "}";
    }

    public String getQuery() {
        return query;
    }
}
