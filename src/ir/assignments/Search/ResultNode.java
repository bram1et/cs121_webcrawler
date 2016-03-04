package ir.assignments.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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
    private PriorityQueue<ResultNode> subPages;

    public void addToSubPages(ResultNode subPage) {
        if (this.subPages == null) {
            this.subPages = new PriorityQueue<ResultNode>();
        }
        this.subPages.add(subPage);
    }

    public void printSubPages() {
        if (this.subPages != null && !this.subPages.isEmpty()) {
            for (int i=0; i < 4; i++) {
                ResultNode rn = subPages.poll();
                System.out.println("\t" + rn.getUrl());
                if (subPages.isEmpty()) break;
            }
        }
    }

    public boolean hasSubPages() {
        return this.subPages != null && !subPages.isEmpty();
    }

    public List<ResultNode> getSubPages() {
        List<ResultNode> subPagesList = new ArrayList<ResultNode>();
        for (int i=0; i < 4; i++) {
            ResultNode rn = subPages.poll();
            subPagesList.add(rn);
            if (subPages.isEmpty()) break;
        }
        return subPagesList;

    }
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

    public void incrementSearchSCore(double score) {
        this.searchScore += score;
    }

    public void setSearchScore(double searchScore) {
        this.searchScore = searchScore;
    }

    public int compareTo(ResultNode resultNode2) {
        return Double.compare(resultNode2.getSearchScore(), this.getSearchScore());
    }
}
