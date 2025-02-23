package com.directa24.backendchallenge.moviedirectorsapi.client;

import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MovieClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MovieClient movieClient;

    @Mock
    private MovieResponse movieResponse;

    @BeforeEach
    void setUp() {
        movieClient = new MovieClient(restTemplate);
    }

    @Test
    void getMovies_CorrectURL() {
        // Arrange
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(movieResponse);

        // Act
        MovieResponse result = movieClient.getMovies(1);

        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_InvalidPage() {
        // Arrange
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", -1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> movieClient.getMovies(-1));
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_ResourceAccessException() {
        // Arrange
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenThrow(new ResourceAccessException("I/O error"));

        // Act & Assert
        assertThrows(ResourceAccessException.class, () -> movieClient.getMovies(1));
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_NullResponse() {
        // Arrange
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(null);

        // Act
        MovieResponse result = movieClient.getMovies(1);

        // Assert
        assertNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }
}
