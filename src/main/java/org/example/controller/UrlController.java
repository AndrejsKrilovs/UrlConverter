package org.example.controller;

import org.example.entity.URL;
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
    private final List<URL> urlList;
    private final UrlService service;

    public UrlController(UrlService service) {
        this.urlList = new ArrayList<>();
        this.service = service;
    }

    @GetMapping
    public String initPage(Map<String, Object> model) {
        model.put(URL_LIST_KEY, Collections.EMPTY_LIST);
        return "main";
    }

    @PostMapping
    public String sendValue(Map<String, Object> model, @RequestParam String originalUrl) {
        service.generateUrl(originalUrl).ifPresent(item -> {
            urlList.add(item);
            urlList.sort(Collections.reverseOrder());
            model.put(URL_LIST_KEY, urlList);
        });
        return "main";
    }

    @ResponseBody
    @GetMapping("{key}")
    public void redirectPage(HttpServletResponse response, @PathVariable String key) throws IOException {
        URL entity = urlList.stream()
                .filter(item -> item.getShortUrl().contains(key))
                .findFirst().orElse(new URL());
        response.sendRedirect(entity.getOriginalUrl());
    }
}
