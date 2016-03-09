/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
*/
package ir.assignments.Search;

import java.util.List;

public class ResultsWrapper {
    public long getNanoTime() {
        return nanoTime;
    }

    public void setNanoTime(long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSpellCorrection() {
        return spellCorrection;
    }

    public void setSpellCorrection(String spellCorrection) {
        this.spellCorrection = spellCorrection;
    }

    public boolean isSpellCorrected() {
        return spellCorrected;
    }

    public void setSpellCorrected(boolean spellCorrected) {
        this.spellCorrected = spellCorrected;
    }

    private long nanoTime;
    private List<Result> results;
    private String query;
    private String spellCorrection;
    private boolean spellCorrected;


}
