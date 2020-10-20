package org.example.repository;

import org.example.entity.URL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<URL, Long> {
    @Query("from URL u where lower(concat(u.createdTime, u.originalUrl, u.urlValue)) like lower(concat('%', :filter, '%'))")
    List<URL> filterUrls(@Param("filter") String filter, Pageable pageable);
    URL findByShortUrlContainingIgnoreCase(String shortUrl);
}
