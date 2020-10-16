package org.example.service;

import org.example.entity.URL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class UrlServiceTest {

    private final UrlService service = new UrlService();

    @Test
    public void generateUrlSuccess() {
        Optional<URL> url = service.generateUrl("https://google.com");
        Assert.assertFalse(url.isEmpty());
    }

    @Test
    public void generateUrlFail() {
        Optional<URL> url = service.generateUrl("https://www.google.com");
        Assert.assertTrue(url.isEmpty());
    }
}
