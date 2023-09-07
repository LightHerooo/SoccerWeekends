package db;

public enum DBProcedures {
    GetGamesPlayedByTwoOpponents ("GetGamesPlayedByTwoOpponents(?, ?)"),
    GetGameRating ("GetGameRating(?)");

    private String query;

    DBProcedures(String query) {
        this.query = "{ call " + query + "}";
    }

    public String getQuery() {
        return query;
    }
}
