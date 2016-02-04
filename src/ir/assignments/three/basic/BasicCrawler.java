/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.assignments.three.basic;

import ir.assignments.three.crawler.Page;
import ir.assignments.three.crawler.WebCrawler;
import ir.assignments.three.parser.HtmlParseData;
import ir.assignments.three.url.WebURL;
import ir.assignments.three.helpers.Utilities;
import ir.assignments.three.helpers.WordFrequencyCounter;
import ir.assignments.three.helpers.Frequency;
import ir.assignments.three.helpers.SubdomainHelper;
import org.apache.http.Header;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.poi.util.SystemOutLogger;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.io.*;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class BasicCrawler extends WebCrawler {
  private final static Pattern BINARY_FILES_EXTENSIONS =
        Pattern.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps" +
        "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox" +
        "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv" +
        "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)" +
        "(\\?.*)?$"); // For url Query parts ( URL?q=... )

  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(Page page, WebURL url) {
    String href = url.getURL().toLowerCase();
    Boolean notEppsteinPics = !href.contains("~eppstein/pix/");
    Boolean notDuttGroup = !href.contains("duttgroup.ics.uci.edu/doku.php/") && !href.contains("do=media&image");
    Boolean uciDomain = href.contains(".ics.uci.edu/");
    Boolean okayToVisit = !BINARY_FILES_EXTENSIONS.matcher(href).matches() && uciDomain && notDuttGroup && notEppsteinPics;
    return okayToVisit;
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */

  @Override
  public void visit(Page page) {
    int docid = page.getWebURL().getDocid();
    String url = page.getWebURL().getURL();
    String domain = page.getWebURL().getDomain();
    String path = page.getWebURL().getPath();
    String subDomain = page.getWebURL().getSubDomain();
    String parentUrl = page.getWebURL().getParentUrl();
    String anchor = page.getWebURL().getAnchor();
    String subdomainFileName = "subdomainsTemp.txt";
    String freqFileName = url.hashCode() + ".txt";
    String pathString = Paths.get("").toAbsolutePath().toString();
    if ( url.contains("duttgroup.ics.uci.edu/doku.php/") && url.contains("do=media&image")) {
      System.out.println("Skipping: " + url);
      return;
    }
    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
//      String html = htmlParseData.getHtml();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();

      System.out.println("URL: " + url);
      /**
       * Writing urls to log file. Might be helpful...
       */
      try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("./logs/" + logFileName, true))) {
        logWriter.write("URL: " + url);
        logWriter.newLine();
        logWriter.write("Text length:" + text.length());
        logWriter.newLine();
        logWriter.write("Number of outgoing links: " + links.size());
        logWriter.newLine();
        logWriter.write("Anchor Text: " + anchor);
        logWriter.newLine();
        logWriter.write("Outlinks: " + links.toString());
        logWriter.flush();
      } catch (IOException e) {
        System.err.println(e);
      }

      /**
       * Write subdomains to a file that will be used to find the
       * number of unique pages detected in each subdomain
       */
      try (BufferedWriter subDomainWriter = new BufferedWriter(new FileWriter(subdomainFileName))) {
        SubdomainHelper subdomainHelper = new SubdomainHelper();
        String subDomainString = subdomainHelper.getSubdomain(subDomain);
        if (!subDomainString.equals("www")) {
          subDomainWriter.write(subDomainString);
          subDomainWriter.newLine();
        }
      } catch (IOException e) {
        System.err.println(e);
      }

      List<String> words = Utilities.tokenizeString(text);
      List<Frequency> frequencies = WordFrequencyCounter.computeWordFrequencies(words);
      String freqFilePath = pathString + "/freqFiles/" + freqFileName;
      Utilities.printFrequenciesToFile(frequencies, freqFilePath, url);
    }

    Header[] responseHeaders = page.getFetchResponseHeaders();
    if (responseHeaders != null) {
      logger.debug("Response headers:");
      for (Header header : responseHeaders) {
        logger.debug("\t{}: {}", header.getName(), header.getValue());
      }
    }
  }
}