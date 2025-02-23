package com.directa24.backendchallenge.moviedirectorsapi.controller;



import com.directa24.backendchallenge.moviedirectorsapi.exception.InvalidThresholdException;
import com.directa24.backendchallenge.moviedirectorsapi.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private DirectorController directorController;

    private Map<String, List<String>> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = Map.of(
                "Christopher Nolan", List.of("Inception", "Interstellar", "Dunkirk"),
                "Steven Spielberg", List.of("Jaws", "E.T.", "Jurassic Park")
        );
    }

    @Test
    void getDirectors_ReturnsExpectedDirectors() {
        int threshold = 3;
        when(movieService.getDirectors(threshold)).thenReturn(mockResponse);

        Map<String, List<String>> result = directorController.getDirectors(threshold);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Christopher Nolan"));
        assertTrue(result.containsKey("Steven Spielberg"));
        assertEquals(3, result.get("Christopher Nolan").size());
        verify(movieService, times(1)).getDirectors(threshold);
    }

    @Test
    void getDirectors_ReturnsEmptyMap_WhenNoDirectorsMeetThreshold() {
        int threshold = 100;
        when(movieService.getDirectors(threshold)).thenReturn(Collections.emptyMap());

        Map<String, List<String>> result = directorController.getDirectors(threshold);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieService, times(1)).getDirectors(threshold);
    }

    @Test
    void getDirectors_ThrowsException_WhenServiceFails() {
        int threshold = 5;
        when(movieService.getDirectors(threshold)).thenThrow(new RuntimeException("Error fetching directors"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            directorController.getDirectors(threshold);
        });

        assertEquals("Error fetching directors", exception.getMessage());
        verify(movieService, times(1)).getDirectors(threshold);
    }

    @Test
    void getDirectors_ThrowsInvalidThresholdException_WhenThresholdIsNegative() {
        int threshold = -1;

        InvalidThresholdException exception = assertThrows(InvalidThresholdException.class, () -> {
            directorController.getDirectors(threshold);
        });

        assertEquals("Threshold must be a non-negative number", exception.getMessage());
        verify(movieService, never()).getDirectors(threshold);
    }

}