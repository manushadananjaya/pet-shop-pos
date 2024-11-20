package manusha.mas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import manusha.mas.model.Cashier;
import manusha.mas.model.PetSupply;
import manusha.mas.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDashboardController {

    public Button delCashier;
    public Button btnClearSearch;
    public Button btnDelSupply;
    // Cashier Section Fields
    @FXML
    private TextField txtUsername, txtPassword, txtName, txtEmail, txtAddress, txtPhone, txtAge;
    @FXML
    private ComboBox<String> cmbGender;
    @FXML
    private TableView<Cashier> tblCashiers;
    @FXML
    private TableColumn<Cashier, Integer> colCashierID;
    @FXML
    private TableColumn<Cashier, String> colCashierUsername, colCashierName, colCashierEmail, colCashierPhone, colCreatedDate;

    // Pet Supplies Section Fields
    @FXML
    private TextField txtSupplyName, txtSupplyPrice, txtSupplyStock, txtSearchKeyword;
    @FXML
    private ComboBox<String> cmbSupplyCategory, cmbSearchCategory;
    @FXML
    private TableView<PetSupply> tblPetSupplies;
    @FXML
    private TableColumn<PetSupply, Integer> colSupplyID;
    @FXML
    private TableColumn<PetSupply, String> colSupplyName, colSupplyCategory;
    @FXML
    private TableColumn<PetSupply, Double> colSupplyPrice;
    @FXML
    private TableColumn<PetSupply, Integer> colSupplyStock;

    @FXML
    private Button btnAddSupply, btnSaveCashier, btnSearchSupplies;

    private final Connection connection;

    {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<Cashier> cashierList = FXCollections.observableArrayList();
    private ObservableList<PetSupply> supplyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        cmbSupplyCategory.setItems(FXCollections.observableArrayList("Pet Toys", "Harnesses", "Cages", "Grooming Products", "Collars", "Food"));
        cmbSearchCategory.setItems(FXCollections.observableArrayList("Pet Toys", "Harnesses", "Cages", "Grooming Products", "Collars", "Food"));

        setupCashierTable();
        setupPetSupplyTable();
        loadCashiers();
        loadPetSupplies();

        btnClearSearch.setOnAction(this::clearSearch);
        btnDelSupply.setOnAction(this::deleteSelectedSupply);
        delCashier.setOnAction(this::deleteSelectedCashier);
    }

    private void setupCashierTable() {
        colCashierID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCashierUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colCashierName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colCashierEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colCashierPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colCreatedDate.setCellValueFactory(cellData -> cellData.getValue().createdDateProperty());
    }

    private void setupPetSupplyTable() {
        colSupplyID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colSupplyName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colSupplyCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colSupplyPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        colSupplyStock.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
    }

    private void loadCashiers() {
        cashierList.clear();
        String query = "SELECT * FROM cashiers";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                cashierList.add(new Cashier(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("created_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tblCashiers.setItems(cashierList);
    }

    private void loadPetSupplies() {
        supplyList.clear();
        String query = "SELECT * FROM pet_supplies";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                supplyList.add(new PetSupply(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("category"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tblPetSupplies.setItems(supplyList);
    }



    @FXML
    private void addCashier(ActionEvent event) {
        String query = "INSERT INTO cashiers (username, password, name, email, address, phone, age, gender, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, txtUsername.getText());
            statement.setString(2, txtPassword.getText());
            statement.setString(3, txtName.getText());
            statement.setString(4, txtEmail.getText());
            statement.setString(5, txtAddress.getText());
            statement.setString(6, txtPhone.getText());
            statement.setInt(7, Integer.parseInt(txtAge.getText()));
            statement.setString(8, cmbGender.getValue());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Cashier added successfully!");
                loadCashiers();
                clearCashierForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add cashier.");
        }
    }

    @FXML
    private void addPetSupply(ActionEvent event) {
        String query = "INSERT INTO pet_supplies (name, category, price, stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, txtSupplyName.getText());
            statement.setString(2, cmbSupplyCategory.getValue());
            statement.setDouble(3, Double.parseDouble(txtSupplyPrice.getText()));
            statement.setInt(4, Integer.parseInt(txtSupplyStock.getText()));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Pet supply added successfully!");
                loadPetSupplies();
                clearSupplyForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add pet supply.");
        }
    }

    @FXML
    private void searchPetSupplies(ActionEvent event) {
        supplyList.clear();
        String category = cmbSearchCategory.getValue();
        String keyword = txtSearchKeyword.getText();

        // Adjust query depending on whether a category is selected
        String query;
        if (category == null || category.isEmpty()) {
            query = "SELECT * FROM pet_supplies WHERE name LIKE ?"; // Search all categories
        } else {
            query = "SELECT * FROM pet_supplies WHERE category = ? AND name LIKE ?"; // Search specific category
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (category == null || category.isEmpty()) {
                statement.setString(1, "%" + keyword + "%");
            } else {
                statement.setString(1, category);
                statement.setString(2, "%" + keyword + "%");
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    supplyList.add(new PetSupply(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("category"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("stock")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tblPetSupplies.setItems(supplyList);
    }


    private void clearCashierForm() {
        txtUsername.clear();
        txtPassword.clear();
        txtName.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtPhone.clear();
        txtAge.clear();
        cmbGender.setValue(null);
    }

    private void clearSupplyForm() {
        txtSupplyName.clear();
        txtSupplyPrice.clear();
        txtSupplyStock.clear();
        cmbSupplyCategory.setValue(null);
    }

    @FXML
    private void clearSearch(ActionEvent event) {
        txtSearchKeyword.clear();
        cmbSearchCategory.setValue(null);
        loadPetSupplies(); // Reload all pet supplies
    }

    @FXML
    private void deleteSelectedSupply(ActionEvent event) {
        PetSupply selectedSupply = tblPetSupplies.getSelectionModel().getSelectedItem();
        if (selectedSupply == null) {
            showAlert("Warning", "Please select a supply to delete.");
            return;
        }

        String query = "DELETE FROM pet_supplies WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, selectedSupply.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Pet supply deleted successfully!");
                loadPetSupplies(); // Refresh the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete pet supply.");
        }
    }

    @FXML
    private void deleteSelectedCashier(ActionEvent event) {
        Cashier selectedCashier = tblCashiers.getSelectionModel().getSelectedItem();
        if (selectedCashier == null) {
            showAlert("Warning", "Please select a cashier to delete.");
            return;
        }

        String query = "DELETE FROM cashiers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, selectedCashier.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Cashier deleted successfully!");
                loadCashiers(); // Refresh the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete cashier.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
