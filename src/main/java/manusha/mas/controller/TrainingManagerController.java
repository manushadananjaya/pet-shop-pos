package manusha.mas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import manusha.mas.model.RequireDetails;
import manusha.mas.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

import manusha.mas.model.RequestDetails;

public class TrainingManagerController {

    @FXML
    private Button SearchBtn;

    @FXML
    private TableColumn<RequestDetails, Integer> countColumn;

    @FXML
    private TableView<RequireDetails> submittedRequirementsTable;

    @FXML
    private TableColumn<RequireDetails, String> ieepfReqColumn;

    @FXML
    private TableColumn<RequireDetails, String> operationReqColumn;

    @FXML
    private TableColumn<RequireDetails, String> nameReqColumn;

    @FXML
    private TableColumn<RequireDetails, String> sectionReqColumn;

    @FXML
    private TableColumn<RequireDetails, LocalDate> dateReqColumn;

    @FXML
    private TextField designationField;

    @FXML
    private TextField genderField;

    @FXML
    private Button logOutBtn;

    @FXML
    private TableColumn<RequestDetails, String> moduleNoColumn;

    @FXML
    private TableColumn<RequestDetails, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private ProgressBar operation1Bar;

    @FXML
    private Label operation1Label;

    @FXML
    private ProgressBar operation2Bar;

    @FXML
    private Label operation2Label;

    @FXML
    private ProgressBar operation3Bar;

    @FXML
    private Label operation3Label;

    @FXML
    private ProgressBar operation4Bar;

    @FXML
    private Label operation4Label;

    @FXML
    private ProgressBar operation5Bar;

    @FXML
    private Label operation5Label;

    @FXML
    private ProgressBar operation6Bar;

    @FXML
    private Label operation6Label;

    @FXML
    private TableColumn<RequestDetails, String> operationColumn;

    @FXML
    private BarChart<String, Number> performanceChart;

    @FXML
    private BarChart<String, Number> performanceDailyChart;

    @FXML
    private BarChart<String, Number> qualityChart;

    @FXML
    private TableColumn<RequestDetails, java.util.Date> reqDateColumn;

    @FXML
    private Button requestedDetailsBtn;

    @FXML
    private AnchorPane requestedDetailsPane;

    @FXML
    private TableView<RequestDetails> requestedDetailsTable;

    @FXML
    private Button requirementFormBtn;

    @FXML
    private AnchorPane requirementFormPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<RequestDetails, String> sectionColumn;

    @FXML
    private TextField trainingField;

    @FXML
    private Button trainingPerformanceBtn;

    @FXML
    private AnchorPane trainingPerformancePane;

    @FXML
    private TextField ieepf_no;

    @FXML
    private ComboBox<String> req_operation;

    @FXML
    private TextField req_name;

    @FXML
    private TextField req_section;

    @FXML
    private DatePicker req_date;

    private ObservableList<RequestDetails> requestedDetailsList = FXCollections.observableArrayList();

    @FXML
    private Label topPerformer1Op1, topPerformer2Op1, topPerformer3Op1;
    @FXML
    private Label topPerformer1Op2, topPerformer2Op2, topPerformer3Op2;
    @FXML
    private Label topPerformer1Op3, topPerformer2Op3, topPerformer3Op3;
    @FXML
    private Label topPerformer1Op4, topPerformer2Op4, topPerformer3Op4;
    @FXML
    private Label topPerformer1Op5, topPerformer2Op5, topPerformer3Op5;
    @FXML
    private Label topPerformer1Op6, topPerformer2Op6, topPerformer3Op6;





    public void initialize() {

        showPane(trainingPerformancePane);
        highlightButton(trainingPerformanceBtn);
        setupRequestedDetailsTable();
        loadRequestedDetails();

        ieepfReqColumn.setCellValueFactory(new PropertyValueFactory<>("ieepf"));
        operationReqColumn.setCellValueFactory(new PropertyValueFactory<>("operation"));
        nameReqColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sectionReqColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        dateReqColumn.setCellValueFactory(new PropertyValueFactory<>("reqDate"));

        // Load submitted requirements into the table
        loadSubmittedRequirements();
        updateTopPerformers(); // Load top performers

        req_operation.getItems().addAll(
                "Operation 1",
                "Operation 2",
                "Operation 3",
                "Operation 4",
                "Operation 5",
                "Operation 6"
        );

        // Optionally set a default value
        req_operation.setValue("Operation 1");



    }

    @FXML
    private void trainingPerformanceBtn(ActionEvent event) {
        showPane(trainingPerformancePane);
        highlightButton(trainingPerformanceBtn);
    }

    @FXML
    private void requestedDetailsBtn(ActionEvent event) {
        showPane(requestedDetailsPane);
        highlightButton(requestedDetailsBtn);
    }

    @FXML
    private void requirementFormBtn(ActionEvent event) {
        showPane(requirementFormPane);
        highlightButton(requirementFormBtn);
    }

    private void showPane(AnchorPane pane) {
        trainingPerformancePane.setVisible(false);
        requestedDetailsPane.setVisible(false);
        requirementFormPane.setVisible(false);
        pane.setVisible(true);
    }

    private void highlightButton(Button activeButton) {
        // Reset styles for all buttons
        trainingPerformanceBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        requirementFormBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        requestedDetailsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        // Apply the active style to the selected button
        activeButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
    }


    //Training performance

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

    //request details form
    private void setupRequestedDetailsTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("ieName"));
        moduleNoColumn.setCellValueFactory(new PropertyValueFactory<>("moduleNo"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("ieSection"));
        operationColumn.setCellValueFactory(new PropertyValueFactory<>("operation"));
        reqDateColumn.setCellValueFactory(new PropertyValueFactory<>("reqDate"));
        countColumn.setCellValueFactory(data -> data.getValue().countProperty().asObject());

        requestedDetailsTable.setItems(requestedDetailsList);
    }

    // Method to fetch requested details from the database
    private void loadRequestedDetails() {
        requestedDetailsList.clear();
        String query = "SELECT * FROM requested_details";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                RequestDetails details = new RequestDetails(
                        resultSet.getInt("id"),
                        resultSet.getString("ieepf"),
                        resultSet.getString("ie_name"),
                        resultSet.getString("ie_shift"),
                        resultSet.getString("ie_section"),
                        resultSet.getString("module_no"),
                        resultSet.getString("customer"),
                        resultSet.getString("operation"),
                        resultSet.getDate("req_date").toLocalDate(),
                        resultSet.getInt("req_count")
                );
                requestedDetailsList.add(details);
            }

        } catch (Exception e) {
            showError("Error loading requested details: " + e.getMessage());
        }
    }


    //reqirement form section
    @FXML
    private void handleSubmitRequirement(ActionEvent event) {
        // Collect form data
        String epfNo = ieepf_no.getText();
        String operation = req_operation.getValue();
        String name = req_name.getText();
        String section = req_section.getText();
        LocalDate date = req_date.getValue();

        // Validate inputs
        if (epfNo.isEmpty() || operation.isEmpty() || name.isEmpty() || section.isEmpty() || date == null) {
            showError("All fields must be filled out.");
            return;
        }


        // Convert LocalDate to SQL Date
        Date sqlDate = Date.valueOf(date);

        // Insert into the database
        String query = "INSERT INTO requirement (ieepf, operation, name, section, req_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, epfNo);
            preparedStatement.setString(2, operation);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, section);
            preparedStatement.setDate(5, sqlDate);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showSuccess("Requirement submitted successfully!");
                clearRequirementForm();
                loadSubmittedRequirements();
            } else {
                showError("Failed to submit the requirement.");
            }
        } catch (Exception e) {
            showError("Error submitting requirement: " + e.getMessage());
        }
    }
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }

    private void clearRequirementForm() {
        ieepf_no.clear();
        req_operation.setValue(null);
        req_name.clear();
        req_section.clear();
        req_date.setValue(null);
    }

    private void loadSubmittedRequirements() {
        ObservableList<RequireDetails> requirementsList = FXCollections.observableArrayList();
        String query = "SELECT ieepf, operation, name, section, req_date FROM requirement";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String ieepf = resultSet.getString("ieepf");
                String operation = resultSet.getString("operation");
                String name = resultSet.getString("name");
                String section = resultSet.getString("section");
                LocalDate reqDate = resultSet.getDate("req_date").toLocalDate();

                requirementsList.add(new RequireDetails(ieepf, operation, name, section, reqDate));
            }

            submittedRequirementsTable.setItems(requirementsList);

        } catch (Exception e) {
            showError("Error loading submitted requirements: " + e.getMessage());
        }
    }

    //top operartion performers update

    private void updateTopPerformers() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Map to hold top performers for each operation
            Map<String, List<String>> topPerformersMap = new HashMap<>();

            // Query for each operation
            for (int i = 1; i <= 6; i++) {
                String operationColumn = "operation" + i;
                String query = "SELECT name, " + operationColumn + " AS score " +
                        "FROM employee_performance " +
                        "WHERE " + operationColumn + " IS NOT NULL " +
                        "ORDER BY score DESC " +
                        "LIMIT 3";

                try (PreparedStatement statement = connection.prepareStatement(query);
                     ResultSet resultSet = statement.executeQuery()) {

                    List<String> topPerformers = new ArrayList<>();
                    while (resultSet.next()) {
                        topPerformers.add(resultSet.getString("name"));
                    }

                    // Store the results in the map
                    topPerformersMap.put(operationColumn, topPerformers);
                }
            }

            // Update the labels with the fetched names
            setTopPerformersLabels(topPerformersMap);

        } catch (Exception e) {
            showError("Error updating top performers: " + e.getMessage());
        }
    }

    private void setTopPerformersLabels(Map<String, List<String>> topPerformersMap) {
        // Update labels for Operation 1
        updateLabelsForOperation("Operation 1", topPerformersMap.getOrDefault("operation1", Collections.emptyList()),
                topPerformer1Op1, topPerformer2Op1, topPerformer3Op1);

        // Update labels for Operation 2
        updateLabelsForOperation("Operation 2", topPerformersMap.getOrDefault("operation2", Collections.emptyList()),
                topPerformer1Op2, topPerformer2Op2, topPerformer3Op2);

        // Continue for all operations...
        updateLabelsForOperation("Operation 3", topPerformersMap.getOrDefault("operation3", Collections.emptyList()),
                topPerformer1Op3, topPerformer2Op3, topPerformer3Op3);

        updateLabelsForOperation("Operation 4", topPerformersMap.getOrDefault("operation4", Collections.emptyList()),
                topPerformer1Op4, topPerformer2Op4, topPerformer3Op4);

        updateLabelsForOperation("Operation 5", topPerformersMap.getOrDefault("operation5", Collections.emptyList()),
                topPerformer1Op5, topPerformer2Op5, topPerformer3Op5);

        updateLabelsForOperation("Operation 6", topPerformersMap.getOrDefault("operation6", Collections.emptyList()),
                topPerformer1Op6, topPerformer2Op6, topPerformer3Op6);
    }

    private void updateLabelsForOperation(String operation, List<String> topPerformers,
                                          Label label1, Label label2, Label label3) {
        if (topPerformers.size() > 0) {
            label1.setText(topPerformers.get(0));
        } else {
            label1.setText("No Data");
        }

        if (topPerformers.size() > 1) {
            label2.setText(topPerformers.get(1));
        } else {
            label2.setText("No Data");
        }

        if (topPerformers.size() > 2) {
            label3.setText(topPerformers.get(2));
        } else {
            label3.setText("No Data");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/mainForm.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("MAS Intimates");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
