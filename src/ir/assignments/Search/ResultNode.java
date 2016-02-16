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
    private double anchorScore;
    private double tfidfScore;
    private double pageRankScore;

    public double getAnchorScore() {
        return anchorScore;
    }

    public void setAnchorScore(double anchorScore) {
        this.anchorScore = anchorScore;
    }

    public double getTfidfScore() {
        return tfidfScore;
    }

    public void setTfidfScore(double tfidfScore) {
        this.tfidfScore = tfidfScore;
    }

    public double getPageRankScore() {
        return pageRankScore;
    }

    public void setPageRankScore(double pageRankScore) {
        this.pageRankScore = pageRankScore;
    }

    public ResultNode(String urlHashCode, String url, double searchScore) {
        this.urlHashCode = urlHashCode;
        this.url = url;
        this.searchScore = searchScore;
        this.tfidfScore = 0.0;
        this.anchorScore = 0.0;
        this.pageRankScore = 0.0;
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
