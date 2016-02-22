package ir.assignments.helpers;

import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xpath.internal.operations.Bool;
import ir.assignments.util.Util;
import javafx.geometry.Pos;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Chris on 1/31/16.
 */
public class WordCounter {
    public static HashMap<String, Frequency> getWordCounts(boolean total_count) {
        HashMap<String, Frequency> wordFrequencies = new HashMap<String, Frequency>();
        String frequencyFlder = "freqFiles";
        String fileName = "";
        String maxURL = "";
        Integer maxTotal = 0;
        File dir = new File(frequencyFlder);
        File[] directoryListing = dir.listFiles();
        float numFiles =  directoryListing.length;
        Integer fileCount = 0;
        if (directoryListing != null) {
            for (File freqFile : directoryListing) {
                fileName = freqFile.toString();
                if (freqFile.isFile() && !fileName.contains(".DS_Store")) {
                    fileCount += 1;
                    System.out.println(100 * fileCount / numFiles);
                    try {
                        Scanner sc = new Scanner(freqFile);
                        String url = sc.nextLine();
                        String anchor = sc.nextLine();
                        String outGoingURLs = sc.nextLine();
                        String total = sc.nextLine().split(": ")[1];
                        String unique = sc.nextLine();
                        if (Integer.parseInt(total) > maxTotal) {
                            maxTotal = Integer.parseInt(total);
                            maxURL = url;
                        }
                        while (sc.hasNext()) {
                            List<String> wordAndCount = Utilities.tokenizeString(sc.nextLine());
//                            List<String> wordAndCount = Arrays.asList(sc.nextLine().split("\\s+"));
                            if (wordAndCount.size() >= 2) {

                                String word = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount);
                                if (total_count) {
                                    Integer count = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
                                /*
                                Commenting out. This is for counting total frequencies of words
                                */
                                    if (wordFrequencies.containsKey(word)) {
                                        wordFrequencies.get(word).increaseFrequencyByAmount(count);
                                    } else {
                                        wordFrequencies.put(word, new Frequency(word, count));
                                    }
                                } else {
                                /*
                                This is for counting document frequency
                                 */
                                    if (wordFrequencies.containsKey(word)) {
                                        wordFrequencies.get(word).incrementFrequency();
                                    } else {
                                        wordFrequencies.put(word, new Frequency(word, 1));
                                    }
                                }

                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("File not found...");
                    }
                }
            }
        } else {
            System.err.println("Hmmm...");
        }
        return wordFrequencies;
    }

    public static TreeMap<String, List<PostingsEntry>> calculatetfidf() {
        TreeMap<String, List<PostingsEntry>> postingsList = new TreeMap<String, List<PostingsEntry>>();
        HashMap<String, Integer> documentFrequencies = Utilities.getDocumentFrequencyMap();
        String frequencyFlder = "freqFiles";
        String fileName = "";
        String maxURL = "";
        Integer maxTotal = 0;
        File dir = new File(frequencyFlder);
        File[] directoryListing = dir.listFiles();
        double numFiles =  directoryListing.length;
        Integer fileCount = 0;
        Integer progressCount = 0;

        if (numFiles > 0) {
            System.out.println("|--------------------------------------------------| 100%");
            System.out.print("|");
            for (File freqFile : directoryListing) {
                fileName = freqFile.toString();
                if (freqFile.isFile() && !fileName.contains(".DS_Store")) {
                    fileCount += 1;
                    if((100 * fileCount / numFiles) > progressCount) {
                        System.out.print("-");
                        progressCount += 2;
                    }
                    try {
                        Scanner sc = new Scanner(freqFile);
                        String url = sc.nextLine().split(": ")[1];
                        String anchor = sc.nextLine();
                        String outGoingURLs = sc.nextLine();
                        String total = sc.nextLine().split(": ")[1];
                        String unique = sc.nextLine();
                        Integer documentFrequency;
                        if (Integer.parseInt(total) > maxTotal) {
                            maxTotal = Integer.parseInt(total);
                            maxURL = url;
                        }
                        while (sc.hasNext()) {
                            List<String> wordAndCount = Arrays.asList(sc.nextLine().split("\\s+"));
                            if (wordAndCount.size() >= 2) {
                                String word = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount);
                                Integer frequency = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
                                if (documentFrequencies.containsKey(word)) {
                                    documentFrequency = documentFrequencies.get(word);
                                } else {
                                    continue;
                                }
                                double tfidf = (1 + Math.log10(frequency)) * Math.log10(numFiles/documentFrequency);
                                PostingsEntry postingsEntry = new PostingsEntry(url.hashCode(), tfidf, url);
                                if (postingsList.containsKey(word)) {
                                    postingsList.get(word).add(postingsEntry);
                                } else {
                                    List<PostingsEntry> postingsEntryList = new ArrayList<>();
                                    postingsEntryList.add(postingsEntry);
                                    postingsList.put(word, postingsEntryList);
                                }
                            }
                        }
                        sc.close();
                    } catch (FileNotFoundException e) {
                        System.err.println("File not found...");
                    }
                }
            }
        } else {
            System.err.println("Hmmm...");
        }
        System.out.println("| Index loaded");
        return postingsList;
    }

    private static void getPostingsList() {
        Gson gson = new Gson();
        TreeMap<String, List<PostingsEntry>> postingsList = calculatetfidf();
        String pathString = Paths.get("").toAbsolutePath().toString();
        String postingsListFolder = pathString + "/postingsList/";
        String json = gson.toJson(postingsList);
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(postingsListFolder + "postingJson.txt");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader br = new BufferedReader(new FileReader(postingsListFolder + "postingJson.txt"));
            //convert the json string back to object
            Type type = new TypeToken<TreeMap<String, List<PostingsEntry>>>(){}.getType();
            TreeMap<String, List<PostingsEntry>> postingsListFromJson = gson.fromJson(br, type);

            System.out.println(postingsListFromJson.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
//        Utilities.writePostingsListToFile(postingsList, postingsListFolder);
    }

    public static TreeMap<String, List<PostingsEntry>> gettfidfFromFiles() {
        TreeMap<String, List<PostingsEntry>> postingsList = new TreeMap<String, List<PostingsEntry>>();
        String postingsFolder = "postingsList";
        String fileName = "";
        String posting = "";
        String urlHashCode = "";
        String tfidfString = "";
        String url = "";
        String term;
        List<String> postingListString;
        List<PostingsEntry> postingsEntryList;
        File dir = new File(postingsFolder);
        File[] directoryListing = dir.listFiles();
        double numFiles =  directoryListing.length;
        if (numFiles > 0) {
            for (File postingsFile: directoryListing) {
                System.out.println(postingsFile.toString());
                if (postingsFile.isFile() && !fileName.contains(".DS_Store")) {
                    try {
                        Scanner scanner = new Scanner(postingsFile);
                        while (scanner.hasNext()) {
                            posting = scanner.nextLine();
                            term = posting.split(" : ")[0];
                            postingListString = Arrays.asList(posting.split(" : ")[1].replace("[", "").replace("]", "").replace(", {", "{").split("PostingsEntry"));
                            postingListString = postingListString.subList(1, postingListString.size());
                            postingsEntryList = new ArrayList<PostingsEntry>();
                            for (String postingString : postingListString) {
                                urlHashCode = postingString.split(", ")[0].split("=")[1];
                                tfidfString = postingString.split(", ")[1].split("=")[1];
                                url = postingString.split(", ")[2].split("=")[1].replace("'", "").replace("}", "");
                                postingsEntryList.add(new PostingsEntry(Integer.parseInt(urlHashCode), Double.parseDouble(tfidfString), url));
                            }
                            postingsList.put(term, postingsEntryList);
                        }
                        scanner.close();
                    } catch (FileNotFoundException e) {
                        System.err.println(e);
                    }
                }
            }
        }
        return postingsList;
    }

    public static HashMap<String, List<String>> getAnchorText() {
        HashMap<String, List<String>> anchorTextMap = new HashMap<String, List<String>>();
        String frequencyFlder = "freqFiles";
        String fileName = "";
        String maxURL = "";
        Integer maxTotal = 0;
        File dir = new File(frequencyFlder);
        File[] directoryListing = dir.listFiles();
        float numFiles =  directoryListing.length;
        Integer fileCount = 0;
        if (directoryListing != null) {
            for (File freqFile : directoryListing) {
                fileName = freqFile.toString();
                if (freqFile.isFile() && !fileName.contains(".DS_Store")) {
                    fileCount += 1;
                    System.out.println(100 * fileCount / numFiles);
                    try {
                        Scanner sc = new Scanner(freqFile);
                        String url = sc.nextLine();
                        String anchorString = sc.nextLine().split(": ")[1];
                        List<String> anchor = Arrays.asList(anchorString.replace("[","").replace("]", "").split(", "));
                        sc.nextLine();
                        sc.nextLine();
                        sc.nextLine();
                        String urlHashCode = fileName.split("/")[1].split(".txt")[0];
                        anchorTextMap.put(urlHashCode, anchor);
                        sc.close();
                    } catch (FileNotFoundException e) {
                        System.err.println("File not found...");
                    }
                }
            }
        } else {
            System.err.println("Hmmm...");
        }
        return anchorTextMap;
    }

    public static HashMap<String, List<String>> getAnchorTextFromFile(Integer numAnchorTexts) {
        HashMap<String, List<String>> anchorTexts = new HashMap<>();
        String pathString = Paths.get("").toAbsolutePath().toString();
        String anchorTextFile = pathString + "/anchorTextsFile.txt";
        String line;
        String urlHashCode;
        Double progressCount = 0.0;
        Double lineCount = 0.0;
        List<String> anchorTextList = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(anchorTextFile));
            System.out.println("|--------------------------------------------------| 100%");
            System.out.print("|");
            while (((line = fileReader.readLine()) != null)) {
                String[] splitLine = line.split(" : ");
                urlHashCode = splitLine[0];
                anchorTextList = Arrays.asList(splitLine[1].replace("[", "").replace("]", "").split(", "));
                anchorTexts.put(urlHashCode, anchorTextList);
                lineCount += 1;
                if((100 * lineCount / numAnchorTexts) > progressCount) {
                    System.out.print("-");
                    progressCount += 2;
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load anchor texts");
            System.exit(1);
        }
        System.out.println("| Anchor Text Loaded");
        return anchorTexts;
    }

    public static HashMap<String, List<String>> getTitleTextsFromFile(Integer numTitleTexts) {
        HashMap<String, List<String>> titleTexts = new HashMap<>();
        String pathString = Paths.get("").toAbsolutePath().toString();
        String anchorTextFile = pathString + "/dataFiles/title_info.txt";
        String line;
        String urlHashCode;
        List<String> titleTextList = new ArrayList<>();
        LoadingProgressTracker loadingProgressTracker;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(anchorTextFile));
            loadingProgressTracker = new LoadingProgressTracker(numTitleTexts, "Title Texts");
            while (((line = fileReader.readLine()) != null)) {
                String[] splitLine = line.split(" : ");
                urlHashCode = splitLine[0];
                titleTextList = Arrays.asList(splitLine[1].replace("[", "").replace("]", "").split(", "));
                titleTexts.put(urlHashCode, titleTextList);
                loadingProgressTracker.incrementProgress();
            }
            loadingProgressTracker.printFinished();
        } catch (Exception e) {
            System.err.println();
            System.err.println("Could not load title texts");
            System.err.println(e);
            System.exit(1);
        }
        return titleTexts;
    }

    public static void getNumberCountToFile() {
        HashMap<String, Frequency> wordFrequencies = getWordCounts(true);
        List<Frequency> frequencyList = new ArrayList<Frequency>();
        for (String key : wordFrequencies.keySet()) {
            frequencyList.add(wordFrequencies.get(key));
        }
        Utilities.printFrequenciesToFile(frequencyList, "totalCounts.txt", "yooo", false);
    }

    public static void main(String[] args) {
//        getTitleTextsFromFile(49226);
        HashMap<String, Frequency> wordFrequencies = getWordCounts(true);
        List<Frequency> frequencyList = new ArrayList<Frequency>();
        for (String key : wordFrequencies.keySet()) {
            if (wordFrequencies.get(key).getFrequency() <= 5) {
                frequencyList.add(wordFrequencies.get(key));
                continue;
            }
            if (key.length() == 1) {
                frequencyList.add(wordFrequencies.get(key));
                continue;
            }
            try {
                Integer int_key = Integer.parseInt(key);
                if (int_key < 999 || int_key > 3000) {
                    frequencyList.add(wordFrequencies.get(key));
                }

            } catch (NumberFormatException nfe) {
                continue;
            }
        }
        Utilities.printFrequenciesToFile(frequencyList, "./dataFiles/do_not_include.txt", "yooo", false);
    }
}
