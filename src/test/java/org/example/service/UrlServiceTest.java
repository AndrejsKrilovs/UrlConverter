package org.example.service;

import org.example.entity.URL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class UrlServiceTest {

    private final UrlService service = new UrlService();

    @Test
    public void generateUrlSuccessTest() {
        Optional<URL> url = service.generateUrl("https://google.com");
        String time = url.map(URL::getTime).orElse(null);
        String shortURL = url.map(URL::getShortUrl).orElse(null);

        Assert.assertFalse(url.isEmpty());
        Assert.assertNotNull(time);
        Assert.assertNotNull(shortURL);
    }

    @Test
    public void generateUrlSuccessFailed() {
        Optional<URL> url = service.generateUrl("https://google.com");
        Assert.assertTrue(url.isEmpty());
    }
}
