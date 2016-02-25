package ir.assignments.Search;

import ir.assignments.helpers.*;
import ir.assignments.mapreduce.Reducer;
import sun.rmi.runtime.Log;

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

    Integer numDocuments;

    public SearchEngine() {
        loadFiles();
    }

    public void run(){
        Boolean running = true;
        List<String> queryTokenized;
        HashSet<String> queryTerms;
        while (running) {
            List<PostingsEntry> postingsEntryList = new ArrayList<PostingsEntry>();
            List<String> anchorText = new ArrayList<String>();
            PriorityQueue<ResultNode> searchResultsHeap = new PriorityQueue<ResultNode>();
            searchResults = new HashMap<String, ResultNode>();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a query");
            String query = scanner.nextLine();
            if (query.trim().length() == 0) continue;
            long startTime = System.nanoTime();
            if (query.equals("q")) {
                running = false;
                continue;
            }
            queryTokenized = Arrays.asList(query.split("\\s+"));
            queryTerms = new HashSet<String>();
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
            System.out.println(queryTerms.size());
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
                if (url.contains("mailman.ics.uci")) mailModifier = 0.5;
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
//                        if (foldToHere.getSearchScore() < resultNode.getSearchScore()) {
//                            foldToHere.setSearchScore(resultNode.getSearchScore());
//                        }
//                        foldToHere.setSearchScore(foldToHere.getSearchScore() * (1 + resultNode.getPageRankScore()));
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
                System.out.println("No results found");
                continue;
            }
            if (searchResultsHeap.size() > 20) {
                for (int i = 0; i < 20; i++) {
//                while (!searchResultsHeap.isEmpty()) {
                    ResultNode searchResult = searchResultsHeap.poll();
//                    System.out.println(hashToURLMap.get(searchResult.getUrlHashCode()) + " : " + searchResult.getSearchScore() + " : " + searchResult.getTfidfScore() + " : " + searchResult.getAnchorScore() + " : " + searchResult.getPageRankScore());
                    System.out.println(searchResult.getUrl() + " : " + searchResult.getSearchScore());
                    searchResult.printSubPages();
                }
            } else {
                while (!searchResultsHeap.isEmpty()) {
                    System.out.println(searchResultsHeap.poll().getUrl());
                }
            }
            long endTime = System.nanoTime();
            System.out.println(endTime - startTime);
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
        this.numDocuments = anchorText.size();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.run();
    }


}
