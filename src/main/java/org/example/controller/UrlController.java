package org.example.controller;

import org.example.service.UrlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class UrlController {

    private static final String URL_LIST_PROPERTY = "urlList";
    private static final String MAIN_PAGE_NAME = "main";
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @GetMapping
    public String initPage(Map<String, Object> model) {
        model.put(URL_LIST_PROPERTY, service.findAllUrls(0));
        return MAIN_PAGE_NAME;
    }

    @PostMapping
    public String generateUrl(Map<String, Object> model, @RequestParam String originalUrl) {
        service.generateUrl(originalUrl);
        model.put(URL_LIST_PROPERTY, service.findAllUrls(0));
        return MAIN_PAGE_NAME;
    }

    @GetMapping("filter")
    public String filterUrlValues(Map<String, Object> model, @RequestParam String param) {
        model.put(URL_LIST_PROPERTY, service.filterUrlList(param, 0));
        return "main";
    }

    @ResponseBody
    @GetMapping("{key}")
    public void redirectToOriginalUrl(HttpServletResponse response, @PathVariable String key) {
        service.getUrlByKey(key)
                .ifPresent(item -> {
                    try {
                        response.sendRedirect(item.getOriginalUrl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
