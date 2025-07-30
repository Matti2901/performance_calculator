package org.apache.roller.weblogger.ui.rendering.model.feed;

import org.apache.roller.weblogger.ui.rendering.pagers.CommentsPager;
import org.apache.roller.weblogger.ui.rendering.util.WeblogFeedRequest;
import org.apache.roller.weblogger.util.i18manage.URLUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedCommentsPager extends CommentsPager {

    private final FeedModel feedModel;
    private final WeblogFeedRequest feedRequest;

    public FeedCommentsPager(FeedModel feedModel, WeblogFeedRequest feedRequest) {
        super(feedModel.urlStrategy, feedModel.urlStrategy.getWeblogFeedURL(feedRequest.getWeblog(),
                feedRequest.getLocale(), feedRequest.getType(),
                feedRequest.getFormat(), null, null,
                null, false, true), feedRequest.getWeblog(), -1, feedRequest.getPage(), FeedModel.DEFAULT_ENTRIES);
        this.feedModel = feedModel;
        this.feedRequest = feedRequest;
    }

    @Override
    protected String createURL(String url, Map<String, String> params) {
        List<String> tags = feedRequest.getTags();
        if (tags != null && !tags.isEmpty()) {
            params.put("tags", URLUtilities.getEncodedTagsString(tags));
        }
        String category = feedRequest.getWeblogCategoryName();
        if (category != null && !category.isBlank()) {
            params.put("cat", URLUtilities.encode(category));
        }
        if (feedRequest.isExcerpts()) {
            params.put("excerpts", "true");
        }
        return super.createURL(url, params);
    }

    @Override
    public String getUrl() {
        return createURL(super.getUrl(), new HashMap<>());
    }
}
