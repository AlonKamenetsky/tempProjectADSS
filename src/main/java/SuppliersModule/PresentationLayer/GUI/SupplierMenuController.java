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
import java.util.ArrayList;

public class SupplierMenuController {
    ServiceController serviceController;
    public SupplierMenuController() {
        serviceController = ServiceController.getInstance();
    }
    public void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onAddSupplier(ActionEvent actionEvent) {

        TextInputDialog supplierNameDialog = new TextInputDialog();
        supplierNameDialog.setTitle("Enter Supplier Name");
        if(supplierNameDialog.showAndWait().orElse("") == null) return;
        String supplierName = supplierNameDialog.getResult();

        TextInputDialog supplierProductCategoryDialog = new TextInputDialog();
        supplierProductCategoryDialog.setTitle("Enter Supplier Product Category");
        if(supplierProductCategoryDialog.showAndWait().orElse("") == null) return;
        int productCategory =  Integer.parseInt(supplierProductCategoryDialog.getResult());

        TextInputDialog supplierBankAccountDialog = new TextInputDialog();
        supplierBankAccountDialog.setTitle("Enter Supplier Bank Account");
        if(supplierBankAccountDialog.showAndWait().orElse("") == null) return;
        String bankAccount = supplierBankAccountDialog.getResult();

        TextInputDialog supplierPaymentMethodDialog = new TextInputDialog();
        supplierPaymentMethodDialog.setTitle("Enter Supplier Payment Method");
        if(supplierPaymentMethodDialog.showAndWait().orElse("") == null) return;
        int paymentMethod =  Integer.parseInt(supplierPaymentMethodDialog.getResult());

        TextInputDialog deliveryMethodDialog = new TextInputDialog();
        deliveryMethodDialog.setTitle("Enter Supplier Delivery Method");
        if(deliveryMethodDialog.showAndWait().orElse("") == null) return;
        int deliveryMethod =  Integer.parseInt(deliveryMethodDialog.getResult());

        TextInputDialog supplierPhoneNumberDialog = new TextInputDialog();
        supplierPhoneNumberDialog.setTitle("Enter Supplier Phone Number");
        if(supplierPhoneNumberDialog.showAndWait().orElse("") == null) return;
        String phoneNumber = supplierPhoneNumberDialog.getResult();

        TextInputDialog supplierAddressDialog = new TextInputDialog();
        supplierAddressDialog.setTitle("Enter Supplier Address");
        if(supplierAddressDialog.showAndWait().orElse("") == null) return;
        String address = supplierAddressDialog.getResult();

        TextInputDialog supplierEmailAddressDialog = new TextInputDialog();
        supplierEmailAddressDialog.setTitle("Enter Supplier Email Address");
        if(supplierEmailAddressDialog.showAndWait().orElse("") == null) return;
        String emailAddress = supplierEmailAddressDialog.getResult();

        TextInputDialog supplierContactNameDialog = new TextInputDialog();
        supplierContactNameDialog.setTitle("Enter Supplier Contact Name");
        if(supplierContactNameDialog.showAndWait().orElse("") == null) return;
        String contactName = supplierContactNameDialog.getResult();

        TextInputDialog supplierSupplyMethodDialog = new TextInputDialog();
        supplierSupplyMethodDialog.setTitle("Enter Supplier Supply Method");
        if(supplierSupplyMethodDialog.showAndWait().orElse("") == null) return;
        int supplyMethod =  Integer.parseInt(supplierSupplyMethodDialog.getResult());

        ArrayList<Integer> supplyDays = null;
        if(supplyMethod == 0) {
            while (true){
                TextInputDialog supplyDaysDialog = new TextInputDialog();
                supplyDaysDialog.setTitle("Enter regular day");
                if(supplyDaysDialog.showAndWait().orElse("") == null) return;
                int supplyDay =  Integer.parseInt(supplyDaysDialog.getResult());
                if(supplyDay == -1){
                    break;
                }
                else{
                    supplyDays.add(supplyDay);
                }
            }
        }
        int supplierID = serviceController.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveryMethod, phoneNumber, address, emailAddress, contactName, bankAccount, paymentMethod, supplyDays);
        if(supplierID == -1) {
            ArrayList<double[]> dataArray = new ArrayList<>();
            while (true) {
                TextInputDialog productIDDialog = new TextInputDialog();
                productIDDialog.setTitle("Enter Product ID");
                if (productIDDialog.showAndWait().orElse("") == null) return;
                int productID = Integer.parseInt(productIDDialog.getResult());

                if(productID == -1) break;
                TextInputDialog productPriceDialog = new TextInputDialog();
                productPriceDialog.setTitle("Enter Product Price");
                if(productPriceDialog.showAndWait().orElse("") == null) return;
                double productPrice = Double.parseDouble(productPriceDialog.getResult());

                TextInputDialog productQuantityForDiscountDialog = new TextInputDialog();
                productQuantityForDiscountDialog.setTitle("Enter Product Quantity for Discount");
                if(productQuantityForDiscountDialog.showAndWait().orElse("") == null) return;
                int productQuantityForDiscount = Integer.parseInt(productQuantityForDiscountDialog.getResult());

                TextInputDialog productDiscountPercentageDialog = new TextInputDialog();
                productDiscountPercentageDialog.setTitle("Enter Product Discount Percentage");
                if(productDiscountPercentageDialog.showAndWait().orElse("") == null) return;
                int productDiscountPercentage = Integer.parseInt(productDiscountPercentageDialog.getResult());
                double data[] = {productID, productPrice, productQuantityForDiscount, productDiscountPercentage};
                dataArray.add(data);
            }
            if(serviceController.registerNewContract(supplierID, dataArray)) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Success");
                error.setHeaderText("Contract added successfully");
                error.showAndWait();
            }
            else{
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error registering new contract");
                error.showAndWait();
            }

        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error adding a new product");
            error.showAndWait();
        }

    }

    public void onUpdateSupplier(ActionEvent actionEvent) {
    }

    public void onDeleteSupplier(ActionEvent actionEvent) {
        TextInputDialog supplierIDDialog = new TextInputDialog();
        supplierIDDialog.setTitle("Enter Supplier ID");
        if(supplierIDDialog.showAndWait().orElse("") == null) return;
        int supplierID =  Integer.parseInt(supplierIDDialog.getResult());
        if(serviceController.deleteSupplier(supplierID)){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Success");
            error.setHeaderText("Supplier deleted successfully");
            error.showAndWait();
        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error: Couldn't delete supplier");
            error.showAndWait();
        }
    }


    public void onPrintAllSuppliers(ActionEvent actionEvent) {

    }

    public void onPrintSupplier(ActionEvent actionEvent) {
        TextInputDialog supplierIDDialog = new TextInputDialog();
        supplierIDDialog.setTitle("Enter Supplier ID");
        if(supplierIDDialog.showAndWait().orElse("") == null) return;
        int supplierID =  Integer.parseInt(supplierIDDialog.getResult());
        String supplier = serviceController.getSupplierAsString(supplierID);
        TextArea textArea = new TextArea(supplier);
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
