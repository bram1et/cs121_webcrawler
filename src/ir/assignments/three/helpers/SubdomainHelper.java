package ir.assignments.three.helpers;

import java.io.File;
import java.util.List;

/**
 * SubdomainHelper: helps in creating and managing files that deal with subdomains
 * e.g. subdomainsTemp.txt and Subdomains.txt
 *
 * Modified on 2/2/2016
 */
public class SubdomainHelper {
    public SubdomainHelper() {}

    /**
     * Extracts the subdomain from a given URL
     * @param url : String representing the domains (subdomain, root and top level) of a URL
     * @return subdomain
     */
    public String getSubdomain (String url) {
        String[] domain = url.split("\\.");
        return domain[0];
    }

    /**
     * Calculates the number of unique pages detected in each subdomain
     * @return List<Frequency> : a list of subdomain frequencies
     */
    public List<Frequency> computeSubdomainFrequencies() {
        File subdomainFile = new File("subdomainsTemp.txt");
        List<String> subdomains = Utilities.tokenizeFile(subdomainFile);
        WordFrequencyCounter freqCounter = new WordFrequencyCounter();
        List<Frequency> freqList = freqCounter.computeWordFrequencies(subdomains);
        return freqList;
    }
}
