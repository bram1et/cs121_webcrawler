/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
*/
package ir.assignments.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;


public class SnippetCreator {

    public static HashMap<String, String> getSnippets() {
        HashMap<String, String> snippetMap = new HashMap<>();
        String fileName;
        String pathString = Paths.get("").toAbsolutePath().toString();
        String snippetFolder = pathString + "/dataFiles/savedTextFiles/";
        File dir = new File(snippetFolder);
        File[] directoryListing = dir.listFiles();
        double numFiles =  directoryListing.length;
        if (numFiles == 0) {
            System.err.println("Error reducing");
            System.exit(1);
        }

        for (File textFile : directoryListing) {
            fileName = textFile.toString().split(".txt")[0].split(snippetFolder)[1];
            if (textFile.isFile() && !fileName.contains(".DS_Store")) {
                String snippet = "";
                String input;
                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader(textFile.toString()));
                    while ((input = fileReader.readLine()) != null) {
                        snippet += input;
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
                snippet = snippet.replaceAll("[\\t\\n\\r]+"," ").trim().replaceAll(" +", " ").substring(0, 240).trim();
                snippetMap.put(fileName, snippet);
            }
        }
        return snippetMap;
    }

    public static void main(String[] args) {
        System.out.println(getSnippets());
    }
}
