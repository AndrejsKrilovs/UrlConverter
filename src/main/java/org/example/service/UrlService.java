package org.example.service;

import org.example.entity.URL;
import org.example.repository.UrlRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class which provides main logic of the application.
 */
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

    /**
     * Method that return sorted Available URL collection.
     * @param page - size, how many elements will be shown. 1 page = 50 elements
     * @return list of sorted URLs
     */
    public List<URL> findAllUrls(Integer page) {
        return repository.findAll(PageRequest.of(page, ITEMS_PER_PAGE, Sort.by(SORT_COLUMN).descending()))
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Method that return sorted Filtered URL collection.
     * @param filter - criteria to find
     * @param page - size, how many elements will be shown. 1 page = 50 elements
     * @return list of filtered and sorted URLs
     */
    public List<URL> filterUrlList(String filter, Integer page) {
        if(StringUtils.isEmpty(filter)) {
            return findAllUrls(0);
        } else {
            return repository.filterUrls(
                    filter,
                    PageRequest.of(page, ITEMS_PER_PAGE, Sort.by(SORT_COLUMN).descending())
            );
        }
    }

    /**
     * Method that finds full URL by key.
     * @param key - key which contains in list
     * @return original URL which contains generated key
     */
    public Optional<URL> getUrlByKey(String key) {
        return Optional.of(repository.findByShortUrlContainingIgnoreCase(key));
    }

    /**
     * Method that generates shor URL from original
     * @param originalUrl - original valid URL which are pass from outside
     * @return generated short URL or nothing (if incoming URL is not valid)
     */
    public URL generateUrl(String originalUrl) {
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

            return url;
        } else {
            return new URL();
        }
    }
}
