package ir.assignments.helpers;


import ir.assignments.mapreduce.Mapper;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class Spelling {

    private final HashMap<String, Integer> nWords = new HashMap<String, Integer>();
    private char[] chars = new char[27];

    public  Spelling()  {
        HashSet<String> doNotIncludeTerms = Mapper.getDoNotIncludeTerms();
        String pathString = Paths.get("").toAbsolutePath().toString() + "/dataFiles/";
        String wordCountFileName = pathString + "total_word_counts.txt";
        File worldCountFile = new File(wordCountFileName);
        LoadingProgressTracker loadingProgressTracker = new LoadingProgressTracker(1279327, "Dictionary Loaded");
        String inputLine;
        String term;
        Integer frequency;
        if (!worldCountFile.exists()) {
            System.err.println("total_word_counts.txt");
            System.exit(1);
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(worldCountFile));
            fileReader.readLine();
            fileReader.readLine();
            while (((inputLine = fileReader.readLine()) != null)) {
                loadingProgressTracker.incrementProgress();
                List<String> wordAndCount = Arrays.asList(inputLine.split("\\s+"));
                term = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount).trim();
                frequency = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
//                if (doNotIncludeTerms.contains(term) || term.contains(" ")) {
                if (doNotIncludeTerms.contains(term) || frequency < 50) {
                    continue;
                }
                nWords.put(term, frequency);
            }
            loadingProgressTracker.printFinished();
        } catch (IOException e) {
            System.err.println(e);
        }
        int i = 0;
        for(char c='a'; c <= 'z'; ++c) {
            chars[i++] = c;
        }
        chars[i] = ' ';

    }

    private final ArrayList<String> edits(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i=0; i < word.length(); ++i) result.add(word.substring(0, i) + word.substring(i+1));
        for(int i=0; i < word.length()-1; ++i) result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
        for(int i=0; i < word.length(); ++i) for(char c : chars) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
        for(int i=0; i <= word.length(); ++i) for(char c : chars) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
        return result;
    }

    public final String correct(String word) {
        if(nWords.containsKey(word)) return word;
        ArrayList<String> list = edits(word);
        HashMap<Integer, String> candidates = new HashMap<Integer, String>();
        for(String s : list) if(nWords.containsKey(s)) candidates.put(nWords.get(s),s);
        if(candidates.size() > 0) return candidates.get(Collections.max(candidates.keySet()));
        for(String s : list) for(String w : edits(s)) if(nWords.containsKey(w)) candidates.put(nWords.get(w),w);
        return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
    }

    public static void main(String args[]) throws IOException {
        Spelling spelling = new Spelling();
        System.out.println("loaded");
        Scanner scanner = new Scanner(System.in);
        String word;
        while(true) {
            word = scanner.nextLine().trim();
            System.out.println(spelling.correct(word));
        }
    }

}