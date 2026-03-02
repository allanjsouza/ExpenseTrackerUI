package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.SignUpRequest;
import org.example.expensetrackerui.services.AuthService;

import java.io.IOException;

public class SignUpController {
    public MFXTextField fullNameField;
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXButton signUpButton;

    public void handleSignUp(ActionEvent actionEvent) {
        SignUpRequest request = new SignUpRequest();
        request.setFullName(fullNameField.getText());
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) signUpButton.getScene().getWindow();
        AuthService.signUp(request, stage);
    }

    public void handleLogin(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/LoginScreen.fxml"));

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets()
                    .add(getClass().getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
