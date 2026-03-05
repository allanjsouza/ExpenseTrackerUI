package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ExpenseController {
    public MFXComboBox expenseTypeDropdown;
    public DatePicker datePicker;
    public MFXTextField amountField;
    public MFXComboBox categoryDropdown;
    public MFXComboBox accountDropdown;
    public MFXTextField noteField;
    public MFXButton submitButton;

    private Long expenseId = null;
    private boolean isEditMode = false;

    public void initialize() {
        expenseTypeDropdown.getItems().addAll("Expense", "Income");
        categoryDropdown.getItems().addAll("Food", "Transport", "Travel", "Household", "Health", "Social",
                "Gift", "Apparel", "Education", "Beauty", "Other");
        accountDropdown.getItems().addAll("Bank", "Cash", "Card");

        if (!isEditMode) datePicker.setValue(LocalDate.now());
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

    private void handleSubmit() {
        String expenseType = expenseTypeDropdown.getValue().equals("Expense") ? "0" : "1";
        LocalDate date = datePicker.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String category = (String) categoryDropdown.getValue();
        String note = noteField.getText();

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
