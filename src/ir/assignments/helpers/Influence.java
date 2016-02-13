package ir.assignments.helpers;

/**
 * Created by Chris on 2/12/16.
 */
public class Influence {
    private double influence;
    private String urlHashCode;
    private double count;

    public Influence(String urlHashCode) {
        this.urlHashCode = urlHashCode;
        this.influence = 1.0;
        this.count = 1.0;

    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Influence{" +
                "influence=" + influence +
                ", urlHashCode=" + urlHashCode +
                ", count=" + count +
                '}';
    }

    public void incrementCount(double amount) {
        this.count += amount;
    }

    public double getInfluence() {
        return influence;
    }


    public void setInfluence(double influence) {
        this.influence = influence;
    }

    public String getUrlHashCode() {
        return urlHashCode;
    }

    public void setUrlHashCode(String urlHashCode) {
        this.urlHashCode = urlHashCode;
    }
}
