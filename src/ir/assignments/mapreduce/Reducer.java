package ir.assignments.mapreduce;

import ir.assignments.helpers.PostingsEntry;
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

    public static HashMap<String, HashMap<String, List<PostingsEntry>>> getPostingsListFromFile() {
        /*
            Iterates through all the 'reduced' files in /reduced and turns content
            of each into a hashmap. Each hashmap will be added to an encompasssing
            hashmap holds all the hashmaps of each file.

            For example in /reduced is
            00.txt
            01.txt
            02.txt
            etc...

            in 00.txt is
            -814063400 : [1361071648+1.36..., etc, etc, etc]
            -1186233000 : [-969445495+2.54..
            -2003616400 : [1037410422+1.36...
            ...
            ...

            Create Hashmap<String, Hashmap> for all files, say PostingsListLevel1
            Iterate through each file in /reduced
                Create Hashmap<String, List<PostingsEntry>> for file ie. PostingsListLevel2
                Iterate through each line in file
                    Parse line into wordHash and postingEntryListString. Line is in format- wordHash : [urlHash+tfidf, urlHash+tfidf, etc]
                    Create a PostingsEntryList to store posting entries.
                    Parse postingEntryListString by using ", " as delimeter and saving into delimetedList
                        Iterate through delimtedList
                            create new PostingsEntry(urlHash, tfidf)
                            Add new PostingsEntry to PostingsEntryList
                    PostingsListLevel2.put(wordHash, PostingsEntryList)
                PostingsListLevel1.put(fileName, PostingsListLevel2) ## for fileName, remove ".txt"
         */
        return null;
    }
    public static void main(String[] args) {
        reduce();
    }

}
