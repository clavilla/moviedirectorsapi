package com.directa24.backendchallenge.moviedirectorsapi.client;

import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MovieClient {

    private final RestTemplate restTemplate;

    public MovieClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "movieClient", fallbackMethod = "fallbackGetMovies")
    public MovieResponse getMovies(int page) {
        String url = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", page);
        return restTemplate.getForObject(url, MovieResponse.class, params);
    }

    public MovieResponse fallbackGetMovies(int page, Throwable t) {
        log.error("Error calling external API: {}", t.getMessage());

        // Create a default response
        return MovieResponse.builder()
                .page(page)
                .data(Collections.emptyList())
                .build();
    }
}
