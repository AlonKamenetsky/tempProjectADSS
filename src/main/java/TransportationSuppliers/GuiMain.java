package TransportationSuppliers;

import TransportationSuppliers.data.Util.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class GuiMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        showCustomDialog("SuperLee", "Welcome to SuperLee", "The logistics system is ready to use!");

        boolean loadFullData = askYesNo("SuperLee", "Load Data", "Do you want to load the system with FULL data?");

        if (loadFullData) {
            DatabaseInitializer db = new DatabaseInitializer();
            db.loadFullDataTransportation();
            System.out.println("FULL DATA loaded.");
        } else {
            System.out.println("Basic data only.");
        }

        String menuChoice = askMenuChoice("SuperLee", "Select Menu", "Which menu do you want to open?");

        String fxmlPath = "";
        if ("Transportation".equals(menuChoice)) {
            fxmlPath = "/fxml/TransportationMenu.fxml";
        } else if ("Supplier".equals(menuChoice)) {
            fxmlPath = "/fxml/SupplierMenu.fxml";
        } else {
            System.out.println("No menu selected. Exiting.");
            System.exit(0);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 600, 400);
        primaryStage.setTitle("SuperLee Logistics System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showCustomDialog(String title, String header, String message) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialogStage.close());

        VBox vbox = new VBox(15, headerLabel, messageLabel, okButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 200);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private boolean askYesNo(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesBtn;
    }

    private String askMenuChoice(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        ButtonType transBtn = new ButtonType("Transportation");
        ButtonType supplierBtn = new ButtonType("Supplier");

        alert.getButtonTypes().setAll(transBtn, supplierBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == transBtn) {
                return "Transportation";
            } else if (result.get() == supplierBtn) {
                return "Supplier";
            }
        }
        return null;
    }
}
