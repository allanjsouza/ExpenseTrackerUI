package org.example.expensetrackerui.services;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.post("/signup", jsonBody);
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                if (authResponse.getAccessToken() != null) {
                    JwtStorageUtil.storeToken(authResponse.getAccessToken());
                    Platform.runLater(() -> navigateToMainScreen(stage));
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
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.post("/login", jsonBody);
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                if (authResponse.getAccessToken() != null) {
                    JwtStorageUtil.storeToken(authResponse.getAccessToken());
                    Platform.runLater(() -> navigateToMainScreen(stage));
                } else {
                    System.out.println(authResponse.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void navigateToMainScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(AuthService.class
                    .getResource("/org/example/expensetrackerui/views/MainScreen.fxml"));

            Scene mainScene = new Scene(loader.load());
            mainScene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/main_screen.css").toExternalForm());
            stage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
