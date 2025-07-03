package Transportation.Presentation.gui;

import Transportation.DTO.SiteDTO;
import Transportation.Service.SiteService;
import Transportation.Service.SiteZoneService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SiteMenuController {

    private final SiteService SitesHandler = new SiteService();
    private final SiteZoneService SiteZoneHandler = new SiteZoneService();

    @FXML
    private Button viewAllSitesButton;

    @FXML
    private Button viewSiteByAddressButton;

    @FXML
    private Button addSiteButton;

    @FXML
    private Button backButton;

    @FXML
    private Button showRemoveSiteButton;

    @FXML
    private VBox removeSiteBox;

    @FXML
    private ComboBox<String> siteComboBox;

    @FXML
    private void onViewAllSites() {
        List<SiteDTO> allSites = SitesHandler.viewAllSites();
        StringBuilder sb = new StringBuilder("All Sites:\n");
        for (SiteDTO site : allSites) {
            String zoneName = SiteZoneHandler.getZoneNameBySite(site);
            sb.append("Site Address: ").append(site.siteAddress()).append("\n")
                    .append("Contact: ").append(site.contactName()).append("\n")
                    .append("Phone: ").append(site.phoneNumber()).append("\n")
                    .append("Zone: ").append(zoneName.toUpperCase()).append("\n")
                    .append("----------------------\n");
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Sites");
        alert.setHeaderText("Site List");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void onViewSiteByAddress() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter site address:");
        dialog.showAndWait();
        String siteAddress = dialog.getResult();

        if (siteAddress != null && !siteAddress.isBlank()) {
            Optional<SiteDTO> result = SitesHandler.getSiteByAddress(siteAddress);

            if (result.isPresent()) {
                SiteDTO site = result.get();
                StringBuilder siteDetails = new StringBuilder();
                siteDetails.append("Site ID: ").append(site.siteId()).append("\n");
                siteDetails.append("Address: ").append(site.siteAddress()).append("\n");
                siteDetails.append("Contact Name: ").append(site.contactName()).append("\n");
                siteDetails.append("Phone Number: ").append(site.phoneNumber()).append("\n");
                siteDetails.append("Zone ID: ").append(site.zoneId()).append("\n");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Site Menu");
                alert.setHeaderText("Site Details");
                alert.setContentText(siteDetails.toString());
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            } else {
                Alert notFound = new Alert(Alert.AlertType.ERROR);
                notFound.setTitle("Error");
                notFound.setHeaderText("Site Not Found");
                notFound.setContentText("No site found with the address: " + siteAddress);
                notFound.showAndWait();
            }
        }
    }


    @FXML
    private void onAddSite() {
        // Step 1: Site address
        TextInputDialog addressDialog = new TextInputDialog();
        addressDialog.setHeaderText("Enter new site's address:");
        if (addressDialog.showAndWait().isEmpty()) return;
        String siteAddress = addressDialog.getResult();

        // Step 2: Contact name
        TextInputDialog contactDialog = new TextInputDialog();
        contactDialog.setHeaderText("Enter new site's contact name:");
        if (contactDialog.showAndWait().isEmpty()) return;
        String siteContact = contactDialog.getResult();

        // Step 3: Phone number
        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setHeaderText("Enter new site's contact's phone number:");
        if (phoneDialog.showAndWait().isEmpty()) return;
        String phoneContact = phoneDialog.getResult();

        // Step 4: Zone
        TextInputDialog zoneDialog = new TextInputDialog();
        zoneDialog.setHeaderText("Enter new site's zone:");
        if (zoneDialog.showAndWait().isEmpty()) return;
        String zone = zoneDialog.getResult();

        // Try adding the site
        try {
            SitesHandler.addSite(siteAddress, siteContact, phoneContact);
            SiteZoneHandler.addSiteToZone(siteAddress, zone);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Site added successfully and mapped to " + zone.toUpperCase());
            success.showAndWait();
        } catch (InstanceAlreadyExistsException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Site Already Exists");
            error.showAndWait();
        } catch (NoSuchElementException n) {
            SitesHandler.deleteSite(siteAddress);
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Zone Not Found");
            error.showAndWait();
        }
    }


    @FXML
    private void onShowRemoveSite() {
        removeSiteBox.setVisible(true);
        removeSiteBox.setManaged(true);
        siteComboBox.getItems().clear();

        List<SiteDTO> sites = SitesHandler.viewAllSites();
        for (SiteDTO site : sites) {
            siteComboBox.getItems().add(site.siteAddress());
        }
    }

    @FXML
    private void onRemoveSite() {
        String selectedAddress = siteComboBox.getValue();

        if (selectedAddress == null || selectedAddress.trim().isEmpty()) {
            Alert emptyAlert = new Alert(Alert.AlertType.WARNING);
            emptyAlert.setTitle("Warning");
            emptyAlert.setHeaderText("No site selected.");
            emptyAlert.showAndWait();
            return;
        }

        try {
            SitesHandler.deleteSite(selectedAddress);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Site removed successfully: " + selectedAddress);
            success.showAndWait();

            siteComboBox.getItems().remove(selectedAddress);
            siteComboBox.setValue(null);

            removeSiteBox.setVisible(false);
            removeSiteBox.setManaged(false);

        } catch (NoSuchElementException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Site not found.");
            error.showAndWait();
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
