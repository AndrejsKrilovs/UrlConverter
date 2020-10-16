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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UrlController {

    private static final String URL_LIST_KEY = "urlList";
    private static final String CORRECT_URL_PATTERN = "https://[a-z]{1,}.[a-z]{2,3}.*{0,}";
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
        Pattern pattern = Pattern.compile(CORRECT_URL_PATTERN);
        Matcher matcher = pattern.matcher(originalUrl);

        if(originalUrl.trim().length() > 0 && !originalUrl.contains("www") && matcher.matches()) {
            URL generatedUrl = service.generateUrl(originalUrl);
            this.urlList.add(generatedUrl);
        }

        urlList.sort(Collections.reverseOrder());
        model.put(URL_LIST_KEY, urlList);
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
