package ir.assignments.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Chris on 2/12/16.
 */
public class LinkInfluenceCalculator {


    private static HashMap<String, Influence> calculateLinkInfluence() {
        HashMap<String, Influence> linkInfluence = new HashMap<String, Influence>();
        List<Double> linkRanks;
        String frequencyFlder = "freqFiles";
        String fileName = "";
        String maxURL = "";
        Integer maxTotal = 0;
        File dir = new File(frequencyFlder);
        File[] directoryListing = dir.listFiles();
        double numFiles = directoryListing.length;
        Integer fileCount = 0;
        if (numFiles > 0) {
            for (int i = 0; i < 3; i++) {
                linkRanks = new ArrayList<Double>();
                for (File freqFile : directoryListing) {
                    if (freqFile.isFile() && !freqFile.toString().contains("DS_Store")) {
                        fileName = freqFile.getName().split("\\.")[0];
                        fileCount += 1;
                        System.out.println(100 * fileCount / numFiles);
                        if (!linkInfluence.containsKey(fileName)) {
                            linkInfluence.put(fileName, new Influence(fileName));
                        }
                        try {
                            Scanner sc = new Scanner(freqFile);
                            String url = sc.nextLine().split(": ")[1];
                            String anchor = sc.nextLine();
                            String outGoingString = sc.nextLine().split(":")[1].replace("[", "").replace("]", "");
                            List<String> outGoingURLs = Arrays.asList(outGoingString.split(", "));
                            sc.nextLine();
                            sc.nextLine();
                            for (String outGoingURL : outGoingURLs) {
                                outGoingURL = outGoingURL.trim();
                                double thisLinksInfluence = linkInfluence.get(fileName).getInfluence();
                                if (linkInfluence.containsKey(outGoingURL)) {
                                    linkInfluence.get(outGoingURL).incrementCount(thisLinksInfluence);
                                } else {
                                    linkInfluence.put(outGoingURL, new Influence(outGoingURL));
                                }
                            }
                        } catch (FileNotFoundException e) {
                            System.err.println("File not found...");
                        }
                    }
                }

                double sum = 0.0;
                double mean = 0.0;
                double numInfluences = 0.0;
                double count;
                for (String key : linkInfluence.keySet()) {
                    count = linkInfluence.get(key).getCount();
                    linkRanks.add(count);
                    sum += count;
                    numInfluences += 1.0;
                }
                mean = sum / numInfluences;
                double tempVar = 0.0;
                double variance = 0.0;
                for (String key : linkInfluence.keySet()) {
                    count = linkInfluence.get(key).getCount();
                    tempVar += (mean - count) * (mean - count);
                }
                variance = tempVar / numInfluences;

                double stddev = Math.sqrt(variance);

                double influenceFactor = 0.0;
                for (String key : linkInfluence.keySet()) {
                    count = linkInfluence.get(key).getCount();
                    influenceFactor = (count - mean) / stddev;
                    if (influenceFactor > 1.0) {
                        linkInfluence.get(key).setInfluence(influenceFactor);
                    } else {
                        linkInfluence.get(key).setInfluence(1.0);
                    }
                    linkInfluence.get(key).setCount(1.0);
                }
            }
        } else {
            System.err.println("Hmmm...");
        }
        return linkInfluence;
    }
    public static void main(String[] args) {
        Utilities.writeInfluenceToFile(calculateLinkInfluence());
    }
}
