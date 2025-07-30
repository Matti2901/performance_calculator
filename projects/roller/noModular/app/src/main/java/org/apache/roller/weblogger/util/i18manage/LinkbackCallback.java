package org.apache.roller.weblogger.util.i18manage;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Parser callback that finds title and excerpt. As we walk through the HTML
 * tags, we keep track of the most recently encountered divider tag in the
 * mStart field. Once we find the referring permalink, we set the mFound
 * flag. After that, we look for the next divider tag and save it's position
 * in the mEnd field.
 */
final class LinkbackCallback extends HTMLEditorKit.ParserCallback {
    private final LinkbackExtractor linkbackExtractor;
    // Dividers
    private HTML.Tag[] mDivTags = {HTML.Tag.TD, HTML.Tag.DIV, HTML.Tag.SPAN,
            HTML.Tag.BLOCKQUOTE, HTML.Tag.P, HTML.Tag.LI,
            HTML.Tag.BR, HTML.Tag.HR, HTML.Tag.PRE, HTML.Tag.H1,
            HTML.Tag.H2, HTML.Tag.H3, HTML.Tag.H4, HTML.Tag.H5,
            HTML.Tag.H6};

    private List<HTML.Tag> mList = Arrays.asList(mDivTags);

    private HTML.Tag mCurrentTag = null;

    public LinkbackCallback(LinkbackExtractor linkbackExtractor) {
        this.linkbackExtractor = linkbackExtractor;
    }

    /**
     * Look for divider tags and for the permalink.
     *
     * @param tag  HTML tag
     * @param atts Attributes of that tag
     * @param pos  Tag's position in file
     */
    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet atts, int pos) {
        if (mList.contains(tag) && !linkbackExtractor.mFound) {
            linkbackExtractor.mStart = pos;
        } else if (mList.contains(tag) && linkbackExtractor.mFound && linkbackExtractor.mEnd == 0) {
            linkbackExtractor.mEnd = pos;
        } else if (tag.equals(HTML.Tag.A)) {
            String href = (String) atts.getAttribute(HTML.Attribute.HREF);
            if (href == null) {
                return;
            }
            int hashPos = href.lastIndexOf('#');
            if (hashPos != -1) {
                href = href.substring(0, hashPos);
            }
            if (href != null
                    && (href.equals(linkbackExtractor.mRequestURL) || href
                    .equals(linkbackExtractor.mRequestURLWWW))) {
                linkbackExtractor.mFound = true;
            }
        }
        mCurrentTag = tag;
    }

    /**
     * Needed to handle SPAN tag.
     */
    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet atts, int pos) {
        if (mList.contains(tag) && linkbackExtractor.mFound && linkbackExtractor.mEnd == 0) {
            linkbackExtractor.mEnd = pos;
        } else if (tag.equals(HTML.Tag.LINK)) {
            // Look out for RSS autodiscovery link
            String title = (String) atts.getAttribute(HTML.Attribute.TITLE);
            String type = (String) atts.getAttribute(HTML.Attribute.TYPE);
            if (title != null && type != null
                    && type.equals("application/rss+xml")
                    && title.equals("RSS")) {
                linkbackExtractor.mRssLink = (String) atts.getAttribute(HTML.Attribute.HREF);

                if (LinkbackExtractor.mLogger.isDebugEnabled()) {
                    LinkbackExtractor.mLogger.debug("Found RSS link " + linkbackExtractor.mRssLink);
                }

                if (linkbackExtractor.mRssLink.startsWith("/") && linkbackExtractor.mRssLink.length() > 1) {
                    try {
                        URL url = new URL(linkbackExtractor.mRefererURL);
                        linkbackExtractor.mRssLink = url.getProtocol() + "://"
                                + url.getHost() + ":" + url.getPort()
                                + linkbackExtractor.mRssLink;
                    } catch (MalformedURLException e) {
                        linkbackExtractor.mRssLink = null;
                        if (LinkbackExtractor.mLogger.isDebugEnabled()) {
                            LinkbackExtractor.mLogger.debug("Determining RSS URL", e);
                        }
                    }
                } else if (!linkbackExtractor.mRssLink.startsWith("http")) {
                    int slash = linkbackExtractor.mRefererURL.lastIndexOf('/');
                    if (slash != -1) {
                        linkbackExtractor.mRssLink = linkbackExtractor.mRefererURL.substring(0, slash) + "/"
                                + linkbackExtractor.mRssLink;
                    }
                }
                if (LinkbackExtractor.mLogger.isDebugEnabled()) {
                    LinkbackExtractor.mLogger.debug("Qualified RSS link is " + linkbackExtractor.mRssLink);
                }
            }
        }
    }

    /**
     * Stop at the very first divider tag after the permalink.
     *
     * @param tag End tag
     * @param pos Position in HTML file
     */
    @Override
    public void handleEndTag(HTML.Tag tag, int pos) {
        if (mList.contains(tag) && linkbackExtractor.mFound && linkbackExtractor.mEnd == 0) {
            linkbackExtractor.mEnd = pos;
        } else if (mList.contains(tag) && !linkbackExtractor.mFound) {
            linkbackExtractor.mStart = pos;
        } else {
            mCurrentTag = null;
        }
    }

    /**
     * Get the page title
     */
    @Override
    public void handleText(char[] data, int pos) {
        if (mCurrentTag != null && mCurrentTag.equals(HTML.Tag.TITLE)) {
            String newText = new String(data);
            if (linkbackExtractor.mTitle.length() < LinkbackExtractor.DESIRED_TITLE_LENGTH) {
                linkbackExtractor.mTitle += newText;
            }
        }
    }
}
