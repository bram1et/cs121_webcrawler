/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
Namirud Yegezu 26447410
Kevin Chen 49859223
*/
package ir.assignments.helpers;

public class LoadingProgressTracker {
    private Integer count;
    private Integer progressCount;
    private Integer totalCount;
    private String thingBeingLoaded;

    public LoadingProgressTracker(Integer totalCount, String thingBeingLoaded) {
        this.count = 0;
        this.progressCount = 0;
        this.totalCount = totalCount;
        this.thingBeingLoaded = thingBeingLoaded;
        this.initializeProgressBar();
    }

    public LoadingProgressTracker(Integer count, Integer progressCount, Integer totalCount) {
        this.count = count;
        this.progressCount = progressCount;
        this.totalCount = totalCount;
        this.initializeProgressBar();
    }

    private void initializeProgressBar() {
        System.out.println("|--------------------------------------------------| 100%");
        System.out.print("|");
    }

    public void printFinished() {
        System.out.println("| " + thingBeingLoaded + " Loaded");
    }

    public void incrementProgress() {
        this.count += 1;
        if((100 * this.count/ this.totalCount) > this.progressCount) {
            System.out.print("-");
            this.progressCount += 2;
        }
    }

    public Integer getProgressCount() {
        return progressCount;
    }

    public void setProgressCount(Integer progressCount) {
        this.progressCount = progressCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
