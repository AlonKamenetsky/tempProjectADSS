package SuppliersModule.PresentationLayer.GUI;

import SuppliersModule.ServiceLayer.ServiceController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductMenuController {
    ServiceController serviceController;
    public ProductMenuController() {
        serviceController = ServiceController.getInstance();
    }

    public void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onPrintAllProducts(ActionEvent actionEvent) {
        String[] products = serviceController.getAllProductsAsStrings();
        String joinedProducts = String.join("\n", products); // Join with line breaks

        TextArea textArea = new TextArea(joinedProducts);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Products");
        alert.setHeaderText("Product List");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }


    public void onPrintProduct(ActionEvent actionEvent) {
        TextInputDialog productIDDialog = new TextInputDialog();
        productIDDialog.setTitle("Enter Product ID");
        if(productIDDialog.showAndWait().orElse("") == null) return;
        int productID = Integer.parseInt(productIDDialog.getResult());
        String product = serviceController.getProductAsString(productID);
        TextArea textArea = new TextArea(product);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Requested product");
        alert.setHeaderText("Product");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();

    }

    public void onUpdateProduct(ActionEvent actionEvent) {
        TextInputDialog productIDDialog = new TextInputDialog();
        productIDDialog.setTitle("Enter Product ID");
        if(productIDDialog.showAndWait().orElse("") == null) return;
        int productID = Integer.parseInt(productIDDialog.getResult());
        TextInputDialog productNameDialog = new TextInputDialog();
        productNameDialog.setHeaderText("Enter product name:");
        if(productNameDialog.showAndWait().orElse("") == null) return;
        String productName = productNameDialog.getResult();
        TextInputDialog companyNameDialog = new TextInputDialog();
        companyNameDialog.setHeaderText("Enter company name:");
        if(companyNameDialog.showAndWait().orElse("") == null) return;
        String companyName = companyNameDialog.getResult();
        TextInputDialog weightDialog = new TextInputDialog();
        weightDialog.setHeaderText("Enter weight in kg:");
        if(weightDialog.showAndWait().orElse("") == null) return;
        float productWeight = Float.parseFloat(weightDialog.getResult());
        TextInputDialog categoryDialog = new TextInputDialog();
        categoryDialog.setHeaderText("Enter category:");
        if(categoryDialog.showAndWait().orElse("") == null) return;
        int newCategory = Integer.parseInt(categoryDialog.getResult());
        try{
            if(serviceController.updateProduct(productID, productName, companyName, newCategory)){
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setContentText("Product updated successfully");
                success.showAndWait();
            }
            else{
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error adding a new product");
                error.showAndWait();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error adding a new product");
            error.showAndWait();
        }

    }

    public void onAddProduct(ActionEvent event) {
        TextInputDialog productNameDialog = new TextInputDialog();
        productNameDialog.setHeaderText("Enter product name:");
        if(productNameDialog.showAndWait().orElse("") == null) return;
        String productName = productNameDialog.getResult();
        TextInputDialog companyNameDialog = new TextInputDialog();
        companyNameDialog.setHeaderText("Enter company name:");
        if(companyNameDialog.showAndWait().orElse("") == null) return;
        String companyName = companyNameDialog.getResult();
        TextInputDialog weightDialog = new TextInputDialog();
        weightDialog.setHeaderText("Enter weight in kg:");
        if(weightDialog.showAndWait().orElse("") == null) return;
        float productWeight = Float.parseFloat(weightDialog.getResult());
        TextInputDialog categoryDialog = new TextInputDialog();
        categoryDialog.setHeaderText("Enter category:");
        if(categoryDialog.showAndWait().orElse("") == null) return;
        int newCategory = Integer.parseInt(categoryDialog.getResult());
        try{
            int id = serviceController.registerNewProduct(productName, companyName, newCategory, productWeight);
            if(id != -1){
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setContentText("Product added successfully");
                success.showAndWait();
            }
            else{
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error adding a new product");
                error.showAndWait();

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onDeleteProduct(ActionEvent actionEvent) {
        TextInputDialog productIDDialog = new TextInputDialog();
        productIDDialog.setHeaderText("Enter product ID:");
        if(productIDDialog.showAndWait().orElse("") == null) return;
        int productID = Integer.parseInt(productIDDialog.getResult());
        if(productID < 0 )
            return;
        if(serviceController.deleteProduct(productID)){
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setContentText("Product deleted successfully");
            success.showAndWait();
        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error while deleting the  product");
            error.showAndWait();
        }
    }
    private void openView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
