package ir.assignments.helpers;

/**
 * Created by Chris on 2/12/16.
 */
public class PostingsEntry {
    private int urlHashCode;
    private int tfidf;
    private String url;

    public PostingsEntry(int urlHashCode, int tfidf, String url) {
        this.url = url;
        this.urlHashCode = urlHashCode;
        this.tfidf = tfidf;
    }

    public void setUrlHashCode(int urlHashCode) {
        this.urlHashCode = urlHashCode;
    }

    public void setTfidf(int tfidf) {
        this.tfidf = tfidf;
    }

    public int getUrlHashCode() {
        return urlHashCode;
    }

    public int getTfidf() {
        return tfidf;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PostingsEntry{" +
                "urlHashCode=" + urlHashCode +
                ", tfidf=" + tfidf +
                ", url='" + url + '\'' +
                '}';
    }
}
