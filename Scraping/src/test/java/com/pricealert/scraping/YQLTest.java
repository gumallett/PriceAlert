package com.pricealert.scraping;

import com.pricealert.scraping.yql.YQLTemplate;
import com.pricealert.scraping.yql.model.YQLCSSQuery;
import com.pricealert.scraping.yql.model.YQLResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class YQLTest {

    private YQLTemplate template = new YQLTemplate();

    @Test
    public void testQuery() throws IOException {
        YQLCSSQuery yqlcssQuery = new YQLCSSQuery();
        yqlcssQuery.setCssSelector(Arrays.asList("span.time_rtq_ticker", "div#yfi_related_tickers span a", "div.title h2"));
        yqlcssQuery.setUrl(String.format("http://finance.yahoo.com/q?s=%s", "PRPFX"));

        YQLResponse response = template.cssQuery(yqlcssQuery);
        assertNotNull(response);
    }

    @Test
    @Ignore
    public void testAmazon() throws Exception {
        String url = "http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q";
        YQLCSSQuery yqlcssQuery = new YQLCSSQuery();
        yqlcssQuery.setUrl(url);
        yqlcssQuery.setCssSelector(Arrays.asList("#priceblock_ourprice"));
        YQLResponse response = template.cssQuery(yqlcssQuery);
        assertNotNull(response);
    }
}
