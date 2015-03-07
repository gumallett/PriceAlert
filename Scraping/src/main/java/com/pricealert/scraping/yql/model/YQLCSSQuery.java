package com.pricealert.scraping.yql.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Class to represent a YQL request
 */
public class YQLCSSQuery {

    private static final Logger LOG = LoggerFactory.getLogger(YQLCSSQuery.class);

    private List<String> cssSelector;
    private String url;

    public List<String> getCssSelector() {
        return cssSelector;
    }

    public void setCssSelector(List<String> cssSelector) {
        this.cssSelector = cssSelector;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for(String s : getCssSelector()) {
            builder.append("css='");
            builder.append(s);
            builder.append("'");
            i++;

            if(i < getCssSelector().size()) {
                builder.append(" OR ");
            }
        }

        try {
            return URLEncoder.encode(String.format("SELECT * FROM data.html.cssselect WHERE url='%s' AND (%s)", getUrl(), builder.toString()), "UTF-8");
        }
        catch(UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }

        return "";
    }
}
