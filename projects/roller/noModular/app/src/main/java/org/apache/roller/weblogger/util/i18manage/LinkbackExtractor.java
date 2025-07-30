/*
* Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.roller.weblogger.util.i18manage;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.Parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Parses HTML file for referring linkback title and excerpt.
 * 
 * @author David M Johnson
 */
public class LinkbackExtractor
{
    public static Log mLogger        = LogFactory.getFactory().getInstance(
                                              LinkbackExtractor.class);
    public boolean    mFound         = false;
    public String     mTitle         = "";
    public String     mRssLink       = null;
    private String     mExcerpt       = null;
    private String     mPermalink     = null;
    public int        mStart         = 0;
    public int        mEnd           = 0;
    public String     mRequestURL    = null;
    public String     mRequestURLWWW = null;
    public String     mRefererURL;

    private static final int MAX_EXCERPT_CHARS = 500;
    public static final int DESIRED_TITLE_LENGTH = 50;

    //------------------------------------------------------------------------
    /**
     * Extract referring page title, excerpt, and permalink.
     * 
     * @param refererURL
     * @param requestURL
     */
    public LinkbackExtractor(String refererURL, String requestURL) throws IOException {
        try {
            extractByParsingHtml(refererURL, requestURL);
            if (mRssLink != null) {
                extractByParsingRss(mRssLink, requestURL);
            }
        } catch (Exception e) {
            if (mLogger.isDebugEnabled()) {
                mLogger.debug("Extracting linkback", e);
            }
        }
    }

    //------------------------------------------------------------------------
    private void extractByParsingHtml(String refererURL, String requestURL) throws IOException {
        URL url = new URL(refererURL);
        InputStream is = url.openStream();

        mRefererURL = refererURL;

        if (requestURL.startsWith("http://www.")) {
            mRequestURLWWW = requestURL;
            mRequestURL = "http://" + mRequestURLWWW.substring(11);
        } else {
            mRequestURL = requestURL;
            mRequestURLWWW = "http://www." + mRequestURL.substring(7);
        }

        // Trick gets Swing's HTML parser by making its protected getParser() method public
        // Ignore inaccurate Sonar complaint about useless overriding method:
        //    http://jira.codehaus.org/browse/SONARJAVA-287
        Parser parser = (new HTMLEditorKit() {
            @Override
            public Parser getParser() {
                return super.getParser();
            }
        }).getParser();

        // Read HTML file into string
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            br.close();
        }

        // Parse HTML string to find title and start and end position
        // of the referring excerpt.
        StringReader sr = new StringReader(sb.toString());
        parser.parse(sr, new LinkbackCallback(this), true);

        if (mStart != 0 && mEnd != 0 && mEnd > mStart) {
            mExcerpt = sb.toString().substring(mStart, mEnd);
            mExcerpt = Utilities.removeHTML(mExcerpt);

            if (mExcerpt.length() > MAX_EXCERPT_CHARS) {
                mExcerpt = mExcerpt.substring(0, MAX_EXCERPT_CHARS) + "...";
            }
        }

        if (mTitle.startsWith(">") && mTitle.length() > 1) {
            mTitle = mTitle.substring(1);
        }
    }

    //------------------------------------------------------------------------
    private void extractByParsingRss(String rssLink, String requestURL)
            throws FeedException, IOException {
        SyndFeedInput feedInput = new SyndFeedInput();       
        SyndFeed feed = feedInput.build(
            new InputStreamReader(new URL(rssLink).openStream()));
        String feedTitle = feed.getTitle();

        int count = 0;

        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Feed parsed, title: " + feedTitle);
        }

        for (Object objItem : feed.getEntries()) {
            count++;
            SyndEntry item = (SyndEntry) objItem;
            if (item.getDescription().getValue().contains(requestURL)) {
                mFound = true;
                mPermalink = item.getLink();
                if (feedTitle != null && !feedTitle.isBlank()) {
                    mTitle = feedTitle + ": " + item.getTitle();
                } else {
                    mTitle = item.getTitle();
                }
                mExcerpt = item.getDescription().getValue();
                mExcerpt = Utilities.removeHTML(mExcerpt);
                if (mExcerpt.length() > MAX_EXCERPT_CHARS) {
                    mExcerpt = mExcerpt.substring(0, MAX_EXCERPT_CHARS) + "...";
                }
                break;
            }
        }

        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Parsed " + count + " articles, found linkback=" + mFound);
        }
    }

    //------------------------------------------------------------------------
    /**
     * Returns the excerpt.
     * 
     * @return String
     */
    public String getExcerpt() {
        return mExcerpt;
    }

    //------------------------------------------------------------------------
    /**
     * Returns the title.
     * 
     * @return String
     */
    public String getTitle() {
        return mTitle;
    }

    //------------------------------------------------------------------------
    /**
     * Returns the permalink.
     * 
     * @return String
     */
    public String getPermalink() {
        return mPermalink;
    }

    //------------------------------------------------------------------------
    /**
     * Sets the permalink.
     * 
     * @param permalink
     *            The permalink to set
     */
    public void setPermalink(String permalink)
    {
        mPermalink = permalink;
    }

    /////////////////////////////////////////////////////////////////////////

}

