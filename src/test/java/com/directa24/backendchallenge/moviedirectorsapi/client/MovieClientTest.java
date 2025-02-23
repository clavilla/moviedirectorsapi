package com.directa24.backendchallenge.moviedirectorsapi.client;

import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = CircuitBreaker.ofDefaults("movieClient");
        lenient().when(circuitBreakerRegistry.circuitBreaker("movieClient")).thenReturn(circuitBreaker);
        movieClient = new MovieClient(restTemplate);
    }

    @Test
    void getMovies_CorrectURL() {
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(movieResponse);

        MovieResponse result = movieClient.getMovies(1);

        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_InvalidPage() {
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", -1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> movieClient.getMovies(-1));
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_ResourceAccessException() {
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenThrow(new ResourceAccessException("I/O error"));

        assertThrows(ResourceAccessException.class, () -> movieClient.getMovies(1));
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_NullResponse() {
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(null);

        MovieResponse result = movieClient.getMovies(1);

        assertNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_CircuitBreakerClosed() {
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(movieResponse);

        MovieResponse result = movieClient.getMovies(1);

        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_CircuitBreakerOpen() {
        circuitBreaker.transitionToOpenState();
        int page = 1;

        MovieResponse result = movieClient.fallbackGetMovies(page, new ResourceAccessException("I/O error"));

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(restTemplate, never()).getForObject(anyString(), eq(MovieResponse.class), anyMap());
    }

    @Test
    void getMovies_CircuitBreakerHalfOpen() {
        circuitBreaker.transitionToOpenState();
        circuitBreaker.transitionToHalfOpenState();
        String expectedUrl = "https://challenge.iugolabs.com/api/movies/search?page={page}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 1);
        when(restTemplate.getForObject(expectedUrl, MovieResponse.class, params))
                .thenReturn(movieResponse);

        MovieResponse result = movieClient.getMovies(1);

        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, MovieResponse.class, params);
    }

    @Test
    void getMovies_CircuitBreakerHalfOpen_Failure() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("movieClient");

        circuitBreaker.transitionToOpenState();
        circuitBreaker.transitionToHalfOpenState();

        MovieResponse result = movieClient.fallbackGetMovies(1, new ResourceAccessException("I/O error"));

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

}
