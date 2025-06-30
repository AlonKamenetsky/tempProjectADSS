package Transportation.Service;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import Transportation.DTO.TransportationTaskDTO;
import Transportation.Domain.*;
import TransportationSuppliers.Integration.SupplierProvider;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TaskService {
    private final TaskManager taskManager;
    private final SupplierProvider supplierProvider;

    public TaskService() {
        supplierProvider = new SupplierProvider();
        this.taskManager = new TaskManager(new ProductAdapter());
    }

    public TaskService(TaskManager taskManager) {
        this.taskManager = taskManager;
        supplierProvider = new SupplierProvider();
    }

    public TransportationTaskDTO addTask(String _taskDate, String _departureTime, String taskSourceSite) throws ParseException, NoSuchElementException, NullPointerException {
        if (_taskDate == null || _departureTime == null || taskSourceSite == null) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(_taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(_departureTime, timeFormatter);
            return taskManager.addTask(parsedDate, parsedTime, taskSourceSite.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public void deleteTask(String taskDate, String taskDeparture, String taskSourceSite) throws NullPointerException, ParseException, NoSuchElementException {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(taskDeparture, timeFormatter);
            taskManager.removeTask(parsedDate, parsedTime, taskSourceSite.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public void addDocToTask(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite, HashMap<String, Integer> itemsChosen) throws NullPointerException, NoSuchElementException {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null || destinationSite == null || itemsChosen.isEmpty()) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(taskDeparture, timeFormatter);

            HashMap<ProductDTO, Integer> chosenItems = new HashMap<>();

            //turn primitive hashmap to ProductDTO containing hashmap
            for (Map.Entry<String, Integer> entry : itemsChosen.entrySet()) {
                String itemName = entry.getKey();
                int quantity = entry.getValue();
                Optional<ProductDTO> maybeProduct = supplierProvider.getProductByName(itemName);
                if (maybeProduct.isEmpty()) {
                    throw new NoSuchElementException("No such product " + itemName);
                }
                chosenItems.put(maybeProduct.get(), quantity);
            }
            taskManager.addDocToTask(parsedDate, parsedTime, taskSourceSite.toLowerCase(), destinationSite.toLowerCase(), chosenItems);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error", e);
        }
    }

    public TransportationTaskDTO updateWeightForTask(String taskDate, String taskDeparture, String taskSourceSite) {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(taskDeparture, timeFormatter);
            return taskManager.updateWeightForTask(parsedDate, parsedTime, taskSourceSite.toLowerCase());
        }
        catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public boolean assignDriverAndTruckToTask(String taskDate, String taskDeparture, String taskSourceSite) {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(taskDeparture, timeFormatter);

            return taskManager.assignWorkersAndTruckToTask(parsedDate, parsedTime, taskSourceSite.toLowerCase());
        }
        catch (SQLException e) {
            throw new RuntimeException("Database access error" + e.getMessage());
        }
    }

    public String getAllTasksString() {
        try {
            return taskManager.getAllTasksString();
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }


    public String getTasksBySourceAddress(String sourceAddress) {
        try {
            return taskManager.getTasksBySourceAddress(sourceAddress.toLowerCase());
        } catch (SQLException e) {
            if (e.getMessage().isEmpty()) {
                throw new RuntimeException("Database access error");
            }
            else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public boolean hasDestination(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite) throws NullPointerException, NoSuchElementException {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null || destinationSite == null) {
            throw new NullPointerException();
        }
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedDate = LocalDate.parse(taskDate, dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(taskDeparture, timeFormatter);
            return taskManager.hasDestination(parsedDate, parsedTime, taskSourceSite, destinationSite);

        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }
}