package ir.assignments.Search;

/**
 * Created by Chris on 2/13/16.
 */
public class ResultNode implements Comparable<ResultNode>{
    @Override
    public String toString() {
        return "ResultNode{" +
                "searchScore=" + searchScore +
                ", url='" + url + '\'' +
                '}';
    }

    private String urlHashCode;
    private String url;
    private double searchScore;

    public ResultNode(String urlHashCode, String url, double searchScore) {
        this.urlHashCode = urlHashCode;
        this.url = url;
        this.searchScore = searchScore;
    }

    public String getUrlHashCode() {
        return urlHashCode;
    }

    public void setUrlHashCode(String urlHashCode) {
        this.urlHashCode = urlHashCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getSearchScore() {
        return searchScore;
    }

    public void setSearchScore(double searchScore) {
        this.searchScore = searchScore;
    }

    public int compareTo(ResultNode resultNode2) {
        return Double.compare(resultNode2.getSearchScore(), this.getSearchScore());
    }
}
