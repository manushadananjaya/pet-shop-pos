package manusha.mas.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
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
import manusha.mas.util.DatabaseConnection;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class TrainingAssistantController {

    public ListView nameSuggestionsListView;
    @FXML
    private Button attendanceBtn;

    @FXML
    private RadioButton attendanceNo;

    @FXML
    private AnchorPane attendancePane;

    @FXML
    private TableColumn<EmployeeAttendance, Double> attendancePercentColumn;

    @FXML
    private TableView<EmployeeAttendance> attendanceTable;

    @FXML
    private RadioButton attendanceYes;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField designationField;

    @FXML
    private TableColumn<EmployeeAttendance, String> epfColumn;

    @FXML
    private TextField epfField;

    @FXML
    private BarChart<String, Number> qualityChart;

    @FXML
    private TextField genderField;

    @FXML
    private Button individualPerformanceBtn;

    @FXML
    private Button logOutBtn;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private TableColumn<EmployeeAttendance, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField nameFieldIndividualPerfomance;

    @FXML
    private Slider operation1Slider;

    @FXML
    private Label operation1ValueLabel;

    @FXML
    private Slider operation2Slider;

    @FXML
    private Label operation2ValueLabel;

    @FXML
    private Slider operation3Slider;

    @FXML
    private Label operation3ValueLabel;

    @FXML
    private Slider operation4Slider;

    @FXML
    private Label operation4ValueLabel;

    @FXML
    private Slider operation5Slider;

    @FXML
    private Label operation5ValueLabel;

    @FXML
    private Slider operation6Slider;

    @FXML
    private Label operation6ValueLabel;

    @FXML
    private BarChart<String, Number> performanceChart;

    @FXML
    private BarChart<String, Number> performanceDailyChart;

    @FXML
    private Button performanceFormBtn;

    @FXML
    private Slider performanceSlider;

    @FXML
    private Label performanceValueLabel;

    @FXML
    private Slider qualitySlider;

    @FXML
    private Label qualityValueLabel;

    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane trainingAssistantPane;

    @FXML
    private TextField trainingField;

    @FXML
    private AnchorPane trainingPerformancePane;

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



    private final ObservableList<String> monthsList = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    );





    @FXML
    private Button saveButton;


    private ObservableList<String> suggestionsList = FXCollections.observableArrayList();



    @FXML
    public void initialize() {
        nameSuggestionsListView.setItems(suggestionsList);
        nameSuggestionsListView.setVisible(false); // Hide by default

        nameFieldIndividualPerfomance.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) { // Only query when input length > 1
                fetchNameSuggestions(newValue);
            } else {
                nameSuggestionsListView.setVisible(false);
            }
        });

        nameSuggestionsListView.setOnMouseClicked((MouseEvent event) -> {
            String selectedName = (String) nameSuggestionsListView.getSelectionModel().getSelectedItem();
            if (selectedName != null) {
                nameFieldIndividualPerfomance.setText(selectedName);
                populateEpfField(selectedName);
                nameSuggestionsListView.setVisible(false);
            }
        });

        bindSliderToLabel(performanceSlider, performanceValueLabel);
        bindSliderToLabel(qualitySlider, qualityValueLabel);
        bindSliderToLabel(operation1Slider, operation1ValueLabel);
        bindSliderToLabel(operation2Slider, operation2ValueLabel);
        bindSliderToLabel(operation3Slider, operation3ValueLabel);
        bindSliderToLabel(operation4Slider, operation4ValueLabel);
        bindSliderToLabel(operation5Slider, operation5ValueLabel);
        bindSliderToLabel(operation6Slider, operation6ValueLabel);

        showPane(trainingAssistantPane);
        highlightButton(individualPerformanceBtn);

        monthComboBox.setItems(monthsList);
        monthComboBox.setOnAction(event -> populateAttendanceTable());

        // Set up static columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        epfColumn.setCellValueFactory(new PropertyValueFactory<>("epf"));
        attendancePercentColumn.setCellValueFactory(new PropertyValueFactory<>("attendancePercentage"));

    }



    @FXML
    private void individualPerformanceBtn() {
        showPane(trainingAssistantPane);
        highlightButton(individualPerformanceBtn);
    }

    @FXML
    private void performanceFormBtn() {
        showPane(trainingPerformancePane);
        highlightButton(performanceFormBtn);
    }

    @FXML
    private void attendanceBtn() {
        showPane(attendancePane);
        highlightButton(attendanceBtn);
    }


    private void showPane(AnchorPane paneToShow) {
        trainingAssistantPane.setVisible(false);
        attendancePane.setVisible(false);
        trainingPerformancePane.setVisible(false);
        paneToShow.setVisible(true);
    }


    private void bindSliderToLabel(Slider slider, Label label) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(String.format("%.0f%%", newValue.doubleValue()));
        });
    }

    private void fetchNameSuggestions(String input) {
        suggestionsList.clear();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT full_name FROM employees WHERE full_name LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, input + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                suggestionsList.add(resultSet.getString("full_name"));
            }

            nameSuggestionsListView.setVisible(!suggestionsList.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateEpfField(String name) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT epf_number FROM employees WHERE full_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                epfField.setText(resultSet.getString("epf_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveButtonClick() {
        if (nameFieldIndividualPerfomance.getText().trim().isEmpty() ||
                epfField.getText().trim().isEmpty() ||
                datePicker.getValue() == null ||
                (!attendanceYes.isSelected() && !attendanceNo.isSelected())) {

            // Show warning alert for missing fields
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields and ensure attendance is selected.");
            alert.show();
            return; // Stop execution if validation fails
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO employee_performance (name, epf, date, attendance, performance, quality, operation1, operation2, operation3, operation4, operation5, operation6) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);

            // Set name and EPF from text fields
            stmt.setString(1, nameFieldIndividualPerfomance.getText().trim());
            stmt.setString(2, epfField.getText().trim());

            // Set date from date picker
            stmt.setDate(3, java.sql.Date.valueOf(datePicker.getValue()));

            // Set attendance as boolean (converted to "Yes" or "No")
            stmt.setInt(4, attendanceYes.isSelected() ? 1 : 0);

            // Set performance, quality, and operation values from sliders
            stmt.setDouble(5, performanceSlider.getValue()); // Set as double to preserve decimal precision
            stmt.setDouble(6, qualitySlider.getValue());
            stmt.setDouble(7, operation1Slider.getValue());
            stmt.setDouble(8, operation2Slider.getValue());
            stmt.setDouble(9, operation3Slider.getValue());
            stmt.setDouble(10, operation4Slider.getValue());
            stmt.setDouble(11, operation5Slider.getValue());
            stmt.setDouble(12, operation6Slider.getValue());

            // Execute the update
            stmt.executeUpdate();

            // Display success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data saved successfully!");
            alert.show();
        } catch (Exception e) {
            // Print stack trace and show error alert
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save data: " + e.getMessage());
            alert.show();
        }
    }

    private void highlightButton(Button activeButton) {
        // Reset styles for all buttons
        individualPerformanceBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        performanceFormBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        attendanceBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        // Apply the active style to the selected button
        activeButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
    }


    // Training performance form
    @FXML
    private void handleSearchEPF() {
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


    private void populateAttendanceTable() {
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth == null) {
            showError("Please select a month.");
            return;
        }

        // Clear existing data and columns
        attendanceTable.getItems().clear();
        attendanceTable.getColumns().removeIf(column -> column.getText().startsWith("Day"));

        // Fetch attendance data for the selected month
        try (Connection connection = DatabaseConnection.getConnection()) {
            YearMonth yearMonth = getYearMonthFromSelectedMonth(selectedMonth);
            int daysInMonth = yearMonth.lengthOfMonth();

            // Add dynamic columns for each day
            for (int day = 1; day <= daysInMonth; day++) {
                TableColumn<EmployeeAttendance, Boolean> dayColumn = new TableColumn<>("Day " + day);
                final int dayIndex = day;
                dayColumn.setCellValueFactory(data -> data.getValue().getDailyAttendanceProperty(dayIndex));
                attendanceTable.getColumns().add(dayColumn);
            }

            // Query to fetch attendance data from employee_performance table
            String query = "SELECT name, epf, date, attendance " +
                    "FROM employee_performance " +
                    "WHERE EXTRACT(MONTH FROM date) = ? AND EXTRACT(YEAR FROM date) = ? " +
                    "ORDER BY epf, date";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, yearMonth.getMonthValue());
            stmt.setInt(2, yearMonth.getYear());

            ResultSet rs = stmt.executeQuery();

            // Map to store employee attendance
            List<EmployeeAttendance> employeeAttendances = new ArrayList<>();

            while (rs.next()) {
                String name = rs.getString("name");
                String epf = rs.getString("epf");
                Date attendanceDate = rs.getDate("date");
                boolean isPresent = rs.getBoolean("attendance");

                // Find or create EmployeeAttendance object
                EmployeeAttendance employeeAttendance = employeeAttendances.stream()
                        .filter(e -> e.getEpf().equals(epf))
                        .findFirst()
                        .orElseGet(() -> {
                            EmployeeAttendance newEntry = new EmployeeAttendance(name, epf, daysInMonth);
                            employeeAttendances.add(newEntry);
                            return newEntry;
                        });

                // Set attendance for the day
                if (attendanceDate != null) {
                    int day = attendanceDate.toLocalDate().getDayOfMonth();
                    employeeAttendance.setAttendance(day, isPresent);
                }
            }

            // Calculate attendance percentages
            employeeAttendances.forEach(e -> e.calculateAttendancePercentage(20));

            // Add data to the table
            attendanceTable.setItems(FXCollections.observableArrayList(employeeAttendances));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load attendance data: " + e.getMessage());
        }
    }

    private YearMonth getYearMonthFromSelectedMonth(String selectedMonth) {
        int monthIndex = monthsList.indexOf(selectedMonth) + 1;
        int currentYear = LocalDate.now().getYear();
        return YearMonth.of(currentYear, monthIndex);
    }

    // Inner class for table data representation
    public static class EmployeeAttendance {
        private final String name;
        private final String epf;
        private final List<Boolean> dailyAttendance;
        private double attendancePercentage;

        public EmployeeAttendance(String name, String epf, int daysInMonth) {
            this.name = name;
            this.epf = epf;
            this.dailyAttendance = new ArrayList<>(Collections.nCopies(daysInMonth, false));
        }

        public String getName() {
            return name;
        }

        public String getEpf() {
            return epf;
        }

        public double getAttendancePercentage() {
            return attendancePercentage;
        }

        public void setAttendance(int day, boolean isPresent) {
            dailyAttendance.set(day - 1, isPresent);
        }

        public ObservableValue<Boolean> getDailyAttendanceProperty(int day) {
            return new SimpleBooleanProperty(dailyAttendance.get(day - 1));
        }

        public void calculateAttendancePercentage(int totalWorkingDays) {
            long presentDays = dailyAttendance.stream().filter(Boolean::booleanValue).count();
            attendancePercentage = (presentDays / (double) totalWorkingDays) * 100;
        }
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
