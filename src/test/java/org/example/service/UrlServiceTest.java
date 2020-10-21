package org.example.service;

import org.example.entity.URL;
import org.example.repository.UrlRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("/application-test.properties")
public class UrlServiceTest {
    @Autowired
    private UrlService service;

    @MockBean
    private UrlRepository repository;

    @Before
    public void setUp() throws Exception {
        URL url1 = new URL();
        url1.setId(1L);
        url1.setCreatedTime(LocalDateTime.now());
        url1.setOriginalUrl("https://google.com");
        url1.setShortUrl("http://localhost:8080/heoy5mo");
        url1.setUrlValue("https://goo.gl/heoy5mo");

        URL url2 = new URL();
        url2.setId(2L);
        url2.setCreatedTime(LocalDateTime.now());
        url2.setOriginalUrl("https://github.com/AndrejsKrilovs");
        url2.setShortUrl("http://localhost:8080/ehnpvo3i");
        url2.setUrlValue("https://goo.gl/ehnpvo3i");

        doReturn(new PageImpl<>(List.of(url1, url2))).when(repository)
                .findAll(PageRequest.of(0, 50, Sort.by("createdTime").descending()));
        doReturn(url2).when(repository)
                .findByShortUrlContainingIgnoreCase("ehnpvo3i");
        doReturn(List.of(url1)).when(repository)
                .filterUrls("goo", PageRequest.of(0, 50, Sort.by("createdTime").descending()));
    }

    @Test
    public void findAllUrls() {
        List<URL> findAllUrls = service.findAllUrls(0);
        assertEquals(2, findAllUrls.size());
    }

    @Test
    public void filterUrlList() {
        List<URL> findAllUrls = service.filterUrlList("goo", 0);
        assertEquals(1, findAllUrls.size());
    }

    @Test
    public void emptyFilterUrlList() {
        List<URL> findAllUrls = service.filterUrlList("", 0);
        assertNotNull(findAllUrls);
        assertEquals(2, findAllUrls.size());
    }

    @Test
    public void getUrlByKey() {
        Optional<URL> entity = service.getUrlByKey("ehnpvo3i");
        assertFalse(entity.isEmpty());
        assertEquals("https://github.com/AndrejsKrilovs", entity.orElse(new URL()).getOriginalUrl());
    }

    @Test
    public void generateSuccessUrl() {
        URL url = service.generateUrl("https://google.com");
        assertNotNull(url);
        assertNotNull(url.getCreatedTime());
        assertTrue(containsString("goo.gl").matches(url.getUrlValue()));
        verify(repository, times(1)).save(url);
    }

    @Test
    public void notGenerateUrl() {
        URL url = service.generateUrl("https://www.google.com");
        assertNotNull(url);
        assertNull(url.getCreatedTime());
        verify(repository, times(0)).save(url);
    }
}