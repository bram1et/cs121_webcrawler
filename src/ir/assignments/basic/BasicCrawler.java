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

package ir.assignments.basic;

import com.sun.org.apache.xpath.internal.operations.Bool;
import ir.assignments.crawler.Page;
import ir.assignments.crawler.WebCrawler;
import ir.assignments.parser.HtmlParseData;
import ir.assignments.url.WebURL;
import ir.assignments.helpers.Utilities;
import ir.assignments.helpers.WordFrequencyCounter;
import ir.assignments.helpers.Frequency;
import ir.assignments.util.Util;
import org.apache.http.Header;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;
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
    Boolean notArchiveDatasets = !href.contains("archive.ics.uci.edu/ml/datasets");
    Boolean notArchiveDtabases = !href.contains("archive.ics.uci.edu/ml/machine-learning-databases/");
    Boolean notMailman = !href.contains("mailman.ics.uci.edu/");
//    Boolean notMlearnDatasets = !href.contains("mlearn.ics.uci.edu/databases");
    Boolean notFano = !href.contains("fano.ics.uci.edu/cites");
    Boolean notGraphMod = !href.contains("graphmod.ics.uci.edu");
    Boolean notDonCode = !href.contains("djp3-pc2");
    Boolean notDrazius = !href.contains("drzaius.ics.uci.edu");
    Boolean uciDomain = href.contains(".ics.uci.edu");
    Boolean notAlreadyVisited = !this.visitedMap.containsKey(url.hashCode());
    Boolean okayToVisit = !BINARY_FILES_EXTENSIONS.matcher(href).matches() &&
                          uciDomain && notDuttGroup && notEppsteinPics && notArchiveDatasets &&
                          notMailman && notFano && notArchiveDtabases && notGraphMod &&
                          notDonCode && notDrazius && notAlreadyVisited;
    return okayToVisit;
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */

  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();
    String anchor = page.getWebURL().getAnchor();
    String freqFileName = url.hashCode() + ".txt";
    String pathString = Paths.get("").toAbsolutePath().toString();

    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();
      Set<Integer> outGoingLinks = new HashSet<Integer>();
      System.out.println("URL: " + url + " : " + url.hashCode());
      /**
       * Writing urls to log file. Might be helpful...
       */
      /*
      for (WebURL link : links) {
        if (shouldVisit(page, link)) {
          outGoingLinks.add(link.hashCode());
        }
      }
      try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("./logs/" + logFileName, true))) {
        logWriter.write("URL: " + url + " : " + url.hashCode());
        logWriter.newLine();
        logWriter.flush();
      } catch (IOException e) {
        System.err.println(e);
      }

      List<String> words = Utilities.tokenizeString(text);
      List<Frequency> frequencies = WordFrequencyCounter.computeWordFrequencies(words);
      String freqFilePath = pathString + "/freqFiles/" + freqFileName;

      try (BufferedWriter siteWriter = new BufferedWriter(new FileWriter(freqFilePath))) {
        siteWriter.write("URL: " + url);
        siteWriter.newLine();
        siteWriter.write("Anchor: " + Utilities.tokenizeString(anchor).toString());
        siteWriter.newLine();
        siteWriter.write("Outgoing: " + outGoingLinks.toString());
        siteWriter.newLine();
        siteWriter.flush();
      } catch (IOException e) {
        System.err.println(e);
      }
      */
      String dataFilesFolder = pathString + "/dataFiles/";

      try (BufferedWriter infoWrite = new BufferedWriter(new FileWriter(dataFilesFolder + "title_info.txt", true))) {
        infoWrite.write(url.hashCode() + " : " + Utilities.tokenizeString(htmlParseData.getTitle()));
        infoWrite.newLine();
        infoWrite.flush();
      } catch (IOException e) {
        System.err.println(e);
      }
      try (BufferedWriter infoWrite = new BufferedWriter(new FileWriter(dataFilesFolder + "date_info.txt", true))) {
        if (htmlParseData.getMetaTags().containsKey("date")) {
          infoWrite.write(url.hashCode() + " : " + htmlParseData.getMetaTags().get("date"));
          infoWrite.newLine();
          infoWrite.flush();
        }
      } catch (IOException e) {
        System.err.println(e);
      }

//      Utilities.printFrequenciesToFile(frequencies, freqFilePath, url, false);
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