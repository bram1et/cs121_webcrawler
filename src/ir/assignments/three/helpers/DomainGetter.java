package ir.assignments.three.helpers;

/**
 * Created by Emily on 1/27/2016.
 */
public class DomainGetter {
    public DomainGetter() {}

    public String getDomainName (String url) {
        String[] domain = url.split("\\.");
        return domain[0];
    }
}
