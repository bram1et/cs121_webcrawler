package ir.assignments.mapreduce;

import ir.assignments.helpers.Utilities;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Chris on 2/16/16.
 */
public class Reducer {

    public static void reduce() {
        String pathString = Paths.get("").toAbsolutePath().toString();
        String reducedFolder = pathString + "/reduced/";
        String mappedFolder = "mapped";

        /*
            Opens folder containing term-document maps
            and if there are no documents, will exit.
         */
        File dir = new File(mappedFolder);
        File[] directoryListing = dir.listFiles();
        double numFiles =  directoryListing.length;
        if (numFiles == 0) {
            System.err.println("Error reducing");
            System.exit(1);
        }
        /*
            Iterates through files with each iteration checking
            to make sure that file is okay
         */
        for (File mapFile : directoryListing) {
            String fileName = mapFile.toString().split(".txt")[0];
            if (mapFile.isFile() && !fileName.contains(".DS_Store")) {
                try {
                    Scanner scanner = new Scanner(mapFile);

                    HashMap<String, List<String>> reducedMap = new HashMap<String, List<String>>();

                    while (scanner.hasNext()) {
                        /*
                            Gets term, document hash and tfidf and adds them to the HashMap.
                         */
                        List<String> termDocumentInfo = Arrays.asList(scanner.nextLine().split(" : "));
                        if (termDocumentInfo.size() >= 2) {
                            if (!reducedMap.containsKey(termDocumentInfo.get(0))) {
                                List<String> documentAndTfidf = new ArrayList<String>();
                                documentAndTfidf.add(termDocumentInfo.get(1));
                                reducedMap.put(termDocumentInfo.get(0), documentAndTfidf);
                            } else {
                                reducedMap.get(termDocumentInfo.get(0)).add(termDocumentInfo.get(1));
                            }
                        }
                    }
                    scanner.close();

                    String reduceFile = reducedFolder + fileName.substring(6,fileName.length()) + ".txt";
                    Utilities.writeReduceFile(reduceFile, reducedMap);

                } catch (FileNotFoundException e) {
                    System.err.println("File not found...");
                }
            }
        }
    }

    public static void main(String[] args) {
        reduce();
    }

}
