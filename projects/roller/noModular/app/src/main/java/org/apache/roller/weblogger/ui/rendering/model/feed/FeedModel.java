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

package org.apache.roller.weblogger.ui.rendering.model.feed;

import java.util.List;
import java.util.Map;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.strategy.URLStrategy;
import org.apache.roller.weblogger.business.weblog.WebloggerFactory;
import org.apache.roller.weblogger.config.WebloggerRuntimeConfig;
import org.apache.roller.weblogger.pojos.core.mediaDirectory.MediaFile;
import org.apache.roller.weblogger.pojos.core.weblog.Weblog;
import org.apache.roller.weblogger.pojos.wrapper.WeblogEntryCommentWrapper;
import org.apache.roller.weblogger.pojos.wrapper.WeblogEntryWrapper;
import org.apache.roller.weblogger.pojos.wrapper.WeblogWrapper;
import org.apache.roller.weblogger.ui.rendering.model.Model;
import org.apache.roller.weblogger.ui.rendering.pagers.Pager;
import org.apache.roller.weblogger.ui.rendering.util.WeblogFeedRequest;
import org.apache.roller.weblogger.ui.rendering.util.WeblogRequest;


/**
 * Model which provides information needed to render a feed.
 */
public class FeedModel implements Model {
    
    public static final int DEFAULT_ENTRIES = WebloggerRuntimeConfig.getIntProperty("site.newsfeeds.defaultEntries");
    
    private WeblogFeedRequest feedRequest = null;
    public URLStrategy urlStrategy = null;
    private Weblog weblog = null;
    
    
    @Override
    public void init(Map<String, Object> initData) throws WebloggerException {
        
        // we expect the init data to contain a weblogRequest object
        WeblogRequest weblogRequest = (WeblogRequest) initData.get("parsedRequest");
        if(weblogRequest == null) {
            throw new WebloggerException("expected weblogRequest from init data");
        }
        
        // PageModel only works on page requests, so cast weblogRequest
        // into a WeblogPageRequest and if it fails then throw exception
        if(weblogRequest instanceof WeblogFeedRequest) {
            this.feedRequest = (WeblogFeedRequest) weblogRequest;
        } else {
            throw new WebloggerException("weblogRequest is not a WeblogFeedRequest."+
                    "  FeedModel only supports feed requests.");
        }
        
        // look for url strategy
        urlStrategy = (URLStrategy) initData.get("urlStrategy");
        if(urlStrategy == null) {
            urlStrategy = WebloggerFactory.getWeblogger().getUrlStrategy();
        }
        
        // extract weblog object
        weblog = feedRequest.getWeblog();
    }
    
    
    /** Template context name to be used for model */
    @Override
    public String getModelName() {
        return "model";
    }
    
    
    /**
     * Get the weblog locale used to render this page, null if no locale.
     */
    public String getLocale() {
        return feedRequest.getLocale();
    }
    
    
    /**
     * Get weblog being displayed.
     */
    public WeblogWrapper getWeblog() {
        return WeblogWrapper.wrap(weblog, urlStrategy);
    }
    
    
    /**
     * Get category path or name specified by request.
     */
    public boolean getExcerpts() {
        return feedRequest.isExcerpts();
    }
    
    
    /**
     * Get category path or name specified by request.
     */
    public String getCategoryName() {
        return feedRequest.getWeblogCategoryName();
    }
    
    /**
     * Gets most recent entries limited by: weblog and category specified in 
     * request plus the weblog.entryDisplayCount.
     */
    public Pager<WeblogEntryWrapper> getWeblogEntriesPager() {
        return new FeedEntriesPager(this, feedRequest);
    }
    
    
    /**
     * Gets most recent comments limited by: weblog specified in request and 
     * the weblog.entryDisplayCount.
     */
    public Pager<WeblogEntryCommentWrapper> getCommentsPager() {
        return new FeedCommentsPager(this, feedRequest);
    }    
        
    /**
     * Gets most recently uploaded media files limited by: weblog specified 
     * in request and the weblog.entryDisplayCount.
     */
    public Pager<MediaFile> getMediaFilesPager() {
        return new FeedFilesPager(this, feedRequest);
    }    
        
    /**
     * Returns the list of tags specified in the request /?tags=foo+bar
     * @return
     */
    public List<String> getTags() {
        return feedRequest.getTags();
    }

}
