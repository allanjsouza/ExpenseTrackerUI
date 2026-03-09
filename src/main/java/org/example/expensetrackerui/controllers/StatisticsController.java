package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.time.LocalDate;

public class StatisticsController {
    public PieChart expensePieChart;
    public MFXComboBox monthPicker;
    public MFXComboBox yearPicker;
    public MFXButton backButton;

    @FXML
    public void initialize() {
        monthPicker.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        Platform.runLater(() -> {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();

            monthPicker.setValue(monthPicker.getItems().get(currentMonth - 1));
            for (int year = 2020; year <= currentYear; year++) yearPicker.getItems().add(year);
            yearPicker.setValue(currentYear);

            loadPieChartData();
        });

        monthPicker.valueProperty().addListener((observable, oldValue, newValue) -> loadPieChartData());
        yearPicker.valueProperty().addListener((observable, oldValue, newValue) -> loadPieChartData());
    }

    private void loadPieChartData() {

    }

    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
