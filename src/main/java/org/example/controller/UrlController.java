package org.example.controller;

import org.example.dto.UrlDTO;
import org.example.mapper.UrlMapper;
import org.example.service.UrlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class UrlController {

    private static final String URL_LIST_KEY = "urlList";
    private final List<UrlDTO> urlList;
    private final UrlService service;
    private final UrlMapper mapper;

    public UrlController(UrlService service, UrlMapper mapper) {
        this.urlList = new ArrayList<>();
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public String initPage(Map<String, Object> model) {
        model.put(URL_LIST_KEY, Collections.EMPTY_LIST);
        return "main";
    }

    @PostMapping
    public String sendValue(Map<String, Object> model, @RequestParam String originalUrl) {
        service.generateUrl(originalUrl)
                .map(mapper::toDto)
                .ifPresent(item -> {
                    urlList.add(item);
                    urlList.sort(Collections.reverseOrder());
                    model.put(URL_LIST_KEY, urlList);
                });

        return "main";
    }

    @ResponseBody
    @GetMapping("{key}")
    public void redirectPage(HttpServletResponse response, @PathVariable String key) throws IOException {
        UrlDTO entity = urlList.stream()
                .filter(item -> item.getShortUrl().contains(key))
                .findFirst().orElse(new UrlDTO());
        response.sendRedirect(entity.getOriginalUrl());
    }
}
