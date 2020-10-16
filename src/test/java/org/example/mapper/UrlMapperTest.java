package org.example.mapper;

import org.example.dto.UrlDTO;
import org.example.entity.URL;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UrlMapperTest {

    private final UrlMapper mapper = new UrlMapper();

    @Test
    public void toDto() {
        URL entity = new URL();
        entity.setTime(LocalDateTime.now());
        entity.setOriginalUrl("https://google.com");
        entity.setShortUrl("http://localhost:8080/uR347ht");
        entity.setValue("https://goo.gl/uR347ht");

        UrlDTO dto = mapper.toDto(entity);
        Assert.assertNotNull(dto.getTime());
        Assert.assertEquals("https://google.com", dto.getOriginalUrl());
        Assert.assertEquals("http://localhost:8080/uR347ht", dto.getShortUrl());
        Assert.assertEquals("https://goo.gl/uR347ht", dto.getValue());
    }

    @Test
    public void fromDto() {
        UrlDTO dto = new UrlDTO();
        dto.setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setOriginalUrl("https://google.com");
        dto.setShortUrl("http://localhost:8080/uR347ht");
        dto.setValue("https://goo.gl/uR347ht");

        URL url = mapper.fromDto(dto);
        Assert.assertNotNull(url.getTime());
        Assert.assertEquals("https://google.com", url.getOriginalUrl());
        Assert.assertEquals("http://localhost:8080/uR347ht", url.getShortUrl());
        Assert.assertEquals("https://goo.gl/uR347ht", url.getValue());

    }
}