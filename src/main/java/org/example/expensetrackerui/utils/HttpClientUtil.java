package org.example.expensetrackerui.utils;

import com.google.gson.Gson;
import org.example.expensetrackerui.exceptions.AuthException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Gson gson = new Gson();

    private static final String BASE_URL = "http://localhost:8080";

    public static HttpResponse<String> post(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> post(String endpoint, String token, String jsonBody) throws IOException, InterruptedException, AuthException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403)
            throw new AuthException("Session has expired. Please log in again.");

        return response;
    }

    public static HttpResponse<String> put(String endpoint, String token, String jsonBody) throws IOException, InterruptedException, AuthException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403)
            throw new AuthException("Session has expired. Please log in again.");

        return response;
    }

    public static HttpResponse<String> get(String url, String token) throws IOException, InterruptedException, AuthException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403)
            throw new AuthException("Session has expired. Please log in again.");

        return response;
    }

    public static HttpResponse<String> delete(String endpoint, String token) throws IOException, InterruptedException, AuthException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403)
            throw new AuthException("Session has expired. Please log in again.");

        return response;
    }
}
