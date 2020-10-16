package org.example.mapper;

import org.example.dto.UrlDTO;
import org.example.entity.URL;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UrlMapper {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy kk:mm:ss";

    public UrlDTO toDto(URL url) {
        UrlDTO result = new UrlDTO();
        result.setTime(url.getTime().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        result.setOriginalUrl(url.getOriginalUrl());
        result.setShortUrl(url.getShortUrl());
        result.setValue(url.getValue());
        return result;
    }

    public URL fromDto(UrlDTO urlDTO) {
        URL result = new URL();
        result.setTime(LocalDateTime.parse(urlDTO.getTime()));
        result.setOriginalUrl(urlDTO.getOriginalUrl());
        result.setShortUrl(urlDTO.getShortUrl());
        result.setValue(urlDTO.getValue());
        return result;
    }
}
