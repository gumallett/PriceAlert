package com.pricealert.scraping.yql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pricealert.scraping.yql.model.YQLCSSQuery;
import com.pricealert.scraping.yql.model.YQLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

public class YQLTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(YQLTemplate.class);

    private static final String yqlUrl = "https://query.yahooapis.com/v1/public/yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public YQLTemplate() {
    }

    public YQLResponse cssQuery(YQLCSSQuery query) {
        String urlString = yqlUrl + query.toString();

        LOG.info("about to run yql query: {}", urlString);

        URL url;
        try {
            url = new URL(urlString);
        }
        catch(MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        //ResponseEntity<String> response = rest.exchange(uri, HttpMethod.GET, null, String.class);

        try {
            return exchange(url);
        }
        catch(IOException e) {
            LOG.error("Problem parsing json", e);
            throw new RuntimeException(e);
        }
    }

    private YQLResponse exchange(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                return handleResponse(conn, conn.getInputStream());
            }
            else {
                String error = handleError(conn, conn.getErrorStream());
                LOG.error("Response not OK: {}", error);
                throw new RuntimeException("Response not OK");
            }
        }
        finally {
            conn.disconnect();
        }
    }

    private YQLResponse handleResponse(HttpURLConnection conn, InputStream is) throws IOException {
        try {
            String body = readStreamToString(is, readCharset(conn));
            LOG.info("Response: {}", body);
            return objectMapper.readValue(body, YQLResponse.class);
        }
        finally {
            try {
                is.close();
            }
            catch(Exception e) {}
        }
    }

    private String handleError(HttpURLConnection conn, InputStream errorStream) {
        try {
            return readStreamToString(errorStream, readCharset(conn));
        }
        finally {
            try {
                errorStream.close();
            }
            catch(Exception e) {}
        }
    }

    private static String readStreamToString(InputStream inputStream, String charset) {
        Scanner s = new Scanner(inputStream, charset).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String readCharset(HttpURLConnection conn) {
        String contentType = conn.getHeaderField("Content-Type");
        if(contentType == null || contentType.isEmpty()) {
            return Charset.defaultCharset().toString();
        }

        String[] parts = contentType.split("charset=");
        return parts[1];
    }

}
