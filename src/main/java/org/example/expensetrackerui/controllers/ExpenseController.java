package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.expensetrackerui.exceptions.AuthException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.time.LocalDate;

public class ExpenseController {
    public MFXComboBox expenseTypeDropdown;
    public DatePicker datePicker;
    public MFXTextField amountField;
    public MFXComboBox<String> categoryDropdown;
    public MFXComboBox<String> accountDropdown;
    public MFXTextField noteField;
    public MFXButton submitButton;

    private Long expenseId = null;
    private boolean isEditMode = false;

    @Setter
    private MainController mainController;

    public void initialize() {
        expenseTypeDropdown.getItems().addAll("Expense", "Income");
        categoryDropdown.getItems().addAll("Food", "Transport", "Travel", "Household", "Health", "Social",
                "Gift", "Apparel", "Education", "Beauty", "Other");
        accountDropdown.getItems().addAll("Bank", "Cash", "Card");

        if (!isEditMode) datePicker.setValue(LocalDate.now());
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setOpacity(1);
    }

    public void initEditMode(Long expenseId, String expenseType, LocalDate date, double amount, String category,
                                   String account, String note) {
        this.isEditMode = true;
        this.expenseId = expenseId;
        expenseTypeDropdown.setValue(expenseType.equals("0") ? "Expense" : "Income");
        datePicker.setValue(date);
        amountField.setText(String.valueOf(amount));
        categoryDropdown.setValue(category);
        accountDropdown.setValue(account);
        noteField.setText(note);
    }

    @FXML
    private void handleSubmit() {
        if (!validateForm()) return;

        int expenseType = expenseTypeDropdown.getValue().equals("Expense") ? 0 : 1;
        LocalDate date = datePicker.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String category = (String) categoryDropdown.getValue();
        String account = accountDropdown.getValue();
        String note = noteField.getText();

        String token = JwtStorageUtil.getToken();
        String expenseJson = ExpenseDataParser.serializeExpense(
                new Expense(0L, expenseType, date, amount, category, account, note)
        );
        try {
            HttpClientUtil.post("/expenses", token, expenseJson);
            if (mainController != null) mainController.refreshExpenses();
        } catch (AuthException e) {
            handleAuthenticationFailure();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        }
    }

    private boolean validateForm() {
        if (expenseTypeDropdown.getValue() == null) {
            showErrorMessage("Please select an expense type.");
            return false;
        }

        LocalDate date;
        try {
            date = datePicker.getValue();
            LocalDate today = LocalDate.now();
            if (date == null || date.isAfter(today) || date.isBefore(today.minusYears(1))) {
                showErrorMessage("Please select a valid date.");
                return false;
            }
        } catch (Exception e) {
            showErrorMessage("Invalid date selected.");
            return false;
        }

        String amountText = amountField.getText();
        if (amountText == null || amountText.isBlank()) {
            showErrorMessage("Amount can not be empty.");
            return false;
        }
        try {
            Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorMessage("Amount must be a numeric value.");
            return false;
        }

        if (categoryDropdown.getValue() == null) {
            showErrorMessage("Please select a category.");
            return false;
        }

        if (accountDropdown.getValue() == null) {
            showErrorMessage("Please select an account.");
            return false;
        }

        if (noteField.getText() == null || noteField.getText().isBlank()) {
            showErrorMessage("Please enter a note.");
            return false;
        }

        return true;
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation error!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleAuthenticationFailure() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again.");

        alert.showAndWait();
    }
}
