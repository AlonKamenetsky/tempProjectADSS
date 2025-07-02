package Transportation.Presentation.gui;

import Transportation.DTO.TruckDTO;
import Transportation.Service.TruckService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class TruckMenuController {

    private final TruckService TrucksHandler = new TruckService();

    @FXML private Button viewAllTrucksButton;
    @FXML private Button viewTruckByLicenseButton;
    @FXML private Button setTruckAvailabilityButton;
    @FXML private Button addTruckButton;
    @FXML private Button showRemoveTruckButton;
    @FXML private VBox removeTruckBox;
    @FXML private ComboBox<String> truckComboBox;
    @FXML private Button backButton;

    @FXML
    private void onViewAllTrucks() {
        String output = TrucksHandler.viewAllTrucks();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, output);
        alert.setTitle("Truck Menu");
        alert.setHeaderText("All Trucks:");
        alert.showAndWait();
    }

    @FXML
    private void onViewTruckByLicense() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter license number:");
        dialog.showAndWait();
        String licenseNumber = dialog.getResult();
        if (licenseNumber != null) {
            String result = TrucksHandler.getTruckByLicenseNumber(licenseNumber);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, result);
            alert.setTitle("Truck Menu");
            alert.setHeaderText("Truck Details");
            alert.showAndWait();
        }
    }

    @FXML
    private void onSetTruckAvailability() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Truck Availability");
        dialog.setHeaderText("Enter license number:");
        dialog.showAndWait();

        String licenseNumber = dialog.getResult();

        if (licenseNumber != null && !licenseNumber.isBlank()) {
            boolean valid = false;
            while (!valid) {
                TextInputDialog availabilityDialog = new TextInputDialog();
                availabilityDialog.setTitle("Truck Availability");
                availabilityDialog.setHeaderText("What is the truck's availability? (Free/Busy)");
                var result = availabilityDialog.showAndWait();
                if (result.isEmpty()) return;

                String availability = result.get().trim();
                if (availability.equalsIgnoreCase("Free")) {
                    TrucksHandler.ChangeTruckAvailability(licenseNumber, true);
                    new Alert(Alert.AlertType.INFORMATION, "Truck set to available").showAndWait();
                    valid = true;

                } else if (availability.equalsIgnoreCase("Busy")) {
                    TrucksHandler.ChangeTruckAvailability(licenseNumber, false);
                    new Alert(Alert.AlertType.INFORMATION, "Truck set to Busy").showAndWait();
                    valid = true;

                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Invalid Input");
                    error.setContentText("Please enter only 'Free' or 'Busy'");
                    error.showAndWait();
                }
            }
        }
    }

    @FXML
    private void onAddTruck() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter license number:");
        dialog.showAndWait();
        String licenseNumber = dialog.getResult();

        dialog.setHeaderText("Enter truck's type (Small/Medium/Large):");
        dialog.showAndWait();
        String truckType = dialog.getResult();

        dialog.setHeaderText("Enter truck's model:");
        dialog.showAndWait();
        String model = dialog.getResult();

        dialog.setHeaderText("Enter truck's Net Weight:");
        dialog.showAndWait();
        String netWeight = dialog.getResult();
        float netF = Float.parseFloat(netWeight);

        dialog.setHeaderText("Enter truck's Maximum Weight:");
        dialog.showAndWait();
        String maximumWeight = dialog.getResult();
        float maxF = Float.parseFloat(maximumWeight);

        try {
            TrucksHandler.AddTruck(truckType, licenseNumber, model, netF, maxF);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Truck Added Successfully");
            success.setContentText("Truck with license " + licenseNumber + " was added.");
            success.showAndWait();
        } catch (InstanceAlreadyExistsException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Truck Already Exists");
            error.setContentText("A truck with license number " + licenseNumber + " already exists.");
            error.showAndWait();
        }
    }

    @FXML
    private void onShowRemoveTruck() {
        removeTruckBox.setVisible(true);
        removeTruckBox.setManaged(true);
        truckComboBox.getItems().clear();

        List<TruckDTO> trucks = TrucksHandler.getAllTrucks();
        for (TruckDTO truck : trucks) {
            truckComboBox.getItems().add(truck.licenseNumber());

        }

    }

    @FXML
    private void onRemoveTruck() {
        String selectedLicense = truckComboBox.getValue();

        if (selectedLicense == null || selectedLicense.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No truck selected.");
            alert.showAndWait();
            return;
        }

        try {
            TrucksHandler.deleteTruck(selectedLicense);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Truck removed successfully: " + selectedLicense);
            success.showAndWait();

            truckComboBox.getItems().remove(selectedLicense);
            truckComboBox.setValue(null);

            removeTruckBox.setVisible(false);
            removeTruckBox.setManaged(false);


        } catch (NoSuchElementException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Truck Not Found");
            alert.setHeaderText("Removal Failed");
            alert.setContentText("Truck with license number " + selectedLicense + " does not exist.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransportationMenu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
