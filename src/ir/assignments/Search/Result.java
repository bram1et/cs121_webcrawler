/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
*/
package ir.assignments.Search;

import java.util.List;

/**
 * Created by Chris on 2/28/16.
 */
public class Result {
    private String text;
    private String url;
    private String snippet;
    private boolean hasSubPages;

    public boolean isHasSubPages() {
        return hasSubPages;
    }

    public void setHasSubPages(boolean hasSubPages) {
        this.hasSubPages = hasSubPages;
    }

    public Result(String text, String url) {
        this.text = text;
        this.url = url;
        this.hasSubPages = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public List<Result> getSubPages() {
        return subPages;
    }

    public void setSubPages(List<Result> subPages) {
        this.subPages = subPages;
    }

    public Result(String text, String url, String snippet) {

        this.text = text;
        this.url = url;
        this.snippet = snippet;
        this.hasSubPages = false;
    }

    private List<Result> subPages;

}
