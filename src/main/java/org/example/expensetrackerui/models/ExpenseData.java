package org.example.expensetrackerui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpenseData {
    private final ObservableList<Expense> expenses;

    public ExpenseData() {
        expenses = FXCollections.observableArrayList();
    }

    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }
}
