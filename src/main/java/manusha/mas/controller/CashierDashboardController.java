package manusha.mas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import manusha.mas.model.PetSupply;
import manusha.mas.util.DatabaseConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashierDashboardController {

    public TextField txtSupplyName;
    public ComboBox <String> cmbSupplyCategory;
    public TextField txtSupplyPrice;
    public TextField txtSupplyStock;
    public Button btnAddSupply;
    public Button btnSearchSupplies;
    @FXML
    private TextField txtCustomerName, txtProductID, txtQuantity, txtTotalPrice;
    @FXML
    private Button btnCalculateTotal, btnCompleteSale;
    @FXML
    private ComboBox<String> cmbSearchCategory;
    @FXML
    private TextField txtSearchKeyword;
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

    private final Connection connection;

    private ObservableList<PetSupply> supplyList = FXCollections.observableArrayList();

    {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        setupPetSupplyTable();
        loadPetSupplies();
    }

    private void setupPetSupplyTable() {
        colSupplyID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colSupplyName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colSupplyCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colSupplyPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        colSupplyStock.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
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
    private void calculateTotal(ActionEvent event) {
        try {
            int productId = Integer.parseInt(txtProductID.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());

            String query = "SELECT price, stock FROM pet_supplies WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, productId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        double price = resultSet.getDouble("price");
                        int stock = resultSet.getInt("stock");

                        if (quantity > stock) {
                            showAlert("Error", "Insufficient stock available!");
                        } else {
                            double totalPrice = price * quantity;
                            txtTotalPrice.setText(String.format("%.2f", totalPrice));
                        }
                    } else {
                        showAlert("Error", "Product not found!");
                    }
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Product ID and Quantity.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void completeSale(ActionEvent event) {
        try {
            int productId = Integer.parseInt(txtProductID.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());
            String customerName = txtCustomerName.getText();

            String query = "SELECT price, stock, name FROM pet_supplies WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, productId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        double price = resultSet.getDouble("price");
                        int stock = resultSet.getInt("stock");
                        String productName = resultSet.getString("name");

                        if (quantity > stock) {
                            showAlert("Error", "Insufficient stock available!");
                            return;
                        }

                        // Update stock in database
                        String updateQuery = "UPDATE pet_supplies SET stock = stock - ? WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, quantity);
                            updateStatement.setInt(2, productId);
                            updateStatement.executeUpdate();
                        }

                        // Generate receipt
                        double totalPrice = price * quantity;
                        String receipt = generateReceipt(customerName, productName, quantity, price, totalPrice);

                        // Save receipt to file
                        saveReceiptToFile(receipt);

                        // Success message
                        showAlert("Success", "Sale completed successfully!");
                        loadPetSupplies();
                        clearForm();
                    } else {
                        showAlert("Error", "Product not found!");
                    }
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Product ID and Quantity.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateReceipt(String customerName, String productName, int quantity, double price, double totalPrice) {
        return String.format(
                "Receipt\n=======\nCustomer: %s\nProduct: %s\nQuantity: %d\nPrice: %.2f\nTotal: %.2f\n",
                customerName, productName, quantity, price, totalPrice
        );
    }

    private void saveReceiptToFile(String receipt) {
        try (FileWriter writer = new FileWriter("receipt.txt")) {
            writer.write(receipt);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save receipt.");
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


    private void clearSupplyForm() {
        txtSupplyName.clear();
        cmbSupplyCategory.setValue(null);
        txtSupplyPrice.clear();
        txtSupplyStock.clear();
    }
    private void clearForm() {
        txtCustomerName.clear();
        txtProductID.clear();
        txtQuantity.clear();
        txtTotalPrice.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
