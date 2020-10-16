package org.example.service;

import org.example.entity.URL;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlService {

    private static final String URL_POSSIBLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CORRECT_URL_PATTERN = "https://[a-z]{1,}.[a-z]{2,3}.*{0,}";
    private static final String SHORTEN_URL_FORMAT = "http://localhost:8080/%s";

    public Optional<URL> generateUrl(String originalUrl) {
        Pattern pattern = Pattern.compile(CORRECT_URL_PATTERN);
        Matcher matcher = pattern.matcher(originalUrl);

        if(originalUrl.trim().length() > 0 && !originalUrl.contains("www") && matcher.matches()) {
            String shortenDomain = originalUrl.substring(0, 13);
            StringBuilder newLink = new StringBuilder();

            newLink.append(shortenDomain.substring(0, 11));
            newLink.append(".");
            newLink.append(shortenDomain.substring(11));
            newLink.append("/");

            StringBuilder key = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                int index = Math.toIntExact(Math.round(Math.random() * URL_POSSIBLE_CHARACTERS.length()));
                char c = URL_POSSIBLE_CHARACTERS.charAt(index);
                key.append(c);
            }

            newLink.append(key);

            URL url = new URL();
            url.setShortUrl(String.format(SHORTEN_URL_FORMAT, key.toString()));
            url.setOriginalUrl(originalUrl);
            url.setValue(newLink.toString());
            url.setTime(LocalDateTime.now());

            return Optional.of(url);
        } else {
            return Optional.empty();
        }
    }
}
