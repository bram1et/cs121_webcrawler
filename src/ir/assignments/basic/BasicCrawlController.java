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

import com.sun.prism.impl.BaseMesh;
import ir.assignments.crawler.CrawlConfig;
import ir.assignments.crawler.CrawlController;
import ir.assignments.fetcher.PageFetcher;
import ir.assignments.robotstxt.RobotstxtConfig;
import ir.assignments.robotstxt.RobotstxtServer;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import java.io.*;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class BasicCrawlController {
//  private static Logger logger = LoggerFactory.getLogger(BasicCrawlController.class);

  private static long startTime;
  private static long endTime;
  private static long totalTime;
  private static long totalTimeSeconds;
  private static int sitesCrawled;

  public long getStartTime() {
    return startTime;
  }

  private static void stuffToDoBeforeCrawl() {
    File freqFiles = new File("./freqFiles");
//    IO.deleteFolderContents(freqFiles);
    startTime = System.nanoTime();
  }

  private static void stuffToDoAfterCrawl() {
    endTime = System.nanoTime();
    totalTime = endTime - startTime;
    totalTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(totalTime);
    sitesCrawled = new File("./freqFiles").listFiles().length;
    System.out.println("Crawled sites: " + sitesCrawled);
    if (totalTimeSeconds < 60) {
      System.out.println("It took about " + totalTimeSeconds + " seconds");
    } else if (totalTimeSeconds >= 60 && totalTimeSeconds < 3600) {
      System.out.println("It took about " + TimeUnit.SECONDS.toMinutes(totalTimeSeconds) + " minute(s)");
    } else {
      System.out.println("It took about " + TimeUnit.SECONDS.toHours(totalTimeSeconds) + " hour(s)");
    }
  }

  private static CrawlConfig setupConfig() {
    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
    String crawlStorageFolder = "./";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */

    CrawlConfig config = new CrawlConfig();

    config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
    config.setPolitenessDelay(1500);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
    config.setMaxDepthOfCrawling(-1);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
    config.setMaxPagesToFetch(-1);

    /**
     * Do you want crawler4j to crawl also binary data ?
     * example: the contents of pdf, or the metadata of images etc
     */
    config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
    config.setResumableCrawling(true);

//    config.setUserAgentString("UCI Inf141-CS121 crawler 75542500 28239807 26447410 49859223");
    config.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.13 (KHTML, like Gecko) Version/9.1 Safari/601.5.13");
    return config;
  }

  public static void crawl() throws Exception{
    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
    String crawlStorageFolder = "./";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
    int numberOfCrawlers = 4;

    CrawlConfig config = new CrawlConfig();

    config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
    config.setPolitenessDelay(600);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
    config.setMaxDepthOfCrawling(-1);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
    config.setMaxPagesToFetch(10);

    /**
     * Do you want crawler4j to crawl also binary data ?
     * example: the contents of pdf, or the metadata of images etc
     */
    config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
    config.setResumableCrawling(true);

    config.setUserAgentString("UCI Inf141-CS121 crawler 75542500 28239807 26447410 49859223");

    /*
     * Instantiate the controller for this crawl.
     */
    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
    controller.addSeed("http://www.ics.uci.edu/");

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
    stuffToDoBeforeCrawl();
    controller.start(BasicCrawler.class, numberOfCrawlers);
    stuffToDoAfterCrawl();

  }

  public static void crawl(String seedURL) throws Exception{
    int numberOfCrawlers = 1;
    CrawlConfig config = setupConfig();
    config.setMaxDepthOfCrawling(0);
    config.setResumableCrawling(false);
    /*
     * Instantiate the controller for this crawl.
     */
    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
    controller.addSeed(seedURL);

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
    stuffToDoBeforeCrawl();
    controller.start(BasicCrawler.class, numberOfCrawlers);
    stuffToDoAfterCrawl();

  }

  public static void crawl(List<String> seedURLs) throws Exception {
    int numberOfCrawlers = 4;
    CrawlConfig config = setupConfig();
    config.setMaxDepthOfCrawling(0);
    config.setResumableCrawling(true);
    /*
     * Instantiate the controller for this crawl.
     */
    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
    for (String url : seedURLs) {
      controller.addSeed(url);
    }
    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
    stuffToDoBeforeCrawl();
    controller.start(BasicCrawler.class, numberOfCrawlers);
    stuffToDoAfterCrawl();
  }

  public static void main(String[] args) throws Exception {
    crawl("http://cml.ics.uci.edu/");
  }
}