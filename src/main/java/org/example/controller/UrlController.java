package org.example.controller;

import org.example.dto.UrlDTO;
import org.example.mapper.UrlMapper;
import org.example.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UrlController {

    private static final Logger ERROR_LOG = LoggerFactory.getLogger(UrlController.class);
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
    public String filterData(Map<String, Object> model,
                             @RequestParam(required = false, defaultValue = "") String filter) {

        if(filter == null || filter.isEmpty()) {
            urlList.sort(Collections.reverseOrder());
            model.put(URL_LIST_KEY, urlList);
        } else {
            List<UrlDTO> filteredList = urlList.stream()
                    .filter(item ->
                            item.getTime().contains(filter) ||
                                    item.getOriginalUrl().contains(filter) ||
                                    item.getValue().contains(filter)
                    ).sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());

            model.put(URL_LIST_KEY, filteredList);
        }

        return "main";
    }

    @PostMapping
    public String convert(Map<String, Object> model, @RequestParam String originalUrl) {
        service.generateUrl(originalUrl)
                .map(mapper::toDto)
                .ifPresent(item -> {
                    urlList.add(item);
                    urlList.sort(Collections.reverseOrder());
                });

        model.put(URL_LIST_KEY, urlList);
        return "main";
    }

    @ResponseBody
    @GetMapping("{key}")
    public void redirectPage(HttpServletResponse response, @PathVariable String key) {
        urlList.stream()
                .filter(item -> item.getShortUrl().contains(key))
                .findFirst()
                .ifPresent(item -> {
                    try {
                        response.sendRedirect(item.getOriginalUrl());
                    } catch (IOException e) {
                        ERROR_LOG.error("Redirection error:", e);
                    }
                });
    }
}
