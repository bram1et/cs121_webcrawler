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
import org.apache.http.Header;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;
import java.nio.file.*;
import java.nio.charset.Charset;
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
    return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.contains(".ics.uci.edu");
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
    String logFileName = "log.txt";
    String freqFileName = url.hashCode() + ".txt";
    Path filePath = Paths.get("./" + logFileName);
    File file = new File(filePath.toString());
    Charset charset = Charset.forName("UTF-8");
    Utilities utilities = new Utilities();
    WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter();


    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        System.err.println(e);
      }
    }


    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();


      System.out.println("URL: " + url);
      /**
       * Writing urls to log file. Might be helpful...
       */
      try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFileName, true))) {
        logWriter.write("URL: " + url);
        logWriter.newLine();
        logWriter.write("Text length:" + text.length());
        logWriter.newLine();
        logWriter.write("Number of outgoing links: " + links.size());
        logWriter.newLine();
        logWriter.flush();
      } catch (IOException e) {
        System.err.println(e);
      }
      List<String> words = utilities.tokenizeString(text);
      List<Frequency> frequencies = wordFrequencyCounter.computeWordFrequencies(words);
      utilities.printFrequenciesToFile(frequencies, "./freqFiles/" + freqFileName, url);
      System.exit(0);

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