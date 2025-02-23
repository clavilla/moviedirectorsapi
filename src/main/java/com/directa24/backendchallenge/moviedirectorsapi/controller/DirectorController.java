package com.directa24.backendchallenge.moviedirectorsapi.controller;

import com.directa24.backendchallenge.moviedirectorsapi.exception.InvalidThresholdException;
import com.directa24.backendchallenge.moviedirectorsapi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DirectorController {

    private MovieService movieService;

    @Autowired
    public DirectorController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/api/directors")
    public Map<String, List<String>> getDirectors(@RequestParam int threshold) {
        if (threshold < 0) {
            throw new InvalidThresholdException("Threshold must be a non-negative number");
        }
        return movieService.getDirectors(threshold);
    }
}
