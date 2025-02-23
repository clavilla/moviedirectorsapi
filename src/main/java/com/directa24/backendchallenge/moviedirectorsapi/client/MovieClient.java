package com.directa24.backendchallenge.moviedirectorsapi.client;

import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class MovieClient {

    private final RestTemplate restTemplate;

    @Autowired
    public MovieClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MovieResponse getMovies(int page) {
        String baseUrl = "https://challenge.iugolabs.com/api/movies/search?page=";
        String apiUrl = baseUrl + "{page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", page);
        return restTemplate.getForObject(apiUrl, MovieResponse.class, params);
    }
}
