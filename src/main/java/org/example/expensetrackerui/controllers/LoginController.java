package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.AuthRequest;
import org.example.expensetrackerui.services.AuthService;

import java.io.IOException;

public class LoginController {
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXButton submitButton;

    public void handleLogin(ActionEvent actionEvent) {
        AuthRequest request = new AuthRequest();
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) submitButton.getScene().getWindow();
        AuthService.login(request, stage);
    }

    public void handleCreateAccount(MouseEvent mouseEvent) {
    }
}
