package ir.assignments.three.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Chris on 2/1/16.
 */

public class LogChecker {
    private static void getLongestText() {
        String logFolder = "logs";
        String fileName = "";
        File dir = new File(logFolder);
        File[] directoryListing = dir.listFiles();
        float numFiles =  directoryListing.length;
        Integer fileCount = 0;
        Integer maxLength = 0;
        String maxURL = "";
        if (directoryListing != null) {
            for (File logFile : directoryListing) {
                fileName = logFile.toString();
                if (logFile.isFile() && fileName.contains("log.txt") && !fileName.contains(".DS_Store")) {
                    try {
                        Scanner sc = new Scanner(logFile);
                        while (sc.hasNext()) {
                            String url = sc.nextLine().split(" ")[1];
                            Integer textLength = Integer.parseInt(sc.nextLine().split(":")[1].trim());
                            String urls = sc.nextLine();
                            if (textLength > maxLength && !url.contains(".csv")) {
                                maxLength = textLength;
                                maxURL = url;
                            }

                        }
                    } catch (FileNotFoundException e) {
                        System.err.println(e.toString());
                    }
                }
            }
        }

        System.out.println(maxURL + " " + maxLength);
    }

    public List<String> getURLsFromLogs() {
        List<String> urls = new ArrayList<String>();
        String logFolder = "logs";
        String fileName = "";
        File dir = new File(logFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File logFile : directoryListing) {
                fileName = logFile.toString();
                if (logFile.isFile() && fileName.contains("log.txt") && !fileName.contains(".DS_Store")) {
                    try {
                        Scanner sc = new Scanner(logFile);
                        while (sc.hasNext()) {
                            String url = sc.nextLine().split(" ")[1];
                            String textLength = sc.nextLine();
                            String numURLs = sc.nextLine();
                            urls.add(url);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println(e.toString());
                    }
                }
            }
        }
        return urls;
    }

    public static void main(String[] args) {
        getLongestText();
    }
}
