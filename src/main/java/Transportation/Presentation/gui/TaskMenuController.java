package Transportation.Presentation.gui;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.ServiceLayer.ProductService;
import Transportation.DTO.SiteDTO;
import Transportation.DTO.TransportationTaskDTO;
import Transportation.Service.ProductAdapter;
import Transportation.Service.SiteService;
import Transportation.Service.TaskService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TaskMenuController {

    private ProductAdapter productAdapter;
    private final TaskService tasksHandler = new TaskService();
    private final SiteService siteHandler = new SiteService();
    private final ProductAdapter productHandler = new ProductAdapter();

    @FXML private Button viewAllTasksButton;
    @FXML private Button viewBySourceSiteButton;
    @FXML private Button addTaskButton;
    @FXML private Button removeTaskButton;
    @FXML private VBox removeTaskBox;
    @FXML private ComboBox<String> taskComboBox;
    @FXML private Button backButton;
    @FXML private TextArea outputTextArea;

    @FXML
    public void initialize() {
        removeTaskBox.setVisible(false);
        removeTaskBox.setManaged(false);
    }

    @FXML
    public void onViewAllTasks() {
        String allTasks = tasksHandler.getAllTasksString();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, allTasks);
        alert.setTitle("Task Menu");
        alert.setHeaderText("All Tasks:");
        alert.showAndWait();
    }

    @FXML
    private void onViewTaskBySource() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter source site address:");
        dialog.showAndWait().ifPresent(site -> {
            String output = tasksHandler.getTasksBySourceAddress(site);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, output);
            alert.setTitle("Tasks");
            alert.setHeaderText("Tasks for site: " + site);
            alert.showAndWait();
        });
    }

    @FXML
    private void onAddTask() {
        boolean destinationAdded = false;
        String taskDate, taskDeparture, taskSourceSite;
        HashMap<String, Integer> itemsChosen = new HashMap<>();

        // Step 1: Basic Task Info
        taskDate = getInput("Add Task", "Enter task date (dd/MM/yyyy):");
        if (taskDate == null) return;

        taskDeparture = getInput("Add Task", "Enter departure time (HH:mm):");
        if (taskDeparture == null) return;

        taskSourceSite = getInput("Add Task", "Enter source site address:");
        if (taskSourceSite == null) return;

        // Try to create base task
        try {
            tasksHandler.addTask(taskDate, taskDeparture, taskSourceSite);
            showInfo("Success", "Task created without destinations.");
        } catch (Exception e) {
            showError("Task Creation Failed", e.getMessage());
            return;
        }

        // Step 2: Destination Sites
        while (true) {
            String destinationSite = getInput("Destination", "Enter destination site (at least one):");
            if (destinationSite == null) break;

            try {
                if (tasksHandler.hasDestination(taskDate, taskDeparture, taskSourceSite, destinationSite)) {
                    showWarning("Duplicate", "This destination is already assigned to the task.");
                    continue;
                }
            } catch (Exception e) {
                showError("Invalid Site", e.getMessage());
                continue;
            }

            String option = getInput("Destination Options", """
                1. Add items to this destination
                2. Choose another destination site
                3. Return to Task Menu
                """);

            if (option == null || option.equals("3")) return;

            if (option.equals("1")) {
                while (true) {
                    List<ProductDTO> allProducts = productHandler.viewAllProducts();
                    StringBuilder allItemsBuilder = new StringBuilder("Available Items:\n");
                    int i = 1;
                    for (ProductDTO product : allProducts) {
                        allItemsBuilder.append(i++)
                                .append(". Name: ").append(product.productName())
                                .append(" | Weight: ").append(product.productWeight()).append("\n");
                    }
                    String allItems = allItemsBuilder.toString();

                    showInfoItems("Available Items", allItems);

                    String itemName = getInput("Add Item", "Enter item name:");
                    if (itemName == null) {
                        // User canceled — exit the item-adding loop
                        break;
                    }
                    if (!productHandler.doesItemExist(itemName)) {
                        showWarning("Invalid Item", "Item does not exist.");
                        continue;
                    }

                    String quantityStr = getInput("Quantity", "Enter quantity for " + itemName + ":");
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        itemsChosen.put(itemName, quantity);
                    } catch (NumberFormatException e) {
                        showWarning("Invalid Input", "Quantity must be a number.");
                        continue;
                    }

                    String done = getInput("Done?", "Are you done adding items to this destination? (Yes/No)");
                    if (done != null && done.equalsIgnoreCase("yes")) {
                        tasksHandler.addDocToTask(taskDate, taskDeparture, taskSourceSite, destinationSite, itemsChosen);
                        destinationAdded = true;
                        break;
                    }
                }
            }


            if (!option.equals("2")) {
                tasksHandler.updateWeightForTask(taskDate, taskDeparture, taskSourceSite);
                showInfo("Destination Added", "Destination site added successfully.");
                itemsChosen.clear();

                String more = getInput("Add More?", "Add more destinations? (Yes/Anything for no)");
                if (more == null || !more.equalsIgnoreCase("yes")) break;
            }
        }

        // If no destination was added, cancel task and alert
        try {
            if (!destinationAdded) {
                tasksHandler.deleteTask(taskDate, taskDeparture, taskSourceSite);
                showError("No Destinations", "You must add at least one destination. Task was canceled.");
                return;
            }
        }
        catch (Exception e) {
            showError("Task Deletion Failed", e.getMessage());
        }

        // Final: Assign driver & truck
        try {
            boolean assigned = tasksHandler.assignDriverAndTruckToTask(taskDate, taskDeparture, taskSourceSite);
            if (!assigned) {
                showError("Assignment Failed", """
                    No drivers or trucks available for this task.
                    The task was deleted.
                    """);
                tasksHandler.deleteTask(taskDate, taskDeparture, taskSourceSite);
                return;
            }

            showInfo("Task Added", "Task added successfully with assigned driver and truck.");

        } catch (Exception e) {
            showError("Finalization Failed", e.getMessage());
            try {
                tasksHandler.deleteTask(taskDate, taskDeparture, taskSourceSite);
            } catch (Exception ex) {
                showError("Cleanup Failed", ex.getMessage());
            }
        }
    }


    @FXML
    private void onShowRemoveTask() {
        // Just show the tasks and call remove flow directly
        String allTasks = tasksHandler.getAllTasksString();

        Alert listAlert = new Alert(Alert.AlertType.INFORMATION);
        listAlert.setTitle("Existing Tasks");
        listAlert.setHeaderText("List of All Tasks");
        listAlert.setContentText(allTasks);
        listAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        listAlert.showAndWait();

        // Trigger manual remove
        onRemoveTask();
    }

    @FXML
    private void onRemoveTask() {
        TextInputDialog dateDialog = new TextInputDialog();
        dateDialog.setTitle("Remove Task");
        dateDialog.setHeaderText("Enter task date (dd/MM/yyyy):");
        if (dateDialog.showAndWait().isEmpty()) return;
        String taskDate = dateDialog.getResult();

        TextInputDialog timeDialog = new TextInputDialog();
        timeDialog.setHeaderText("Enter departure time (HH:mm):");
        if (timeDialog.showAndWait().isEmpty()) return;
        String departureTime = timeDialog.getResult();

        TextInputDialog siteDialog = new TextInputDialog();
        siteDialog.setHeaderText("Enter source site:");
        if (siteDialog.showAndWait().isEmpty()) return;
        String sourceSite = siteDialog.getResult();

        try {
            tasksHandler.deleteTask(taskDate, departureTime, sourceSite);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Task removed successfully");
            success.setContentText("Date: " + taskDate + "\nTime: " + departureTime + "\nSite: " + sourceSite);
            success.showAndWait();
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Could not remove task");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    @FXML
    public void onBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransportationMenu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    // helper methods
    private String getInput(String title, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(prompt);
        var result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showInfoItems(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
