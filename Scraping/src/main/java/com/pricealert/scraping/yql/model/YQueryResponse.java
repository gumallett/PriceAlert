package com.pricealert.scraping.yql.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;

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

        public String getText(String... ids) {
            if(results == null) {
                return null;
            }

            if(results.isArray()) {
                Iterator<JsonNode> iterator = results.iterator();

                while(iterator.hasNext()) {
                    JsonNode node = iterator.next();

                    if(node == null || node.isNull()) {
                        continue;
                    }

                    List<JsonNode> idNodes = node.findParents("id");
                    for(JsonNode idNode : idNodes) {
                        for(String id : ids) {
                            if(id.equals(idNode.get("id").asText())) {
                                return idNode.findValue("content").asText();
                            }
                        }
                    }
                }
            }

            return textHelper(results, ids);
        }

        private String textHelper(JsonNode node, String... ids) {
            List<JsonNode> idNodes = node.findParents("id");
            for(JsonNode idNode : idNodes) {
                for(String id : ids) {
                    if(id.equals(idNode.get("id").asText())) {
                        return idNode.findValue("content").asText();
                    }
                }
            }

            return null;
        }
    }
}
