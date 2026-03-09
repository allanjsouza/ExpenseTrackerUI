package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.expensetrackerui.exceptions.AuthException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.services.AuthService;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    public DatePicker datePicker;
    public MFXButton addExpenseButton;
    public MFXButton monthlyStatsButton;
    public MFXButton logoutButton;
    public TableView<Expense> expensesTable;
    public TableColumn<Expense, String> categoryColumn;
    public TableColumn<Expense, String> descriptionColumn;
    public TableColumn<Expense, String> amountColumn;
    public TableColumn<Expense, String> dateColumn;
    public TableColumn<Expense, Void> editColumn;
    public TableColumn<Expense, Void> deleteColumn;

    @FXML
    public void initialize() {
        LocalDate currentDate = LocalDate.now();
        datePicker.setValue(currentDate);

        expensesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        addEditButtonToTable();
        addDeleteButtonToTable();

        expensesTable.getColumns().add(categoryColumn);
        expensesTable.getColumns().add(descriptionColumn);
        expensesTable.getColumns().add(amountColumn);
        expensesTable.getColumns().add(dateColumn);
        expensesTable.getColumns().add(editColumn);
        expensesTable.getColumns().add(deleteColumn);

        expensesTable.getItems().clear();

        datePicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null)
                fetchExpensesByDate(newValue.toString());
        }));

        Platform.runLater(this::refreshExpenses);
    }

    private void addEditButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory = param -> new TableCell<>() {
            private final MFXButton btn = new MFXButton("Edit");
            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    openEditExpenseScreen(expense);
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else
                    setGraphic(btn);
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void addDeleteButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory = param -> new TableCell<>() {
            private final MFXButton btn = new MFXButton("Delete");
            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    askForDeleteConfirmation(expense);
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else
                    setGraphic(btn);
            }
        };
        deleteColumn.setCellFactory(cellFactory);
    }

    private void fetchExpensesByDate(String date) {
        String token = JwtStorageUtil.getToken();

        if (token == null || token.isEmpty()) {
            System.out.println("There is no token");
            return;
        }

        try {
            HttpResponse<String> response = HttpClientUtil.get("/expenses?date=" + date, token);

            if (response.statusCode() == 200) {
                List<Expense> expenses = ExpenseDataParser.parseExpenseList(response.body());
                expensesTable.getItems().clear();
                expensesTable.getItems().addAll(expenses);
            } else {
                System.out.println("Error: " + response);
            }
        } catch (AuthException e) {
            handleAuthenticationFailure();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void handleAddExpense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            ExpenseController controller = loader.getController();
            controller.setMainController(this);
            if (datePicker.getValue() != null) controller.datePicker.setValue(datePicker.getValue());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            scene.getStylesheets().add(getClass().getResource("/org/example/expensetrackerui/css/expense_screen.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Add Expense");

            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAuthenticationFailure() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again.");
        alert.setOnHiding(event -> {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            redirectToLogin(stage);
        });

        alert.showAndWait();
    }

    private void redirectToLogin(Stage stage) {
        JwtStorageUtil.clearToken();
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/LoginScreen.fxml"));

            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshExpenses() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) fetchExpensesByDate(selectedDate.toString());
    }

    private void openEditExpenseScreen(Expense expense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            ExpenseController controller = loader.getController();
            controller.setMainController(this);
            controller.initEditMode(expense);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);
            scene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/expense_screen.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Edit expense");
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void askForDeleteConfirmation(Expense expense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete expense confirmation");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("Expense: " + expense.getNote() + " $" + String.valueOf(expense.getAmount()));
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) deleteExpense(expense);
        });
    }

    private void deleteExpense(Expense expense) {
        String token = JwtStorageUtil.getToken();

        try {
            HttpClientUtil.delete("/expenses/" + expense.getId(), token);
            refreshExpenses();
        } catch (AuthException e) {
            handleAuthenticationFailure();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMonthlyStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/StatisticsScreen.fxml"));
            VBox statisticsPane = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(statisticsPane);
            scene.getStylesheets().add(
                    AuthService.class.getResource("/org/example/expensetrackerui/css/statistics_screen.css").toExternalForm());

            stage.setTitle("Monthly statistics");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
