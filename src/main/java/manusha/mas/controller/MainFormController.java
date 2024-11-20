package manusha.mas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import manusha.mas.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;

public class MainFormController {

    @FXML
    private Button btnsignin;

    @FXML
    private TextField txtuname;

    @FXML
    private PasswordField txtpword;

    @FXML
    private ComboBox<String> roleSelect;

    // Variables for window dragging
    private double x = 0;
    private double y = 0;

    public static class getData {
        public static String username;
    }

    // FXML file paths for roles
    private static final String MANAGER_DASHBOARD = "/view/ManagerDashboard.fxml";
    private static final String CASHIER_DASHBOARD = "/view/CashierDashboard.fxml";

    @FXML
    private void initialize() {
        btnsignin.setOnAction(this::handleSignIn);
    }

    @FXML
    void handleSignIn(ActionEvent event) {
        String username = txtuname.getText().trim();
        String password = txtpword.getText().trim();
        String selectedRole = roleSelect.getValue();

        if (!validateInput(username, password, selectedRole)) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (authenticateUser(connection, username, password, selectedRole)) {
                getData.username = username;

                // Show success message and load the corresponding dashboard
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
                loadDashboard(selectedRole);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username, password, or role.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "An error occurred while loading the dashboard.");
        }
    }

    /**
     * Validates user input fields.
     *
     * @param username     The entered username.
     * @param password     The entered password.
     * @param selectedRole The selected role.
     * @return True if valid, false otherwise.
     */
    private boolean validateInput(String username, String password, String selectedRole) {
        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields and select a role.");
            return false;
        }
        return true;
    }

    /**
     * Authenticates the user against the database.
     *
     * @param connection   The database connection.
     * @param username     The entered username.
     * @param password     The entered password.
     * @param selectedRole The selected role.
     * @return True if authentication is successful, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private boolean authenticateUser(Connection connection, String username, String password, String selectedRole)
            throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, selectedRole);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Loads the appropriate dashboard based on the selected role.
     *
     * @param role The role selected by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
    private void loadDashboard(String role) throws IOException {
        String fxmlFile = switch (role) {
            case "Manager" -> MANAGER_DASHBOARD;
            case "Cashier" -> CASHIER_DASHBOARD;
            default -> throw new IllegalStateException("Unexpected role: " + role);
        };

        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        // Enable window dragging
        enableWindowDragging(root, stage);

        stage.setScene(scene);
        stage.setTitle(role + " Dashboard");
        stage.show();

        // Close the current login window
        btnsignin.getScene().getWindow().hide();
    }

    /**
     * Enables window dragging for a stage.
     *
     * @param root  The root node.
     * @param stage The stage to apply dragging to.
     */
    private void enableWindowDragging(Parent root, Stage stage) {
        root.setOnMousePressed(e -> {
            x = e.getSceneX();
            y = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - x);
            stage.setY(e.getScreenY() - y);
        });
    }

    /**
     * Displays an alert with the specified type, title, and message.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert.
     * @param message   The message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
