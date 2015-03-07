package com.pricealert.scraping;

import com.pricealert.scraping.yql.YQLTemplate;
import com.pricealert.scraping.yql.model.YQLCSSQuery;
import com.pricealert.scraping.yql.model.YQLResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class YQLTest {

    @Test
    public void testQuery() throws IOException {
        YQLTemplate template = new YQLTemplate();
        YQLCSSQuery yqlcssQuery = new YQLCSSQuery();
        yqlcssQuery.setCssSelector(Arrays.asList("span.time_rtq_ticker", "div#yfi_related_tickers span a", "div.title h2"));
        yqlcssQuery.setUrl(String.format("http://finance.yahoo.com/q?s=%s", "PRPFX"));

        YQLResponse response = template.cssQuery(yqlcssQuery);
        assertNotNull(response);
    }
}
