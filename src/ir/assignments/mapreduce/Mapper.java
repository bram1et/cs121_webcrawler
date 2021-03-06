/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
*/
package ir.assignments.mapreduce;

import ir.assignments.helpers.PostingsEntry;
import ir.assignments.helpers.Utilities;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Mapper {
    /*
        Starting with document files saved as a bag of words, creates
        map of term to documentID. Does this for each frequency document
        within freqFiles/.

        For example... for document 1009367760.txt corresponding with

        URL: http://www.ics.uci.edu/~smyth/
        Anchor: [padhraic, smyth]
        Outgoing: [...]
        Total item count: 316
        Unique item count: 190
        cs                       8 <- term and frequency count start here
        learning                 7
        science                  7
        data                     6
        group                    6
        machine                  6
        ...
        ...

        Iterates through terms
        Take hash of term, say learning.hashCode() = 1574204190
        Take last two digits of hash, 90
        In 90.txt append:
            word hash : document hash+tfidf <- use word hash as opposed to word to save space. '+' is delimiter
            ie 1574204190 : 1009367760+3.14

     */
    public static void map() {
        HashMap<String, Integer> documentFrequencies = Utilities.getDocumentFrequencyMap();
        HashSet<String> doNotIncludeTerms = getDoNotIncludeTerms();

        String fileName;
        String pathString = Paths.get("").toAbsolutePath().toString();
        String mappedFolder = pathString + "/mapped/";
        String frequencyFolder = "freqFiles";
        Integer fileCount = 0;
        Integer progressCount = 0;
        /*
            Opens folder containing frequency documents
            and if there are no documents, will exit.
         */
        File dir = new File(frequencyFolder);
        File[] directoryListing = dir.listFiles();
        double numFiles =  directoryListing.length;
        if (numFiles == 0) {
            System.err.println("Error mapping");
            System.exit(1);
        }
        /*
            Iterates through files with each iteration checking
            to make sure that file is okay
         */
        for (File freqFile : directoryListing) {
            /*
                DocumentID is saved in fileName.
             */
            fileName = freqFile.toString().split(".txt")[0];
            if (freqFile.isFile() && !fileName.contains(".DS_Store")) {
                try {
                    Integer documentFrequency;
                    Scanner scanner = new Scanner(freqFile);
                    /*
                        Skips past irrelevant information such as url, word count, etc.
                     */
                    scanner.nextLine();
                    scanner.nextLine();
                    scanner.nextLine();
                    scanner.nextLine();
                    scanner.nextLine();
                    while (scanner.hasNext()) {
                        /*
                            Gets word and term frequency and saves into variables.
                         */
                        List<String> wordAndCount = Arrays.asList(scanner.nextLine().split("\\s+"));
                        if (wordAndCount.size() >= 2) {
                            String word = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount);
                            if (doNotIncludeTerms.contains(word)) continue;
                            Integer frequency = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
                            if (documentFrequencies.containsKey(word)) documentFrequency = documentFrequencies.get(word);
                            else continue;
                            double tfidf = (1 + Math.log10(frequency)) * Math.log10(numFiles / documentFrequency);
                            String wordHashCode = Integer.toString(word.hashCode());
                            String mapFile = mappedFolder + wordHashCode.substring(wordHashCode.length()-2, wordHashCode.length()) +".txt";
                            try (BufferedWriter mapWriter = new BufferedWriter(new FileWriter(mapFile, true))) {
                                mapWriter.write(wordHashCode + " : " + fileName.split("/")[1] + "+" + tfidf);
                                mapWriter.newLine();
                                mapWriter.flush();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        }
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    System.err.println("File not found...");
                }
            }
        }
    }
    public static HashSet<String> getDoNotIncludeTerms() {
        HashSet<String> doNotIncludeTerms = new HashSet<>();
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String doNotIncludeTermFileName = pathString + "do_not_include.txt";
        File doNotIncludeTermFile = new File(doNotIncludeTermFileName);
        String inputLine;
        if (!doNotIncludeTermFile.exists()) {
            System.err.println("Error opening do_not_include.txt");
            System.exit(1);
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(doNotIncludeTermFileName));

            while (((inputLine = fileReader.readLine()) != null)) {
                List<String> wordAndCount = Utilities.tokenizeString(inputLine);
                String word = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount);
                doNotIncludeTerms.add(word);
            }
        } catch (IOException e) {
            System.err.println("Could not load hashcode map");
            System.exit(1);
        }
        return doNotIncludeTerms;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        map();
        Reducer.reduce();
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
    }
}
