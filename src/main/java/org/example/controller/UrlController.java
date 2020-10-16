package org.example.controller;

import org.example.entity.URL;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UrlController {

    private static final String SHORT_URL_KEY = "shortUrl";
    private static final String URL_LIST_KEY = "urlList";
    private static final String CORRECT_URL_PATTERN = "https://[a-z]{1,}.[a-z]{2,3}.*{0,}";
    private static final String URL_POSSIBLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final List<URL> urlList;

    public UrlController(List<URL> urlList) {
        this.urlList = urlList;
    }

    @GetMapping
    public String initPage(Map<String, Object> model) {
        model.put(SHORT_URL_KEY, "");
        model.put(URL_LIST_KEY, List.of());
        return "main";
    }

    @PostMapping
    public String sendValue(Map<String, Object> model, @RequestParam String originalUrl) {
        Pattern pattern = Pattern.compile(CORRECT_URL_PATTERN);
        Matcher matcher = pattern.matcher(originalUrl);

        if(originalUrl.trim().length() > 0 && matcher.matches()) {
            URL generatedUrl = generateUrl(originalUrl);
            model.put(SHORT_URL_KEY, String.format("http://localhost:8080/%s", generatedUrl.getGeneratedKey()));
            urlList.add(generatedUrl);
        } else {
            model.put(SHORT_URL_KEY, "");
        }

        model.put(URL_LIST_KEY, urlList);
        return "main";
    }

    @ResponseBody
    @GetMapping("{key}")
    public void redirectPage(HttpServletResponse response, @PathVariable String key) throws IOException {
        URL entity = urlList.stream()
                .filter(item -> item.getGeneratedKey().equals(key))
                .findFirst().orElseThrow(RuntimeException::new);
        response.sendRedirect(entity.getOriginalUrl());
    }

    private URL generateUrl(String originalUrl) {
        String shortenDomain = originalUrl.substring(0,13);
        StringBuilder newLink = new StringBuilder();

        newLink.append(shortenDomain.substring(0,11));
        newLink.append(".");
        newLink.append(shortenDomain.substring(11));
        newLink.append("/");

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int index = Math.toIntExact(Math.round(Math.random() * URL_POSSIBLE_CHARACTERS.length()));
            Character c = URL_POSSIBLE_CHARACTERS.charAt(index);
            key.append(c);
        }

        newLink.append(key);

        URL url = new URL();
        url.setGeneratedKey(key.toString());
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(newLink.toString());
        url.setTime(LocalDateTime.now());

        return url;
    }
}