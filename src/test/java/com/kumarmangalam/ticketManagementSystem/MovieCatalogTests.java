package com.kumarmangalam.ticketManagementSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import com.kumarmangalam.ticketManagementSystem.service.MovieService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieCatalogTests {
    @LocalServerPort
    int port;

    @Autowired
    MovieService movies;

    private HttpResponse<String> get(String path) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .GET().build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    @Test
    void catalogServesPageAssetsAndLiveRepositoryMovies() throws Exception {
        var page = get("/");
        assertEquals(200, page.statusCode());
        assertTrue(page.body().contains("id=\"movies\""));
        assertEquals(200, get("/movies.js").statusCode());
        assertEquals(200, get("/movies.css").statusCode());
        movies.addMovie("Catalog integration test movie");
        var response = get("/api/movies");
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").contains("application/json"));
        assertTrue(response.body().contains("Hanuman Ansh"));
        assertTrue(response.body().contains("Awarapan 2"));
        assertTrue(response.body().contains("Catalog integration test movie"));
        assertTrue(response.body().contains("movieId"));
    }
}
