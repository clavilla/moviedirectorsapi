package com.directa24.backendchallenge.moviedirectorsapi.service;

import com.directa24.backendchallenge.moviedirectorsapi.client.MovieClient;
import com.directa24.backendchallenge.moviedirectorsapi.model.Movie;
import com.directa24.backendchallenge.moviedirectorsapi.model.MovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieClient movieClient;

    @InjectMocks
    private MovieService movieService;

    private MovieResponse singlePageResponse;
    private MovieResponse multiPageResponsePage1;
    private MovieResponse multiPageResponsePage2;

    @BeforeEach
    void setUp() {
        singlePageResponse = MovieResponse.builder()
                .page(1)
                .totalPages(1)
                .data(
                List.of(
                        Movie.builder().title("Movie1").director("Director1").build(),
                        Movie.builder().title("Movie2").director("Director2").build(),
                        Movie.builder().title("Movie3").director("Director1").build(),
                        Movie.builder().title("Movie4").director("Director2").build(),
                        Movie.builder().title("Movie5").director("Director1").build())
                )
                .build();

        multiPageResponsePage1 = MovieResponse.builder()
                .page(1)
                .totalPages(2)
                .data(
                        List.of(
                                Movie.builder().title("Movie1").director("Director1").build(),
                                Movie.builder().title("Movie2").director("Director2").build())
                )
                .build();

        multiPageResponsePage2 = MovieResponse.builder()
                .page(2)
                .totalPages(2)
                .data(
                        List.of(
                                Movie.builder().title("Movie3").director("Director1").build(),
                                Movie.builder().title("Movie4").director("Director2").build(),
                                Movie.builder().title("Movie5").director("Director1").build())
                )
                .build();
    }

    @Test
    void getDirectors_ReturnsExpectedDirectors_WhenThresholdMet() {
        int threshold = 1;
        when(movieClient.getMovies(1)).thenReturn(singlePageResponse);

        Map<String, List<String>> result = movieService.getDirectors(threshold);

        assertNotNull(result);
        assertTrue(result.containsKey("directors"));
        List<String> directors = result.get("directors");

        assertEquals(2, directors.size());
        assertTrue(directors.contains("Director1"));
        assertTrue(directors.contains("Director2"));

        verify(movieClient, times(1)).getMovies(1);
    }

    @Test
    void getDirectors_ReturnsEmptyList_WhenThresholdIsTooHigh() {
        int threshold = 10;
        when(movieClient.getMovies(1)).thenReturn(singlePageResponse);

        Map<String, List<String>> result = movieService.getDirectors(threshold);

        assertNotNull(result);
        assertTrue(result.containsKey("directors"));
        assertTrue(result.get("directors").isEmpty());

        verify(movieClient, times(1)).getMovies(1);
    }

    @Test
    void getDirectors_HandlesMultiplePagesCorrectly() {
        // Arrange
        int threshold = 1;
        when(movieClient.getMovies(1)).thenReturn(multiPageResponsePage1);
        when(movieClient.getMovies(2)).thenReturn(multiPageResponsePage2);

        // Act
        Map<String, List<String>> result = movieService.getDirectors(threshold);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("directors"));
        List<String> directors = result.get("directors");

        assertEquals(2, directors.size());
        assertTrue(directors.contains("Director1"));
        assertTrue(directors.contains("Director2"));

        verify(movieClient, times(1)).getMovies(1);
        verify(movieClient, times(1)).getMovies(2);
    }

    @Test
    void getDirectors_ReturnsEmptyList_WhenApiReturnsNoData() {
        when(movieClient.getMovies(1)).thenReturn(
                MovieResponse.builder()
                        .page(1)
                        .totalPages(1)
                        .data(List.of())
                        .build()
        );

        Map<String, List<String>> result = movieService.getDirectors(1);

        assertNotNull(result);
        assertTrue(result.containsKey("directors"));
        assertTrue(result.get("directors").isEmpty());

        verify(movieClient, times(1)).getMovies(1);
    }

    @Test
    void getDirectors_ThrowsException_WhenApiFails() {
        when(movieClient.getMovies(1)).thenThrow(new RuntimeException("API error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movieService.getDirectors(1);
        });

        assertEquals("API error", exception.getMessage());
        verify(movieClient, times(1)).getMovies(1);
    }
}
