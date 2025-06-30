package Transportation.Presentation.gui;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.Service.SiteZoneService;
import Transportation.Service.ZoneService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class ZoneMenuController {

    private final ZoneService zonesHandler = new ZoneService();
    private final SiteZoneService siteZoneHandler = new SiteZoneService();

    @FXML private Button viewAllZonesButton;
    @FXML private Button viewSitesByZoneButton;
    @FXML private Button addZoneButton;
    @FXML private Button showModifyZoneButton;
    @FXML private VBox modifyZoneBox;
    @FXML private ComboBox<String> zoneComboBox;
    @FXML private Button showRemoveZoneButton;
    @FXML private VBox removeZoneBox;
    @FXML private ComboBox<String> removeZoneComboBox;
    @FXML private Button backButton;

    @FXML
    private void onViewAllZones() {
        try {
            List<ZoneDTO> allZones = zonesHandler.viewAllZones();
            StringBuilder sb = new StringBuilder("All Zones:\n");
            for (ZoneDTO zone : allZones) {
                ZoneDTO populated = siteZoneHandler.getZoneWithSites(zone.zoneName());
                sb.append("Zone: ").append(populated.zoneName()).append("\nSites Mapped:\n");
                int i = 1;
                for (String site : populated.sitesRelated()) {
                    sb.append(" ").append(i++).append(". ").append(site).append("\n");
                }
                sb.append("----------------------\n");
            }
            showAlert("Zones", sb.toString());
        } catch (NoSuchElementException e) {
            showAlert("Error", "No zones found.");
        }
    }

    @FXML
    private void onViewSitesByZone() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter Zone Name:");
        dialog.showAndWait();
        String zoneName = dialog.getResult();
        if (zoneName != null) {
            try {
                List<SiteDTO> sites = siteZoneHandler.getSitesByZone(zoneName);
                StringBuilder sb = new StringBuilder("Sites in " + zoneName + ":\n");
                for (SiteDTO site : sites) {
                    sb.append("Site: ").append(site.siteAddress()).append("\n");
                }
                showAlert("Sites By Zone", sb.toString());
            } catch (NoSuchElementException e) {
                showAlert("Error", "Zone does not exist.");
            }
        }
    }

    @FXML
    private void onAddZone() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter New Zone Name:");
        dialog.showAndWait();
        String zoneName = dialog.getResult();
        if (zoneName != null) {
            try {
                zonesHandler.AddZone(zoneName);
                showAlert("Success", "Zone added successfully.");
            } catch (InstanceAlreadyExistsException e) {
                showAlert("Error", "Zone already exists.");
            }
        }
    }

    @FXML
    private void onShowModifyZone() {
        modifyZoneBox.setVisible(true);
        zoneComboBox.getItems().clear();
        zonesHandler.viewAllZones().forEach(z -> zoneComboBox.getItems().add(z.zoneName()));
    }

    @FXML
    private void onShowRemoveZone() {
        removeZoneBox.setVisible(true);
        removeZoneComboBox.getItems().clear();
        zonesHandler.viewAllZones().forEach(z -> removeZoneComboBox.getItems().add(z.zoneName()));
    }

    @FXML
    private void onChangeZoneName() {
        String oldName = zoneComboBox.getValue();
        if (oldName == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter new name:");
        dialog.showAndWait();
        String newName = dialog.getResult();
        if (newName != null) {
            zonesHandler.UpdateZone(oldName, newName);
            showAlert("Success", "Zone name updated.");
        }
    }

    @FXML
    private void onMapSiteToZone() {
        String zoneName = zoneComboBox.getValue();
        if (zoneName == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter Site Address:");
        dialog.showAndWait();
        String address = dialog.getResult();
        if (address != null) {
            siteZoneHandler.addSiteToZone(address, zoneName);
            showAlert("Success", "Site mapped.");
        }
    }

    @FXML
    private void onRemoveSiteMapping() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter Site Address:");
        dialog.showAndWait();
        String address = dialog.getResult();
        if (address != null) {
            siteZoneHandler.removeSiteFromZone(address);
            showAlert("Success", "Site mapping removed.");
        }
    }

    @FXML
    private void onRemoveZone() {
        String zoneName = removeZoneComboBox.getValue();
        if (zoneName == null) return;
        zonesHandler.deleteZone(zoneName);
        showAlert("Success", "Zone removed.");
        removeZoneComboBox.getItems().remove(zoneName);
    }

    @FXML
    private void onBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transportation/Presentation/gui/TransportationMenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
