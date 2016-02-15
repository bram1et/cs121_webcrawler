package ir.assignments.helpers;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Chris on 2/12/16.
 */
public class LinkInfluenceCalculator {

    public static HashMap<String, Influence> calculateLinkInfluence() {
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
        Integer iterations = 3;
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
                            if (url.contains("djp3-pc2") || url.contains("luci.ics.uci.edu") || url.contains("LUCICodeRepository")) {
                                linkInfluence.put(fileName, new Influence(fileName));
                                continue;
                            }
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
                            if (i == (iterations - 1)) {
                                sc.close();
                            }

                        } catch (FileNotFoundException e) {
                            System.err.println("File not found...");
                        } finally {
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

    public static HashMap<String, Influence> getInfluenceFromFile() {
        HashMap<String, Influence> linkInfluenceMap = new HashMap<>();
        String pathString = Paths.get("").toAbsolutePath().toString();
        String influenceFile = pathString + "/influenceFilev5.txt";
        String line;
        String urlHashCode;
        Double influence;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(influenceFile));
            while (((line = fileReader.readLine()) != null)) {
                String[] splitLine = line.split(" : ");
                urlHashCode = splitLine[0];
                influence = Double.parseDouble(splitLine[1]);
                linkInfluenceMap.put(urlHashCode, new Influence(urlHashCode, influence));
            }
        } catch (IOException e) {
            System.err.println("Could not load influence");
            System.exit(1);
        }
        return linkInfluenceMap;
    }
    public static void main(String[] args) {
        Utilities.writeInfluenceToFile(calculateLinkInfluence());
    }
}
