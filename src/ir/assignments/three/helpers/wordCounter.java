package ir.assignments.three.helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import ir.assignments.three.helpers.Utilities;
import ir.assignments.three.util.Util;

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
                        String total = sc.nextLine().split(": ")[1];
                        String unique = sc.nextLine();

                        if (Integer.parseInt(total) > maxTotal) {
                            maxTotal = Integer.parseInt(total);
                            maxURL = url;
                        }
                        while (sc.hasNext()) {
                            List<String> wordAndCount = Utilities.tokenizeString(sc.nextLine());
                            if (wordAndCount.size() == 2) {
                                String word = wordAndCount.get(0);
                                Integer count = Integer.parseInt(wordAndCount.get(1));

                                if (wordFrequencies.containsKey(word)) {
                                    wordFrequencies.get(word).increaseFrequencyByAmount(count);
                                } else {
                                    wordFrequencies.put(word, new Frequency(word, count));
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
        String freqFileName = "totalFreqs.txt";
        Utilities.printFrequenciesToFile(freqList, freqFileName, "yooo");
    }
}
