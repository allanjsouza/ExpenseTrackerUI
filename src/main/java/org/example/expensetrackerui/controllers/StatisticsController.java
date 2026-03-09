package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.expensetrackerui.exceptions.AuthException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {
    public PieChart expensePieChart;
    public MFXComboBox<String> monthPicker;
    public MFXComboBox<Integer> yearPicker;
    public MFXButton backButton;

    private final List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
    private List<String> categories;

    @FXML
    public void initialize() {
        monthPicker.getItems().addAll(months);

        Platform.runLater(() -> {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();

            monthPicker.setValue(monthPicker.getItems().get(currentMonth - 1));
            for (int year = 2020; year <= currentYear; year++) yearPicker.getItems().add(year);
            yearPicker.setValue(currentYear);

            fetchCategories();
        });

        monthPicker.valueProperty().addListener((observable, oldValue, newValue) -> loadPieChartData());
        yearPicker.valueProperty().addListener((observable, oldValue, newValue) -> loadPieChartData());
    }

    private void loadPieChartData() {
        if (categories == null || categories.isEmpty()) return;

        int selectedYear = yearPicker.getValue();
        int selectedMonth = months.indexOf(monthPicker.getValue()) + 1;
        try {
            Map<String, Double> categoryExpenses = new HashMap<>();
            for (String category : categories) {
                List<Expense> expenses = fetchExpensesByCategoryAndMonth(category, selectedMonth, selectedYear);
                List<Double> expenseValues = expenses.stream().map(e -> e.getExpenseType() == 0 ? e.getAmount() : 0).toList();
                double totalExpense = expenseValues.stream().mapToDouble(Double::doubleValue).sum();
                categoryExpenses.put(category, totalExpense);
            }
            for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
                if (entry.getValue() > 0) expensePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        } catch (AuthException e) {
            handleAuthFailure();
        } catch (Exception e) {
            showLoadExpensesError();
        }
    }

    @FXML
    public void handleBack() {
        closeStage();
    }

    private void fetchCategories() {
        String token = JwtStorageUtil.getToken();
        try {
            HttpResponse<String> response = HttpClientUtil.get("/expense_categories", token);
            categories = ExpenseDataParser.parseCategoryList(response.body());
        } catch (AuthException e) {
            handleAuthFailure();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Expense> fetchExpensesByCategoryAndMonth(String category, int month, int year) throws AuthException, IOException, InterruptedException {
        String token = JwtStorageUtil.getToken();
        String endpoint = "/expenses?category=" + category + "&month=" + year + "-" + String.format("%02d", month);
        HttpResponse<String> response = HttpClientUtil.get(endpoint, token);

        return ExpenseDataParser.parseExpenseList(response.body());
    }

    private void handleAuthFailure() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again.");
        alert.showAndWait();

        closeStage();
    }

    private void showLoadExpensesError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Problem loading expenses");
        alert.setHeaderText("Could not load expenses");
        alert.setContentText("There was a problem while loading expenses. Please try again");
        alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
