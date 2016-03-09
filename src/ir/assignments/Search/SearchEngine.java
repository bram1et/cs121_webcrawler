/*
Team Members:
Christopher Dang 75542500
Emily Puth 28239807
*/
package ir.assignments.Search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.assignments.helpers.*;
import ir.assignments.mapreduce.Reducer;
import ir.assignments.util.Util;
import sun.rmi.runtime.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


public class SearchEngine {

    private HashMap<String, HashMap<String, List<PostingsEntry>>> postingsList;
    private HashMap<String, Influence> linkInfluence;
    private HashMap<String, ResultNode> searchResults;
    private HashMap<String, Integer> documentFrequencies;
    private HashMap<String, List<String>> anchorText;
    private HashMap<String, List<String>> titleText;
    private HashMap<String, String> hashToURLMap;
    private HashMap<String, String> normalizingMap;
    private HashMap<String, String> snippetMap;
    private Spelling spelling;

    Integer numDocuments;
    public SearchEngine() {
        loadFiles();
    }


    public ResultsWrapper handleQuery(String query) {
        /*
            Takes a string and returns related links found by our webcrawler
             sorted by perceived relevancy.

             Parameters
             ----------
             query: String
                String containing query with which we search

         */

         //Initializing timer and variables.

        long startTime = System.nanoTime();
        ResultsWrapper resultsWrapper = new ResultsWrapper();
        List<Result> results = new ArrayList<Result>();
        HashSet<String> queryTerms = new HashSet<String>();
        List<PostingsEntry> postingsEntryList = new ArrayList<PostingsEntry>();
        PriorityQueue<ResultNode> searchResultsHeap = new PriorityQueue<ResultNode>();
        searchResults = new HashMap<String, ResultNode>();

        /*
        Get spell correction. If spell corrected, change query to corrected
        spelling. If '~' ignore spell correction.
        */
        String correctedQuery = spelling.correct(query);
        String originalQuery = query;

        if (!query.startsWith("~") && !correctedQuery.equals(query)) {
            query = correctedQuery;
            resultsWrapper.setSpellCorrected(true);
            resultsWrapper.setSpellCorrection(correctedQuery);
        }
        if (query.startsWith("~")) {
            query = query.substring(1, query.length());
            originalQuery = query;
        }
        /*
            Tokenize query and add two-grams and three-grams to query set.
         */
        List<String> queryTokenized = Arrays.asList(query.trim().split("\\s+"));
        String minusTwo = "";
        String minusOne = "";
        String current = "";
        Integer documentFrequency = 0;


        for (String queryToken : queryTokenized) {
            current = queryToken.toLowerCase().trim();
            queryTerms.add((current).trim());
            queryTerms.add((minusOne + " " +  current).trim());
            queryTerms.add((minusTwo + " " + minusOne + " " +  current).trim());
            minusTwo = minusOne;
            minusOne = current;
        }

        /*
            For term in query set, get postings list of documents. Create a resulteNode
            for each document and add to hashMap. For each document, sum up tfidf over
            separate query terms
         */

        for (String token : queryTerms) {
            postingsEntryList = new ArrayList<>();
            String tokenHash = String.valueOf(token.hashCode());
            String fileOfToken = tokenHash.substring(tokenHash.length() - 2, tokenHash.length());
            if (token.length() == 0) continue;
            if (postingsList.containsKey(fileOfToken)) {
                if (postingsList.get(fileOfToken).containsKey(tokenHash)) {
                    postingsEntryList = postingsList.get(fileOfToken).get(tokenHash);
                }
            }
            if (postingsEntryList.size() == 0) {
                continue;
            }
            if (documentFrequencies.containsKey(token)) {
                documentFrequency = documentFrequencies.get(token);
            } else {
                documentFrequency = 1;
            }
            for (PostingsEntry postings : postingsEntryList) {
                String urlHashCode = String.valueOf(postings.getUrlHashCode());
                String url = hashToURLMap.get(urlHashCode);
                double tfidf = postings.getTfidf();
                Double influnece;
                if (linkInfluence.containsKey(urlHashCode)) influnece = linkInfluence.get(urlHashCode).getInfluence();
                else influnece = 1.0;
                double siteInfluence = 1 + Math.log10(influnece);
                if (searchResults.containsKey(urlHashCode)) {
                    double score = searchResults.get(urlHashCode).getSearchScore();
                    score += tfidf * siteInfluence * Math.log10(this.numDocuments/documentFrequency);
                    searchResults.get(urlHashCode).setSearchScore(score);
                    double tfidfScore = searchResults.get(urlHashCode).getTfidfScore();
                    double siteInfluenceScore = searchResults.get(urlHashCode).getPageRankScore();
                    searchResults.get(urlHashCode).setTfidfScore(tfidfScore + (tfidf *Math.log10(this.numDocuments/documentFrequency)));
                    searchResults.get(urlHashCode).setPageRankScore(siteInfluenceScore + siteInfluence);
                } else {
                    searchResults.put(urlHashCode, new ResultNode(urlHashCode, url, tfidf * siteInfluence * Math.log10(this.numDocuments/documentFrequency)));
                }
            }
        }

        /*
            For each document multiply score by anchor text and title text
            modifiers. Add search results to result heap.
         */

        List<ResultNode> sortedResults = new ArrayList<ResultNode>();
        for (String result : searchResults.keySet()) {
            ResultNode resultNode = searchResults.get(result);
            String url = resultNode.getUrl();
            String urlHashCode = resultNode.getUrlHashCode();
            Double searchSore = resultNode.getSearchScore();
            double anchorScore = resultNode.getAnchorScore();
            double anchorPercent = 0.0;
            double termsInAchor = 0;
            double termsinTitle = 0;
            double titlePercent = 0.0;
            double mailModifier = 1.0;
            if (url != null && url.contains("mailman.ics.uci")) mailModifier = 0.5;
            Integer singleQueryTerms = queryTokenized.size();
            if (this.anchorText.containsKey(urlHashCode)) {
                List<String> anchorTextList = this.anchorText.get(urlHashCode);
                for (String term : queryTerms) {
                    if (anchorTextList.contains(term)) termsInAchor += 1;
                }
            }
            if (this.titleText.containsKey(urlHashCode)) {
                List<String> titleTextList = this.titleText.get(urlHashCode);
                for (String term : queryTerms) {
                    if (titleTextList.contains(term)) termsinTitle += 1;
                }
            }
            anchorPercent = 1 + 1 * (termsInAchor / singleQueryTerms);
            titlePercent = 1 + 1 * (termsinTitle / singleQueryTerms);
            resultNode.setSearchScore(searchSore * anchorPercent * titlePercent * mailModifier);
            resultNode.setAnchorScore(anchorScore + anchorPercent);
            sortedResults.add(resultNode);
            searchResultsHeap.add(resultNode);
        }

        /*
            For results, fold links into normalized form. Set score to the
            higher of the two. Removed folded links.
         */

        ArrayList<String> resultsToremove = new ArrayList<String>();
        for (String result : searchResults.keySet()) {
            ResultNode resultNode = searchResults.get(result);
            String urlToFold = resultNode.getUrl();
            if (urlToFold != null) {
                String urlHashCode = String.valueOf(urlToFold.hashCode());
                if (normalizingMap.containsKey(urlHashCode)) {
                    String foldedURLHash = normalizingMap.get(urlHashCode);
                    if (foldedURLHash.equals(resultNode.getUrlHashCode())) continue;
                    if (searchResults.containsKey(foldedURLHash)) {
                        ResultNode foldToHere = searchResults.get(foldedURLHash);
                        searchResultsHeap.remove(foldToHere);
                        double foldScore = foldToHere.getSearchScore();
                        double resultScore = resultNode.getSearchScore();
                        foldToHere.setSearchScore(Math.max(foldScore, resultScore));
                        if (searchResultsHeap.contains(resultNode)) {
                            searchResultsHeap.remove(resultNode);
                            resultsToremove.add(result);
                        }
                        searchResultsHeap.add(foldToHere);
                    }
                }
            }
        }

        for (String resultToRemove: resultsToremove) {
            searchResults.remove(resultToRemove);
        }

        /*
            Fold links into homepage if homepage is contained within
            search results. Increase homepage score/set to highest of
            contained links.
         */

        resultsToremove = new ArrayList<String>();
        for (String result : searchResults.keySet()) {
            ResultNode resultNode = searchResults.get(result);
            String urlToFold = resultNode.getUrl();
            if (urlToFold == null) continue;
            String homePage = foldLinkToHomePage(urlToFold);
            String urlFoldedHash = String.valueOf(homePage.hashCode());
            if (searchResults.containsKey(urlFoldedHash)) {
                ResultNode homeResultNode = searchResults.get(urlFoldedHash);
                if (homeResultNode.getUrlHashCode().equals(resultNode.getUrlHashCode())) continue;
                if (searchResultsHeap.contains(homeResultNode)) {
                    searchResultsHeap.remove(homeResultNode);
                    homeResultNode.addToSubPages(resultNode);
                    double homeScore = homeResultNode.getSearchScore();
                    double resultScore = resultNode.getSearchScore();
                    homeResultNode.setSearchScore(Math.max(homeScore, resultScore));
                    homeResultNode.setSearchScore(homeResultNode.getSearchScore() + .05 * (resultNode.getSearchScore()));
                    searchResultsHeap.remove(resultNode);
                    searchResultsHeap.add(homeResultNode);
                }
            }
        }

        if (searchResultsHeap.isEmpty()) {
            System.out.print("");
        }

        /*
            Removes top 20 results from heap, packages them into wrapper class.
            Retrieve title text, anchor text, snippet (if possible) for link.
            Returns json-ified version of wrapper class.
         */

        if (!searchResultsHeap.isEmpty()) {
            for (int i = 0; i < 20; i++) {
                if (searchResultsHeap.isEmpty()) break;
                ResultNode searchResult = searchResultsHeap.poll();
                String url = searchResult.getUrl();
                String text;
                if (titleText.containsKey(searchResult.getUrlHashCode())) {
                    List<String> titleTextList = titleText.get(searchResult.getUrlHashCode());
//                    text = Utilities.stringCombiner(0, titleTextList.size() - 1, true, titleTextList);
                    text = Utilities.titleCreator(titleTextList);
                } else {
                    text = url;
                }
                String pathString = Paths.get("").toAbsolutePath().toString();
                String collectThesePages = pathString + "/dataFiles/download_these_urls.txt";
                File writeHere = new File(collectThesePages);
                if (!writeHere.exists()) {
                    try {
                        writeHere.createNewFile();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
                try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(collectThesePages, true))) {
                    fileWriter.write(url);
                    fileWriter.newLine();
                    fileWriter.flush();
                } catch (IOException e) {
                    System.err.println(e);
                }
                String snippet = "A major key, never panic. Don’t panic, when it gets crazy and rough, don’t panic, stay calm. Egg whites, turkey sausage, wheat toast, water. Of course they don’t want us to eat our breakfast, so we are going to enjoy our breakfast.";
                if (snippetMap.containsKey(searchResult.getUrlHashCode())) {
                    snippet = snippetMap.get(searchResult.getUrlHashCode());
                }
                Result result = new Result(text, url, snippet);
                if (searchResult.hasSubPages()) {
                    List<ResultNode> subPages= searchResult.getSubPages();
                    List<Result> subPagesResults = new ArrayList<Result>();
                    for (ResultNode subPage : subPages) {
                        url = subPage.getUrl();
                        if (this.anchorText.containsKey(subPage.getUrlHashCode())) {
                            List<String> anchorTextList = this.anchorText.get(subPage.getUrlHashCode());
                            text = Utilities.titleCreator(anchorTextList);
                            if (text.equals(" ")){
                                text = subPage.getUrl().split("ics.uci.edu/")[1];
                            }
                        } else {
                            text = subPage.getUrl().split("ics.uci.edu/")[1];
                            System.out.println(text);
                        }
                        Result subPageResult = new Result(text, url);
                        subPagesResults.add(subPageResult);
                    }
                    result.setHasSubPages(true);
                    result.setSubPages(subPagesResults);
                }
                results.add(result);

            }
        } else {
            System.out.print("");
        }
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        resultsWrapper.setQuery(originalQuery);
        resultsWrapper.setNanoTime(endTime - startTime);
        resultsWrapper.setResults(results);
        return resultsWrapper;
    }

    public void run() {
        Boolean running = true;
        while (running) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a query");
            String query = scanner.nextLine();
            if (query.trim().length() == 0) continue;
            System.out.println(handleQuery(query));
        }
    }

    private String urlLinkFolder( String url) {
        int urlSize = url.length();
        String urlToReturn = url;
        if (url.contains("https")) {
            url = url.replace("https", "http");
            urlToReturn = url;

        }
        if (url.contains("index")) {
            int index_loc = url.lastIndexOf("index");
            url = url.substring(0, index_loc);
            if (hashToURLMap.containsKey(String.valueOf(url.hashCode()))) {
                urlToReturn = url;
                return String.valueOf(urlToReturn.hashCode());
            }
        }
        if (url.contains(".php") || url.contains(".html")) {
            int exten_loc = url.lastIndexOf(".");
            url = url.substring(0, exten_loc);
            if (hashToURLMap.containsKey(String.valueOf(url.hashCode()))) {
                urlToReturn = url;
                return String.valueOf(urlToReturn.hashCode());
            }
        }
        return String.valueOf(urlToReturn.hashCode());
    }

    private String foldLinkToHomePage(String url) {
        String originalURL = url;
        String subDomain = url.split("ics.uci.edu")[0] + "ics.uci.edu";
        subDomain = subDomain.replace("https", "http");
        String urlPath = url.split("ics.uci.edu")[1];
        List<String> paths = Arrays.asList(urlPath.split("/"));
        if (paths.size() > 0) {
            if (originalURL.contains("www.ics.uci.edu")) {
                paths = paths.subList(1, paths.size());
                subDomain += "/";
            }
            for (String path : paths) {
                subDomain += path + "/";
                if (hashToURLMap.containsKey(String.valueOf(subDomain.hashCode()))) {
                    return subDomain;
                }
            }
        }
        return originalURL;

    }

    private void loadFiles() {
        /*
            Loads all the necessary files into hashmaps for quick lookup.
         */
        System.out.println("Loading files...");
        System.out.println("Loading index...");
        postingsList = Reducer.getPostingsListFromFile(419029);
        System.out.println("Loading pagerank scores...");
        linkInfluence = LinkInfluenceCalculator.getInfluenceFromFile();
        System.out.println("Loading document frequencies...");
        documentFrequencies = Utilities.getDocumentFrequencyMap();
        System.out.println("Loading anchor texts map...");
        anchorText = WordCounter.getAnchorTextFromFile(67696);
        System.out.println("Loading title texts map...");
        titleText = WordCounter.getTitleTextsFromFile(49226);
        System.out.println("Loading hash to URL map...");
        hashToURLMap = LogChecker.getURLMapFromFile(67696);
        System.out.println("Loading URL normalizing map...");
        normalizingMap = LogChecker.getNormURLsFromFile(3574);
        System.out.println("Loading spelling dictionary...");
        spelling = new Spelling();
        System.out.println("Loading snippet map...");
        snippetMap = SnippetCreator.getSnippets();
        this.numDocuments = anchorText.size();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.run();
    }


}
