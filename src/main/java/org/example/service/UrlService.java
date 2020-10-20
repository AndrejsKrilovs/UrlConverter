package org.example.service;

import org.example.entity.URL;
import org.example.repository.UrlRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private static final String URL_POSSIBLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CORRECT_URL_PATTERN = "https://[a-z]{5,}.[a-z]{2,3}.*{0,}";
    private static final String SHORTEN_URL_FORMAT = "http://localhost:8080/%s";
    private static final String SORT_COLUMN = "createdTime";
    private static final Integer ITEMS_PER_PAGE = 50;
    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public List<URL> findAllUrls(Integer page) {
        return repository.findAll(PageRequest.of(page, ITEMS_PER_PAGE, Sort.by(SORT_COLUMN).descending()))
                .stream()
                .collect(Collectors.toList());
    }

    public List<URL> filterUrlList(String filter, Integer page) {
        if(filter == null || filter.isBlank()) {
            return findAllUrls(0);
        } else {
            return repository.filterUrls(
                    filter,
                    PageRequest.of(page, ITEMS_PER_PAGE, Sort.by(SORT_COLUMN).descending())
            );
        }
    }

    public Optional<URL> getUrlByKey(String key) {
        return Optional.of(repository.findByShortUrlContainingIgnoreCase(key));
    }

    public void generateUrl(String originalUrl) {
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
            url.setUrlValue(newLink.toString());
            url.setCreatedTime(LocalDateTime.now());
            repository.save(url);
        }
    }
}
