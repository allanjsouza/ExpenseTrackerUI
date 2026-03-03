package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
        datePicker.setValue(LocalDate.now());
    }
}
