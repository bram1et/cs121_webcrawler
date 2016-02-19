package ir.assignments.Search;

import ir.assignments.helpers.*;
import ir.assignments.mapreduce.Reducer;

import java.util.*;

/**
 * Created by Chris on 2/12/16.
 */
public class SearchEngine {

    private HashMap<String, HashMap<String, List<PostingsEntry>>> postingsList;
    private HashMap<String, Influence> linkInfluence;
    private HashMap<String, ResultNode> searchResults;
    private HashMap<String, Integer> documentFrequencies;
    private HashMap<String, List<String>> anchorText;
    private HashMap<String, List<String>> titleText;
    private HashMap<String, String> hashToURLMap;

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
                    String url = postings.getUrl();
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
                String urlHashCode = resultNode.getUrlHashCode();
                Double searchSore = resultNode.getSearchScore();
                double anchorScore = resultNode.getAnchorScore();
                double anchorPercent = 0.0;
                double termsInAchor = 0;
                double termsinTitle = 0;
                double titlePercent = 0.0;
                Integer singleQueryTerms = queryTokenized.size();
                if (this.anchorText.containsKey(urlHashCode)) {
                    List<String> anchorTextList = this.anchorText.get(urlHashCode);
                    for (String term : queryTerms) {
                        if (anchorTextList.contains(term)) termsInAchor += 1;
//                        if (term.contains(" ")) singleQueryTerms -= 1;
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
                resultNode.setSearchScore(searchSore * anchorPercent * titlePercent);
                resultNode.setAnchorScore(anchorScore + anchorPercent);
                sortedResults.add(resultNode);
                searchResultsHeap.add(resultNode);
            }
            if (sortedResults.isEmpty()) {
                System.out.println("No results found");
                continue;
            }
            Collections.sort(sortedResults);
            if (sortedResults.size() > 20) {
                for (int i = 0; i < 20; i++) {
                    ResultNode searchResult = searchResultsHeap.poll();
                    System.out.println(hashToURLMap.get(searchResult.getUrlHashCode()) + " : " + searchResult.getSearchScore() + " : " + searchResult.getTfidfScore() + " : " + searchResult.getAnchorScore() + " : " + searchResult.getPageRankScore());
                }
            } else {
                for (int i = 0; i < sortedResults.size(); i++) {
                    System.out.println(searchResultsHeap.poll().getUrl() + " : " + sortedResults.get(i).getSearchScore());
                }
            }
        }
    }

    private void loadFiles() {
        System.out.println("Loading files...");
        System.out.println("Loading index...");
        postingsList = Reducer.getPostingsListFromFile();
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

        this.numDocuments = anchorText.size();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.run();
    }


}