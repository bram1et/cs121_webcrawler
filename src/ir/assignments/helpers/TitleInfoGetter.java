package ir.assignments.helpers;

import ir.assignments.basic.BasicCrawlController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Chris on 2/17/16.
 */
public class TitleInfoGetter {
    private static HashSet<String> getTitlesFromFile() {
        HashSet<String> hasTitleInfo = new HashSet<>();
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
//        String hasTitleInfoName = pathString + "title_info.txt";
        String hasTitleInfoName = pathString + "download_these_urls.txt";
        File hasTitleInfoFile = new File(hasTitleInfoName);
        Integer progressCount = 0;
        Integer lineCount = 0;
        String inputLine;
        String urlHashCode;
        String url;
        if (!hasTitleInfoFile.exists()) {
            System.err.println("Error opening hash_to_URL.txt");
            System.exit(1);
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(hasTitleInfoName));
//            System.out.println("|--------------------------------------------------| 100%");
//            System.out.print("|");
            while (((inputLine = fileReader.readLine()) != null)) {
                String[] splitLine = inputLine.split(" : ");
                urlHashCode = splitLine[0];
                hasTitleInfo.add(urlHashCode);
            }
        } catch (IOException e) {
            System.err.println("Could not load hashcode map");
            System.exit(1);
        }
        return hasTitleInfo;
    }

    private static List<String> downloadTheseFiles() {
//        HashSet<String> downloadThese = new HashSet<>();
        List<String> downloadThese = new ArrayList<>();
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
//        String hasTitleInfoName = pathString + "title_info.txt";
        String hasTitleInfoName = pathString + "download_these_urls.txt";
        File hasTitleInfoFile = new File(hasTitleInfoName);
        Integer progressCount = 0;
        Integer lineCount = 0;
        String inputLine;
        String urlHashCode;
        String url;
        if (!hasTitleInfoFile.exists()) {
            System.err.println("Error opening hash_to_URL.txt");
            System.exit(1);
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(hasTitleInfoName));
//            System.out.println("|--------------------------------------------------| 100%");
//            System.out.print("|");
            while (((inputLine = fileReader.readLine()) != null)) {
                String[] splitLine = inputLine.split(" : ");
                urlHashCode = splitLine[0];
                downloadThese.add(urlHashCode);
            }
        } catch (IOException e) {
            System.err.println("Could not load hashcode map");
            System.exit(1);
        }

        return downloadThese;
    }

    public static List<String> getRemainingTitleInfo() {
        HashSet<String> hasTitle = getTitlesFromFile();
        HashMap<String, String> visitedURLs = LogChecker.getURLMapFromFile(67697);
        ArrayList<String> urlsToCrawl = new ArrayList<>();
        for (String key : visitedURLs.keySet()) {
            if (!hasTitle.contains(key)) {
                urlsToCrawl.add(visitedURLs.get(key));
            }
        }
        System.out.println(urlsToCrawl.size());
        return urlsToCrawl;
    }

    public static void main(String[] args) {
        try {
            BasicCrawlController.crawl(downloadTheseFiles());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
