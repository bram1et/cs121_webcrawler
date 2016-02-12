package ir.assignments.helpers;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Chris on 1/31/16.
 */
public class WordCounter {
    private static HashMap<String, Frequency> getWordCounts() {
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
//                            List<String> wordAndCount = Utilities.tokenizeString(sc.nextLine());
                            List<String> wordAndCount = Arrays.asList(sc.nextLine().split("\\s+"));
                            if (wordAndCount.size() >= 2) {

                                String word = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount);
                                /*
                                Commenting out. This is for counting total frequencies of words
                                if (wordFrequencies.containsKey(word)) {
                                    wordFrequencies.get(word).increaseFrequencyByAmount(count);
                                } else {
                                    wordFrequencies.put(word, new Frequency(word, count));
                                }
                                */
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
                    } catch (FileNotFoundException e) {
                        System.err.println("File not found...");
                    }


                }
            }
        } else {
            System.err.println("Hmmm...");
        }
        System.out.println(maxURL + ": " + maxTotal);
        return wordFrequencies;
    }

    public static void main(String[] args) {
        HashMap<String, Frequency> wordFrequencies = getWordCounts();
        List<Frequency> freqList = new ArrayList<Frequency>();
        for (String key : wordFrequencies.keySet()) {
            freqList.add(wordFrequencies.get(key));
        }
        String pathString = Paths.get("").toAbsolutePath().toString();
        String dfFolder = pathString + "/documentFrequencies/";
        Utilities.writeDocumentFrequencyToFile(freqList, dfFolder, "", true);
    }
}
