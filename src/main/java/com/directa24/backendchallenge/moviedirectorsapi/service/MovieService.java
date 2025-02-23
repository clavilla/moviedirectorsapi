package com.directa24.backendchallenge.moviedirectorsapi.service;

import com.directa24.backendchallenge.moviedirectorsapi.client.MovieClient;
import com.directa24.backendchallenge.moviedirectorsapi.model.Movie;
import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieClient movieClient;

    @Autowired
    public MovieService(MovieClient movieClient) {
        this.movieClient = movieClient;
    }

    public Map<String, List<String>> getDirectors(int threshold) {
        Map<String, Integer> directorCount = new HashMap<>();
        int page = 1;
        MovieResponse response;

        do {
            response = movieClient.getMovies(page);
            for (Movie movie : response.getData()) {
                directorCount.put(movie.getDirector(), directorCount.getOrDefault(movie.getDirector(), 0) + 1);
            }
            page++;
        } while (page <= response.getTotalPages());

        List<String> directors = directorCount.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        Map<String, List<String>> result = new HashMap<>();
        result.put("directors", directors);
        return result;
    }
}
