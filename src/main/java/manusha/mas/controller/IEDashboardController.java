package manusha.mas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import manusha.mas.util.DatabaseConnection;
import manusha.mas.model.RequestDetails;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class IEDashboardController {

    public Button editBtn;

    public TableView<RequestDetails> shiftTable;
    public TableColumn<RequestDetails, String> nameColumn;
    public TableColumn<RequestDetails, String> sectionColumn;
    public TableColumn<RequestDetails, String> moduleColumn;
    public TableColumn<RequestDetails, String> operationColumn;
    public TableColumn<RequestDetails, String> dateColumn;
    public TableColumn<RequestDetails, Integer> countColumn;
    public TextField searchField;
    public Button SearchBtn;
    public TextField nameField;
    public TextField designationField;
    public TextField genderField;
    public TextField trainingField;
    public ProgressBar operation1Bar;
    public Label operation1Label;
    public ProgressBar operation2Bar;
    public Label operation2Label;
    public ProgressBar operation3Bar;
    public Label operation3Label;
    public ProgressBar operation4Bar;
    public Label operation4Label;
    public ProgressBar operation5Bar;
    public Label operation5Label;
    public ProgressBar operation6Bar;
    public Label operation6Label;
    public BarChart performanceChart;
    public BarChart performanceDailyChart;
    public BarChart qualityChart;


    @FXML
    private AnchorPane mainContentPane, requestedDetailsPane, trainingPerformancePane;

    @FXML
    private Button requestingFormBtn, requestedDetailsBtn, trainingPerfoBtn;

    @FXML
    private TextField ieepfField, ieNameField, ieShiftField, ieSectionField;
    @FXML
    private TextField moduleNoField, customerField, requiredOperationField, requiredCountField;

    @FXML
    private DatePicker requiredDatePicker;

    @FXML
    private TableView<RequestDetails> ieTable;

    @FXML
    private TableColumn<RequestDetails, Integer> idColumn;
    @FXML
    private TableColumn<RequestDetails, String> ieepfColumn, ieNameColumn, ieShiftColumn, ieSectionColumn;
    @FXML
    private TableColumn<RequestDetails, String> moduleNoColumn, customerColumn, ReqOperationColumn, reqDateColumn;
    @FXML
    private TableColumn<RequestDetails, Integer> reqCountColumn;

    @FXML
    private Button saveBtn, deleteBtn;

    private ObservableList<RequestDetails> requestDetailsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initially show the main content pane and hide others
        showPane(mainContentPane);
        highlightButton(requestingFormBtn);

        // Initialize table columns
        initializeTableColumns();

        initializeShiftTableColumns();


        // Load current data into the table
        loadCurrentRequestedDetails();

        // Add button actions
        saveBtn.setOnAction(event -> saveOrUpdateRequestDetails());
        deleteBtn.setOnAction(event -> deleteSelectedDetails());
        editBtn.setOnAction(event -> loadSelectedDetailsForEdit());
    }

    @FXML
    private void handleRequestingFormBtn() {
        loadCurrentRequestedDetails();
        showPane(mainContentPane);
        highlightButton(requestingFormBtn);
    }

    @FXML
    private void handleRequestedDetailsBtn() {
        loadShiftDetails();
        showPane(requestedDetailsPane);
        highlightButton(requestedDetailsBtn);
    }

    @FXML
    private void handleTrainingPerfoBtn() {
        showPane(trainingPerformancePane);
        highlightButton(trainingPerfoBtn);
    }

    private void showPane(AnchorPane paneToShow) {
        mainContentPane.setVisible(false);
        requestedDetailsPane.setVisible(false);
        trainingPerformancePane.setVisible(false);
        paneToShow.setVisible(true);
    }

    private void highlightButton(Button activeButton) {
        requestingFormBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        requestedDetailsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        trainingPerfoBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        activeButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
    }

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        ieepfColumn.setCellValueFactory(data -> data.getValue().ieepfProperty());
        ieNameColumn.setCellValueFactory(data -> data.getValue().ieNameProperty());
        ieShiftColumn.setCellValueFactory(data -> data.getValue().ieShiftProperty());
        ieSectionColumn.setCellValueFactory(data -> data.getValue().ieSectionProperty());
        moduleNoColumn.setCellValueFactory(data -> data.getValue().moduleNoProperty());
        ReqOperationColumn.setCellValueFactory(data -> data.getValue().operationProperty());
        reqDateColumn.setCellValueFactory(data -> data.getValue().reqDateProperty().asString());
        reqCountColumn.setCellValueFactory(data -> data.getValue().countProperty().asObject());
    }

    private void initializeShiftTableColumns() {
        nameColumn.setCellValueFactory(data -> data.getValue().ieNameProperty());
        sectionColumn.setCellValueFactory(data -> data.getValue().ieSectionProperty());
        moduleColumn.setCellValueFactory(data -> data.getValue().moduleNoProperty());
        operationColumn.setCellValueFactory(data -> data.getValue().operationProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().reqDateProperty().asString());
        countColumn.setCellValueFactory(data -> data.getValue().countProperty().asObject());

    }

    private void loadShiftDetails() {
        requestDetailsList.clear();
        String query = "SELECT * FROM requested_details";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requestDetailsList.add(new RequestDetails(
                        rs.getInt("id"),
                        rs.getString("ieepf"),
                        rs.getString("ie_name"),
                        rs.getString("ie_shift"),
                        rs.getString("ie_section"),
                        rs.getString("module_no"),
                        rs.getString("customer"),
                        rs.getString("operation"),
                        rs.getDate("req_date").toLocalDate(),
                        rs.getInt("req_count")
                ));
            }
        } catch (SQLException e) {
            showErrorDialog("Error Loading Data", "Unable to load data from the database.\n" + e.getMessage());
        }
        shiftTable.setItems(requestDetailsList);
    }



    private void loadSelectedDetailsForEdit() {
        RequestDetails selected = ieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Selection Error", "No record selected for editing.");
            return;
        }

        // Populate fields with the selected record's data
        ieepfField.setText(selected.getIeepf());
        ieNameField.setText(selected.getIeName());
        ieShiftField.setText(selected.getIeShift());
        ieSectionField.setText(selected.getIeSection());
        moduleNoField.setText(selected.getModuleNo());
        customerField.setText(selected.getCustomer());
        requiredOperationField.setText(selected.getOperation());
        requiredDatePicker.setValue(selected.getReqDate());
        requiredCountField.setText(String.valueOf(selected.getCount()));

        // Highlight the row being edited
        ieTable.getSelectionModel().clearSelection();
        ieTable.getSelectionModel().select(selected);
    }

    private void loadCurrentRequestedDetails() {
        requestDetailsList.clear();
        String query = "SELECT * FROM requested_details WHERE req_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requestDetailsList.add(new RequestDetails(
                        rs.getInt("id"),
                        rs.getString("ieepf"),
                        rs.getString("ie_name"),
                        rs.getString("ie_shift"),
                        rs.getString("ie_section"),
                        rs.getString("module_no"),
                        rs.getString("customer"),
                        rs.getString("operation"),
                        rs.getDate("req_date").toLocalDate(),
                        rs.getInt("req_count")
                ));
            }
        } catch (SQLException e) {
            showErrorDialog("Error Loading Data", "Unable to load data from the database.\n" + e.getMessage());
        }
        ieTable.setItems(requestDetailsList);
    }

    private void saveOrUpdateRequestDetails() {
        String ieepf = ieepfField.getText();
        String ieName = ieNameField.getText();
        String ieShift = ieShiftField.getText();
        String ieSection = ieSectionField.getText();
        String moduleNo = moduleNoField.getText();
        String customer = customerField.getText();
        String operation = requiredOperationField.getText();
        LocalDate reqDate = requiredDatePicker.getValue();
        String reqCountStr = requiredCountField.getText();

        if (ieepf.isEmpty() || ieName.isEmpty() || reqDate == null || reqCountStr.isEmpty()) {
            showErrorDialog("Validation Error", "Please fill in all required fields.");
            return;
        }

        try {
            int reqCount = Integer.parseInt(reqCountStr);
            RequestDetails selected = ieTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                // Update existing record
                String updateQuery = "UPDATE requested_details SET ieepf = ?, ie_name = ?, ie_shift = ?, " +
                        "ie_section = ?, module_no = ?, customer = ?, operation = ?, req_date = ?, req_count = ? WHERE id = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setString(1, ieepf);
                    stmt.setString(2, ieName);
                    stmt.setString(3, ieShift);
                    stmt.setString(4, ieSection);
                    stmt.setString(5, moduleNo);
                    stmt.setString(6, customer);
                    stmt.setString(7, operation);
                    stmt.setDate(8, java.sql.Date.valueOf(reqDate));
                    stmt.setInt(9, reqCount);
                    stmt.setInt(10, selected.getId());
                    stmt.executeUpdate();
                }
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO requested_details (ieepf, ie_name, ie_shift, ie_section, module_no, " +
                        "customer, operation, req_date, req_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, ieepf);
                    stmt.setString(2, ieName);
                    stmt.setString(3, ieShift);
                    stmt.setString(4, ieSection);
                    stmt.setString(5, moduleNo);
                    stmt.setString(6, customer);
                    stmt.setString(7, operation);
                    stmt.setDate(8, java.sql.Date.valueOf(reqDate));
                    stmt.setInt(9, reqCount);
                    stmt.executeUpdate();
                }
            }

            // Reload the table data
            loadCurrentRequestedDetails();
            clearFields();

        } catch (NumberFormatException e) {
            showErrorDialog("Validation Error", "Required Count must be a number.");
        } catch (SQLException e) {
            showErrorDialog("Error Saving Data", "Unable to save data to the database.\n" + e.getMessage());
        }
    }

    private void clearFields() {
        ieepfField.clear();
        ieNameField.clear();
        ieShiftField.clear();
        ieSectionField.clear();
        moduleNoField.clear();
        customerField.clear();
        requiredOperationField.clear();
        requiredDatePicker.setValue(null);
        requiredCountField.clear();
    }

    private void deleteSelectedDetails() {
        RequestDetails selected = ieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Selection Error", "No record selected for deletion.");
            return;
        }

        String query = "DELETE FROM requested_details WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadCurrentRequestedDetails();
        } catch (SQLException e) {
            showErrorDialog("Error Deleting Data", "Unable to delete the selected record.\n" + e.getMessage());
        }
    }
    
    



    // Training performance form
    @FXML
    private void handleSearchPerformance() {
        String epfNumber = searchField.getText();
        if (epfNumber == null || epfNumber.isEmpty()) {
            showError("EPF Number cannot be empty");
            return;
        }

        try {
            // Fetch employee details
            String fullName = null, designation = null, gender = null;
            int trainingDays = 0;

            String employeeQuery = "SELECT full_name, designation, gender FROM employees WHERE epf_number = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(employeeQuery)) {

                statement.setString(1, epfNumber);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    fullName = resultSet.getString("full_name");
                    designation = resultSet.getString("designation");
                    gender = resultSet.getString("gender");
                } else {
                    showError("No employee found for EPF number: " + epfNumber);
                    return;
                }
            }

            // Fetch performance and operation data
            Map<String, List<Double>> operationValues = new HashMap<>();
            Map<Date, List<Double>> dailyPerformanceMap = new HashMap<>();
            Map<Date, List<Double>> dailyQualityMap = new HashMap<>();

            String performanceQuery = "SELECT operation1, operation2, operation3, operation4, " +
                    "operation5, operation6, performance, quality, date FROM employee_performance WHERE epf = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(performanceQuery)) {

                statement.setString(1, epfNumber);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    // Increment training days
                    trainingDays++;

                    // Collect operation values
                    double[] operations = {
                            resultSet.getDouble("operation1"),
                            resultSet.getDouble("operation2"),
                            resultSet.getDouble("operation3"),
                            resultSet.getDouble("operation4"),
                            resultSet.getDouble("operation5"),
                            resultSet.getDouble("operation6")
                    };

                    for (int i = 0; i < operations.length; i++) {
                        String operationKey = "Operation " + (i + 1);
                        operationValues.computeIfAbsent(operationKey, k -> new ArrayList<>()).add(operations[i]);
                    }

                    // Collect daily performance data
                    Date date = resultSet.getDate("date");
                    dailyPerformanceMap.computeIfAbsent(date, k -> new ArrayList<>())
                            .add(resultSet.getDouble("performance"));

                    // Collect daily quality data
                    dailyQualityMap.computeIfAbsent(date, k -> new ArrayList<>())
                            .add(resultSet.getDouble("quality"));
                }
            }

            // Hardcoded required values
            Map<String, Double> requiredValues = new HashMap<>();
            requiredValues.put("Operation 1", 80.0);
            requiredValues.put("Operation 2", 75.0);
            requiredValues.put("Operation 3", 90.0);
            requiredValues.put("Operation 4", 85.0);
            requiredValues.put("Operation 5", 70.0);
            requiredValues.put("Operation 6", 60.0);

            // Calculate averages for actual values
            Map<String, Double> actualAverages = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : operationValues.entrySet()) {
                double average = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                actualAverages.put(entry.getKey(), average);
            }

            // Create the ordered data for the chart
            List<String> operationsOrder = Arrays.asList(
                    "Operation 1", "Operation 2", "Operation 3", "Operation 4", "Operation 5", "Operation 6"
            );

            // Update performance chart with two series (Required and Actual)
            XYChart.Series<String, Number> requiredSeries = new XYChart.Series<>();
            requiredSeries.setName("Required Values");
            XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
            actualSeries.setName("Actual Values");

            // Ensure the operations are in order
            for (String operation : operationsOrder) {
                requiredSeries.getData().add(new XYChart.Data<>(operation, requiredValues.getOrDefault(operation, 0.0)));
                actualSeries.getData().add(new XYChart.Data<>(operation, actualAverages.getOrDefault(operation, 0.0)));
            }

            performanceChart.getData().clear();
            performanceChart.getData().addAll(requiredSeries, actualSeries);

            // Update daily performance chart
            XYChart.Series<String, Number> dailyPerformanceSeries = new XYChart.Series<>();
            dailyPerformanceSeries.setName("Daily Performance");
            for (Map.Entry<Date, List<Double>> entry : dailyPerformanceMap.entrySet()) {
                String day = entry.getKey().toString();
                double average = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                dailyPerformanceSeries.getData().add(new XYChart.Data<>(day, average));
            }
            performanceDailyChart.getData().clear();
            performanceDailyChart.getData().add(dailyPerformanceSeries);

            // Update daily quality chart
            XYChart.Series<String, Number> dailyQualitySeries = new XYChart.Series<>();
            dailyQualitySeries.setName("Daily Quality");
            for (Map.Entry<Date, List<Double>> entry : dailyQualityMap.entrySet()) {
                String day = entry.getKey().toString();
                double average = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                dailyQualitySeries.getData().add(new XYChart.Data<>(day, average));
            }
            qualityChart.getData().clear();
            qualityChart.getData().add(dailyQualitySeries);

            // Update UI with employee details
            nameField.setText(fullName);
            designationField.setText(designation);
            genderField.setText(gender);
            trainingField.setText(trainingDays + " days");

            // Update skill matrix progress bars
            updateSkillMatrix(actualAverages);

        } catch (Exception e) {
            showError("Error fetching data: " + e.getMessage());
        }
    }

    private void updateSkillMatrix(Map<String, Double> operationAverages) {
        setProgressBar(operation1Bar, operation1Label, operationAverages.getOrDefault("Operation 1", 0.0));
        setProgressBar(operation2Bar, operation2Label, operationAverages.getOrDefault("Operation 2", 0.0));
        setProgressBar(operation3Bar, operation3Label, operationAverages.getOrDefault("Operation 3", 0.0));
        setProgressBar(operation4Bar, operation4Label, operationAverages.getOrDefault("Operation 4", 0.0));
        setProgressBar(operation5Bar, operation5Label, operationAverages.getOrDefault("Operation 5", 0.0));
        setProgressBar(operation6Bar, operation6Label, operationAverages.getOrDefault("Operation 6", 0.0));
    }
    private void setProgressBar(ProgressBar progressBar, Label percentageLabel, double average) {
        double progress = average / 100.0; // Convert percentage to a 0-1 range
        progressBar.setProgress(progress);
        percentageLabel.setText(String.format("%.0f%%", average));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/mainForm.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("MAS Intimates");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
