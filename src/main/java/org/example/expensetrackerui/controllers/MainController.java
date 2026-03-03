package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.expensetrackerui.services.AuthService;
import org.example.expensetrackerui.utils.JwtStorageUtil;
import java.io.IOException;
import java.time.LocalDate;

public class MainController {
    public DatePicker datePicker;
    public MFXButton addExpenseButton;
    public MFXButton monthlyStatsButton;
    public MFXButton logoutButton;
    public TableView expensesTable;
    public TableColumn categoryColumn;
    public TableColumn descriptionColumn;
    public TableColumn amountColumn;
    public TableColumn dateColumn;
    public TableColumn editColumn;
    public TableColumn deleteColumn;

    @FXML
    public void initialize() {

    @FXML
    private void handleLogout() {
        JwtStorageUtil.clearToken();
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/LoginScreen.fxml"));

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
