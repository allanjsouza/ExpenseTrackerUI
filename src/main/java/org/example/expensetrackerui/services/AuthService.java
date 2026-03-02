package org.example.expensetrackerui.services;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.AuthRequest;
import org.example.expensetrackerui.models.AuthResponse;
import org.example.expensetrackerui.models.SignUpRequest;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.net.http.HttpResponse;

public class AuthService {
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    public static void signUp(SignUpRequest request, Stage stage) {
        new Thread(() -> {
            try {
                String url = BASE_URL + "/signup";
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.post(url, jsonBody);
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                if (authResponse.getAccessToken() != null) {
                    JwtStorageUtil.storeToken(authResponse.getAccessToken());
                    Platform.runLater(() -> navigateToLoadingScreen(stage));
                } else {
                    System.out.println(authResponse.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void login(AuthRequest request, Stage stage) {
        new Thread(() -> {
            try {
                String url = BASE_URL + "/login";
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.post(url, jsonBody);
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                if (authResponse.getAccessToken() != null) {
                    JwtStorageUtil.storeToken(authResponse.getAccessToken());
                    Platform.runLater(() -> navigateToLoadingScreen(stage));
                } else {
                    System.out.println(authResponse.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void navigateToLoadingScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(AuthService.class
                    .getResource("/org/example/expensetrackerui/views/LoadingScreen.fxml"));

            Scene loadingScene = new Scene(loader.load());
            loadingScene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loadingScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
