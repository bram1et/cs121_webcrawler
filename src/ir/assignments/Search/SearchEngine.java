package ir.assignments.Search;

import ir.assignments.helpers.*;

import java.util.*;

/**
 * Created by Chris on 2/12/16.
 */
public class SearchEngine {

    private TreeMap<String, List<PostingsEntry>> postingsList;
    private HashMap<String, Influence> linkInfluence;
    private HashMap<String, ResultNode> searchResults;
    private PriorityQueue<ResultNode> searchResultsHeap;
    HashMap<String, Integer> documentFrequencies;
    HashMap<String, List<String>> anchorText;

    public SearchEngine() {
        loadFiles();
    }

    public void run(){
        Boolean running = true;
        List<String> queryTokenized;
        List<String> queryTerms;
        while (running) {
            List<PostingsEntry> postingsEntryList = new ArrayList<PostingsEntry>();
            List<String> anchorText = new ArrayList<String>();
            searchResults = new HashMap<String, ResultNode>();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a query");
            String query = scanner.nextLine();
            if (query.equals("q")) {
                running = false;
                break;
            }
            queryTokenized = Arrays.asList(query.split("\\s+"));
            queryTerms = new ArrayList<String>();
            String minusTwo = "";
            String minusOne = "";
            String current = "";
            for (String queryToken : queryTokenized) {
                current = queryToken.toLowerCase().trim();
                queryTerms.add((current).trim());
                queryTerms.add((minusOne + " " +  current).trim());
                queryTerms.add((minusTwo + " " + minusOne + " " +  current).trim());
                minusTwo = minusOne;
                minusOne = current;
            }

            for (String token : queryTerms) {
                if (token.length() == 0) continue;
                if (postingsList.containsKey(token)) {
                    postingsEntryList = postingsList.get(token);
                } else {
                    continue;
                }
                if (postingsEntryList.size() == 0) {
                    continue;
                }
                for (PostingsEntry postings : postingsEntryList) {
                    String urlHashCode = String.valueOf(postings.getUrlHashCode());
                    String url = postings.getUrl();
                    double tfidf = postings.getTfidf();
                    double siteInfluence = 1 + Math.log10(linkInfluence.get(urlHashCode).getInfluence());
                    if (searchResults.containsKey(urlHashCode)) {
                        double score = searchResults.get(urlHashCode).getSearchScore();
                        score += tfidf * siteInfluence;
                        searchResults.get(urlHashCode).setSearchScore(score);
                    } else {
                        searchResults.put(urlHashCode, new ResultNode(urlHashCode, url, tfidf * siteInfluence));
                    }
                }
            }
            List<ResultNode> sortedResults = new ArrayList<ResultNode>();
            for (String result : searchResults.keySet()) {
                ResultNode resultNode = searchResults.get(result);
                String urlHashCode = resultNode.getUrlHashCode();
                Double searchSore = resultNode.getSearchScore();
                double termsInAchor = 0;
                List<String> anchorTextList = this.anchorText.get(urlHashCode);
                for (String term : queryTerms) {
                    if (anchorTextList.contains(term)) termsInAchor += 1;
                }
                resultNode.setSearchScore(searchSore * (1 + (termsInAchor / queryTerms.size())));
                sortedResults.add(resultNode);
            }
            if (sortedResults.isEmpty()) {
                System.out.println("No results found");
                continue;
            }
            Collections.sort(sortedResults);
            if (sortedResults.size() > 20) {
                for (int i = 0; i < 20; i++) {
                    System.out.println(sortedResults.get(i).getUrl() + " : " + sortedResults.get(i).getSearchScore());
                }
            } else {
                System.out.println(sortedResults);
            }
        }
    }

    private void loadFiles() {
        System.out.println("Loading files...");
        postingsList = WordCounter.calculatetfidf();
        linkInfluence = LinkInfluenceCalculator.getInfluenceFromFile();
        documentFrequencies = Utilities.getDocumentFrequencyMap();
        anchorText = WordCounter.getAnchorText();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.run();
    }


}
