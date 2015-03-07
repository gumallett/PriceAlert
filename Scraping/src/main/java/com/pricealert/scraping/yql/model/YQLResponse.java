package com.pricealert.scraping.yql.model;

/**
 * Bean encapsulating a YQL response.
 */
public class YQLResponse {

    private YQueryResponse query;

    public YQueryResponse getQuery() {
        return query;
    }

    public void setQuery(YQueryResponse query) {
        this.query = query;
    }
}
