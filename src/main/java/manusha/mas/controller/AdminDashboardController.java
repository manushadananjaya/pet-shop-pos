package manusha.mas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.cell.PropertyValueFactory;
import manusha.mas.model.Employee;
import manusha.mas.util.DatabaseConnection;

import java.sql.*;

public class AdminDashboardController {

    @FXML
    private AnchorPane adminDashboardPane;

    @FXML
    private TableView<Employee> adminTable;

    @FXML
    private TableColumn<Employee, String> colName;

    @FXML
    private TableColumn<Employee, String> colEpfNumber;

    @FXML
    private TableColumn<Employee, String> colNationalId;

    @FXML
    private TableColumn<Employee, String> colSection;

    @FXML
    private TableColumn<Employee, String> colLineNo;

    @FXML
    private TableColumn<Employee, String> colDesignation;

    @FXML
    private TableColumn<Employee, String> colAddress;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField designationField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private TextField educationLevelField;

    @FXML
    private TextField epfNumberField;

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextField lineNumberField;

    @FXML
    private ComboBox<String> maritalStatusComboBox;

    @FXML
    private TextField nationalIdField;

    @FXML
    private TextField pastExperienceField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField addressField;

    @FXML
    private TextField sectionField;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up table columns
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEpfNumber.setCellValueFactory(new PropertyValueFactory<>("epfNumber"));
        colNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colSection.setCellValueFactory(new PropertyValueFactory<>("section"));
        colLineNo.setCellValueFactory(new PropertyValueFactory<>("lineNumber"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Initialize gender and marital status combo boxes
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        maritalStatusComboBox.setItems(FXCollections.observableArrayList("Single", "Married", "Divorced", "Widowed"));

        // Load existing data from the database
        loadEmployeesFromDatabase();

        // Set the table data
        adminTable.setItems(employeeList);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Validate fields
        if (fullNameField.getText().isEmpty() || epfNumberField.getText().isEmpty() ||
                nationalIdField.getText().isEmpty() || sectionField.getText().isEmpty() ||
                lineNumberField.getText().isEmpty() || designationField.getText().isEmpty() ||
                addressField.getText().isEmpty() || dobPicker.getValue() == null ||
                genderComboBox.getValue() == null || maritalStatusComboBox.getValue() == null ||
                educationLevelField.getText().isEmpty() || pastExperienceField.getText().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        // Create Employee object
        Employee newEmployee = new Employee(
                fullNameField.getText(),
                epfNumberField.getText(),
                nationalIdField.getText(),
                sectionField.getText(),
                lineNumberField.getText(),
                designationField.getText(),
                addressField.getText(),
                dobPicker.getValue(),
                genderComboBox.getValue(),
                maritalStatusComboBox.getValue(),
                educationLevelField.getText(),
                pastExperienceField.getText()
        );

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO employees (full_name, epf_number, national_id, section, line_number, designation, address, dob, gender, marital_status, education_level, past_experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newEmployee.getFullName());
            stmt.setString(2, newEmployee.getEpfNumber());
            stmt.setString(3, newEmployee.getNationalId());
            stmt.setString(4, newEmployee.getSection());
            stmt.setString(5, newEmployee.getLineNumber());
            stmt.setString(6, newEmployee.getDesignation());
            stmt.setString(7, newEmployee.getAddress());
            stmt.setDate(8, Date.valueOf(newEmployee.getDob()));
            stmt.setString(9, newEmployee.getGender());
            stmt.setString(10, newEmployee.getMaritalStatus());
            stmt.setString(11, newEmployee.getEducationLevel());
            stmt.setString(12, newEmployee.getPastExperience());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                employeeList.add(newEmployee);
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee saved successfully.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Employee selectedEmployee = adminTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM employees WHERE epf_number = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, selectedEmployee.getEpfNumber());

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    employeeList.remove(selectedEmployee);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete employee: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to delete.");
        }
    }

    private void loadEmployeesFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM employees";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getString("full_name"),
                        rs.getString("epf_number"),
                        rs.getString("national_id"),
                        rs.getString("section"),
                        rs.getString("line_number"),
                        rs.getString("designation"),
                        rs.getString("address"),
                        rs.getDate("dob").toLocalDate(),
                        rs.getString("gender"),
                        rs.getString("marital_status"),
                        rs.getString("education_level"),
                        rs.getString("past_experience")
                );
                employeeList.add(employee);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load employees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        fullNameField.clear();
        epfNumberField.clear();
        nationalIdField.clear();
        sectionField.clear();
        lineNumberField.clear();
        designationField.clear();
        addressField.clear();
        dobPicker.setValue(null);
        genderComboBox.setValue(null);
        maritalStatusComboBox.setValue(null);
        educationLevelField.clear();
        pastExperienceField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
