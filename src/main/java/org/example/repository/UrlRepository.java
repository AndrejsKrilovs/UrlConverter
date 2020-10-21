package org.example.repository;

import org.example.entity.URL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface which allows generate SQL queries into database
 */
@Repository
public interface UrlRepository extends JpaRepository<URL, Long> {
    /**
     * Method that filter URLS
     * @param filter - filter criteria
     * @param pageable - additional criteria to optimize sql query performance
     * @return ResultSet which are converted in List
     */
    @Query("from URL u where lower(concat(u.createdTime, u.originalUrl, u.urlValue)) like lower(concat('%', :filter, '%'))")
    List<URL> filterUrls(@Param("filter") String filter, Pageable pageable);

    /**
     * Method that gets original URL
     * @param shortUrl - short URL that pass from outside
     * @return original URL from database table
     */
    URL findByShortUrlContainingIgnoreCase(String shortUrl);
}
