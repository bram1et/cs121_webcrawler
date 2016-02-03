package ir.assignments.three.helpers;

import java.util.List;

/**
 * Counts the number of unique pages in a subdomain and prints it into
 * a file called Subdomains.txt
 * Modified on 2/2/2016.
 */
public class SubdomainCounter {
    /**
     * Write out the list of subdomains and their number of unique
     * pages into Subdomains.txt
     */
    public static void main(String[] args) {
        SubdomainHelper subdomainHelper = new SubdomainHelper();
        List<Frequency> subdomainFrequencies = subdomainHelper.computeSubdomainFrequencies();
        Utilities.printSubdomainFrequenciesToFile(subdomainFrequencies, "Subdomains.txt");
    }
}
