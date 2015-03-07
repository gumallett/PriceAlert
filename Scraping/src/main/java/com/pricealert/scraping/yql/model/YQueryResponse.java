package com.pricealert.scraping.yql.model;

import com.fasterxml.jackson.databind.JsonNode;

public class YQueryResponse {
    private int count;
    private String created;
    private String lang;
    private YQueryResultsContainer results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public YQueryResultsContainer getResults() {
        return results;
    }

    public void setResults(YQueryResultsContainer results) {
        this.results = results;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public static class YQueryResultsContainer {

        private JsonNode results;

        public JsonNode getResults() {
            return results;
        }

        public void setResults(JsonNode results) {
            this.results = results;
        }
    }
}
