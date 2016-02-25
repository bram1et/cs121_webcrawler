package ir.assignments.helpers;

import ir.assignments.basic.BasicCrawlController;

import java.io.*;
import java.util.*;
import java.nio.file.Paths;

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

    public static List<String> getURLsFromLogs() {
        List<String> urls = new ArrayList<String>();
        String logFolder = "logs";
        String fileName = "";
        File dir = new File(logFolder);
        File[] directoryListing = dir.listFiles();
        Integer count = 0;
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
                            count += 1;
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println(e.toString());
                    }
                }
            }
        }
        return urls;
    }

    public static HashMap<Integer, String> getVisitedURLs() {
        HashMap<Integer, String> hashCodeMap = new HashMap<Integer, String>();
        String logFolder = "logs";
        String fileName = "";
        File dir = new File(logFolder);
        File[] directoryListing = dir.listFiles();
        Integer count = 0;
        if (directoryListing != null) {
            for (File logFile : directoryListing) {
                fileName = logFile.toString();
                if (logFile.isFile() && fileName.contains("log.txt") && !fileName.contains(".DS_Store")) {
                    try {
                        Scanner sc = new Scanner(logFile);
                        while (sc.hasNext()) {
                            List<String> logLine = Arrays.asList(sc.nextLine().split(": "));
                            String url = logLine.get(1).trim();
                            Integer hashCode = Integer.parseInt(logLine.get(2).trim());
                            hashCodeMap.put(hashCode, url);
                            count += 1;
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println(e.toString());
                    }
                }
            }
        }
        return hashCodeMap;
    }

    public static void writeURLMapToFile(HashMap<Integer, String> hashCodeMap) {
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String hashMapFileName = pathString + "hash_to_URL.txt";
        File hashCodeMapFile = new File(hashMapFileName);
        if (hashCodeMapFile.exists()) {
            hashCodeMapFile.delete();
        }
        try {
            hashCodeMapFile.createNewFile();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        for (Integer urlHashCode : hashCodeMap.keySet()) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(hashMapFileName, true))) {
                fileWriter.write(urlHashCode.toString() + " : " + hashCodeMap.get(urlHashCode));
                fileWriter.newLine();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static HashMap<String, String> getURLMapFromFile (Integer numURLs) {
        HashMap<String, String> hashCodeMap = new HashMap<String, String>();
        LoadingProgressTracker loadingProgressTracker;
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String hashMapFileName = pathString + "hash_to_URL.txt";
        File hashCodeMapFile = new File(hashMapFileName);
        Integer progressCount = 0;
        Integer lineCount = 0;
        String inputLine;
        String urlHashCode;
        String url;
        if (!hashCodeMapFile.exists()) {
            System.err.println("Error opening hash_to_URL.txt");
            System.exit(1);
        }
        loadingProgressTracker = new LoadingProgressTracker(numURLs, "URL Map");
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(hashMapFileName));
            loadingProgressTracker.incrementProgress();
            while (((inputLine = fileReader.readLine()) != null)) {
                String[] splitLine = inputLine.split(" : ");
                urlHashCode = splitLine[0];
                url = splitLine[1];
                hashCodeMap.put(urlHashCode, url);
                lineCount += 1;
                if((100 * lineCount / numURLs) > progressCount) {
                    System.out.print("-");
                    progressCount += 2;
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load hashcode map");
            System.exit(1);
        }
        loadingProgressTracker.printFinished();
        return hashCodeMap;
    }

    public static void getSubdomains(List<String> urls) {
        String subdomainFileName = "subdomainsTemp.txt";
        String subDomain = "";
        for (String url : urls) {
            try (BufferedWriter subDomainWriter = new BufferedWriter(new FileWriter(subdomainFileName, true))) {
                subDomain = url.split(".ics.uci.edu")[0];
                System.out.println(subDomain);
                if (subDomain.length() < 30) {
                    subDomainWriter.write(subDomain);
                    subDomainWriter.newLine();
                    subDomainWriter.flush();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static void urlNormalizingMap() {
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String hashMapFileName = pathString + "url_to_norm_url.txt";
        File hashCodeMapFile = new File(hashMapFileName);
        if (hashCodeMapFile.exists()) {
            hashCodeMapFile.delete();
        }
        try {
            hashCodeMapFile.createNewFile();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        HashMap<String, String> hashToURLMap = getURLMapFromFile(67696);
        for (String key : hashToURLMap.keySet()) {
            String url = hashToURLMap.get(key);
            String urlMapsTohere = urlLinkFolder(hashToURLMap, url);
            if (!key.equals(urlMapsTohere)) {
                try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(hashMapFileName, true))) {
                    fileWriter.write(key + " : " + urlMapsTohere);
                    fileWriter.newLine();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }

    private static String urlLinkFolder(HashMap<String, String> hashToURLMap, String url) {
        int urlSize = url.length();
        String urlToReturn = url;
        if (url.contains("https")) {
            url = url.replace("https", "http");
            if (hashToURLMap.containsKey(String.valueOf(url.hashCode()))) {
                urlToReturn = url;
            }

        }
        if (url.contains("index")) {
            int index_loc = url.lastIndexOf("index");
            url = url.substring(0, index_loc);
            if (hashToURLMap.containsKey(String.valueOf(url.hashCode()))) {
                urlToReturn = url;
                return String.valueOf(urlToReturn.hashCode());
            }
        }
        if (url.contains(".php") || url.contains(".html")) {
            int exten_loc = url.lastIndexOf(".");
            url = url.substring(0, exten_loc);
            if (hashToURLMap.containsKey(String.valueOf(url.hashCode()))) {
                urlToReturn = url;
                return String.valueOf(urlToReturn.hashCode());
            }
        }
        return String.valueOf(urlToReturn.hashCode());
    }

    public static HashMap<String, String> getNormURLsFromFile(int numURLs) {
        HashMap<String, String> hashCodeMap = new HashMap<String, String>();
        LoadingProgressTracker loadingProgressTracker;
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String hashMapFileName = pathString + "url_to_norm_url.txt";
        File hashCodeMapFile = new File(hashMapFileName);
        Integer progressCount = 0;
        Integer lineCount = 0;
        String inputLine;
        String urlHashCode;
        String url;
        if (!hashCodeMapFile.exists()) {
            System.err.println("Error opening hash_to_URL.txt");
            System.exit(1);
        }
        loadingProgressTracker = new LoadingProgressTracker(numURLs, "URL Normalizer Map");
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(hashMapFileName));
            while (((inputLine = fileReader.readLine()) != null)) {
                String[] splitLine = inputLine.split(" : ");
                urlHashCode = splitLine[0];
                url = splitLine[1];
                hashCodeMap.put(urlHashCode, url);
                loadingProgressTracker.incrementProgress();
            }
        } catch (IOException e) {
            System.err.println("Could not load hashcode map");
            System.exit(1);
        }
        loadingProgressTracker.printFinished();
        return hashCodeMap;
    }
    public static void main(String[] args) {
        /*
        List<String> visitedURLS = new ArrayList<>();
        HashMap<String, String> hashToURLMap = getURLMapFromFile(67696);

        for (String urlHashCode : getURLMapFromFile(67697).keySet()) {
            visitedURLS.add(hashToURLMap.get(urlHashCode));
        }
        try {
            BasicCrawlController.crawl(visitedURLS);
        } catch (Exception e) {
            System.err.println(e);
        }
        */
        urlNormalizingMap();
    }
}
