package org.example.controller;

import org.example.service.UrlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Controller class which prepare data to render.
 */
@Controller
public class UrlController {

    private static final String URL_LIST_PROPERTY = "urlList";
    private static final String MAIN_PAGE_NAME = "main";
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    /**
     * Method that initialized main page. This is web application entry point
     * @param model - parameter map which are passed to html
     * @return paige template which need to be shown
     */
    @GetMapping
    public String initPage(Map<String, Object> model) {
        model.put(URL_LIST_PROPERTY, service.findAllUrls(0));
        return MAIN_PAGE_NAME;
    }

    /**
     * Method that generated shot URL.
     * @param model - parameter map which are passed to html
     * @param originalUrl - full valid URL without {@value www}  prefix
     * @return paige template which need to be shown
     */
    @PostMapping
    public String generateUrl(Map<String, Object> model, @RequestParam String originalUrl) {
        service.generateUrl(originalUrl);
        model.put(URL_LIST_PROPERTY, service.findAllUrls(0));
        return MAIN_PAGE_NAME;
    }

    /**
     * Method that represents filtered list.
     * @param model - parameter map which are passed to html
     * @param param - parameter which need to find in whole of the list
     * @return paige template which need to be shown
     */
    @GetMapping("filter")
    public String filterUrlValues(Map<String, Object> model, @RequestParam String param) {
        model.put(URL_LIST_PROPERTY, service.filterUrlList(param, 0));
        return "main";
    }

    /**
     * Method that redirect from short URL to original URL
     * @param response - {@inheritDoc Servlet} response object
     * @param key - generated URL key
     */
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
