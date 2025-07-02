package Transportation.Domain;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import Transportation.DTO.*;
import Transportation.Domain.Repositories.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TaskManager {
    private final SiteManager siteManager;
    private final TruckManager truckManager;
    private final DriverManager driverManager;
    private final ItemListManager itemListManager;
    private final TransportationDocRepository docRepository;
    private final TransportationTaskRepository taskRepository;
    private List<WarehouseWorker> warehouseWorkers;
    private final ProductProvider productProvider;

    public TaskManager(ProductProvider productProvider) {
        this.productProvider = productProvider;
        docRepository = new TransportationDocRepositoryImpli();
        taskRepository = new TransportationTaskRepositoryImpli(new SiteRepositoryImpli());
        siteManager = new SiteManager();
        truckManager = new TruckManager();
        driverManager = new DriverManager();
        itemListManager = new ItemListManager();
        initializeWarehouseWorkers();
    }


    public TaskManager(SiteManager siteManager, TruckManager truckManager, DriverManager driverManager, ItemListManager itemListManager, TransportationDocRepository docRepository, TransportationTaskRepository taskRepository, List<WarehouseWorker> warehouseWorkers, ProductProvider productProvider) {
        this.siteManager = siteManager;
        this.truckManager = truckManager;
        this.driverManager = driverManager;
        this.itemListManager = itemListManager;
        this.docRepository = docRepository;
        this.taskRepository = taskRepository;
        this.warehouseWorkers = warehouseWorkers;
        this.productProvider = productProvider;
    }

    public TransportationTaskDTO addTask(LocalDate _taskDate, LocalTime _departureTime, String taskSourceSite) throws ParseException, SQLException {
        Optional<SiteDTO> site = siteManager.findSiteByAddress(taskSourceSite);
        if (site.isEmpty()) {
            throw new NoSuchElementException();
        }
        return taskRepository.createTask(_taskDate, _departureTime, taskSourceSite);
    }

    public void removeTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException {
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isPresent()) {
            // free truck and driver
            if (!task.get().truckLicenseNumber().isEmpty() && !task.get().driverId().isEmpty()) {
                truckManager.setTruckAvailability(truckManager.getTruckIdByLicense(task.get().truckLicenseNumber()), true);
                driverManager.setDriverAvailability(task.get().driverId(), true);
            }

            taskRepository.deleteTask(task.get().taskId());
        }
    }


    public void addDocToTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite,
                             String destinationSite, HashMap<ProductDTO, Integer> itemsChosen) throws SQLException {
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isPresent()) {
            //Create itemList and push it to database
            int listId = itemListManager.makeList(itemsChosen);
            //Create doc (creating mapping between list and destination) and push it to database
            int destinationSiteId = siteManager.findSiteByAddress(destinationSite).get().siteId();
            docRepository.createDoc(task.get().taskId(), destinationSiteId, listId);
            //Phase add doc to task and add mapping to database
            taskRepository.addDestination(task.get().taskId(), destinationSiteId);
        }
    }


    public TransportationTaskDTO updateWeightForTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException, NoSuchElementException {
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        float weight = 0;
        if (task.isPresent()) {
            List<TransportationDocDTO> taskDocs = docRepository.findDocByTaskId(task.get().taskId());
            for (TransportationDocDTO doc : taskDocs) {
                int itemsListId = docRepository.findDocItemsListId(doc.docId());
                weight += itemListManager.findWeightList(itemsListId);
            }
            return taskRepository.updateWeight(task.get().taskId(), weight);
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean assignWorkersAndTruckToTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException {
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isPresent()) {
            Optional<TruckDTO> nextAvailableTruck = truckManager.getNextTruckAvailable(task.get().weightBeforeLeaving());
            if (nextAvailableTruck.isEmpty()) {
                throw new NoSuchElementException("No truck available for this task");
            }

            String licenseTypeNeeded = LicenseMapper.getRequiredLicense(TruckType.fromString(nextAvailableTruck.get().truckType())).toString();

            Optional<DriverDTO> availableDriver = driverManager.getAvailableDriverByLicense(licenseTypeNeeded);
            if (availableDriver.isEmpty()) {
                throw new NoSuchElementException("No drivers available for this task");
            }

            // assign warehouse worker
            WarehouseWorker availableWarehouseWorker = null;
            for (WarehouseWorker w : warehouseWorkers) {
                if (w.isAvailable()) {
                    w.setAvailable(false);
                    availableWarehouseWorker = w;
                    break;
                }
            }
           if (availableWarehouseWorker == null) {
                throw new NoSuchElementException("No warehouse workers available for this task");
           }

            // All good → assign
            taskRepository.assignTruckToTask(task.get().taskId(), nextAvailableTruck.get().licenseNumber());
            truckManager.setTruckAvailability(nextAvailableTruck.get().truckId(), false);
            taskRepository.assignDriverToTask(task.get().taskId(), availableDriver.get().driverId());
            driverManager.setDriverAvailability(task.get().driverId(), false);
            taskRepository.assignWarehouseWorkerToTask(task.get().taskId(), availableWarehouseWorker.getHwId());



            return true;
        } else {
            throw new NoSuchElementException("This task isn't available");
        }
    }

    public String getTaskString(TransportationTaskDTO t, int counter) throws SQLException {
        StringBuilder sb = new StringBuilder("Transportation Task\n");
        sb.append(counter).append(". Transportation Task\n");
        sb.append("Source Site: ").append(t.sourceSiteAddress()).append("\n");
        sb.append("Departure Date: ").append(t.taskDate()).append("\n");
        sb.append("Departure Time: ").append(t.departureTime()).append("\n");
        sb.append("Driver Assigned: ").append(t.driverId()).append("\n");
        sb.append("Warehouse Worker Assigned: ").append(t.whwId()).append("\n");
        sb.append("Truck Assigned: ").append(t.truckLicenseNumber()).append("\n");
        sb.append("Weight Before Leaving: ").append(t.weightBeforeLeaving()).append(" kg\n");
        sb.append("Destinations:\n");

        for (TransportationDocDTO doc : docRepository.findDocByTaskId(t.taskId())) {
            String destinationAddress = siteManager.getSiteById(doc.destinationSite())
                    .orElseThrow(() -> new SQLException("Destination site not found")).siteAddress();
            sb.append("  - Destination: ").append(destinationAddress).append("\n");

            ItemsListDTO list = itemListManager.getItemsList(docRepository.findDocItemsListId(doc.docId()));
            for (Map.Entry<Integer, Integer> entry : list.items().entrySet()) {
                String itemName = productProvider.getItemById(entry.getKey());
                int quantity = entry.getValue();

                sb.append("    • ").append(itemName).append(" — Quantity: ").append(quantity).append("\n");
            }
        }

        sb.append("----------------------\n");
        return sb.toString();
    }

    public String getAllTasksString() throws SQLException {
        List<TransportationTaskDTO> allTasks = taskRepository.getAllTasks();
        int counter = 1;
        StringBuilder sb = new StringBuilder("All Tasks:\n");

        for (TransportationTaskDTO t : allTasks) {
            sb.append(getTaskString(t, counter));
            counter++;
        }

        return sb.toString();
    }

    public String getTasksBySourceAddress(String sourceAddress) throws SQLException {
        int counter = 1;
        List<TransportationTaskDTO> tasks = taskRepository.findTaskBySourceAddress(sourceAddress);
        StringBuilder sb = new StringBuilder();

        for (TransportationTaskDTO t : tasks) {
            sb.append(getTaskString(t, counter));
            counter++;
        }

        return sb.toString();
    }

    public boolean hasDestination(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite, String destinationSite) throws NoSuchElementException, SQLException {
        Optional<SiteDTO> site = siteManager.findSiteByAddress(destinationSite);
        if (site.isEmpty()) {
            throw new NoSuchElementException();
        }
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isEmpty()) {
            throw new NoSuchElementException();
        }

        int taskId = task.get().taskId();
        return taskRepository.hasDestination(taskId, site.get().siteId());
    }

    private void initializeWarehouseWorkers() {
        this.warehouseWorkers = new ArrayList<>();
        warehouseWorkers.add(new WarehouseWorker("1", "avi"));
        warehouseWorkers.add(new WarehouseWorker("2", "shlomi"));
        warehouseWorkers.add(new WarehouseWorker("3", "keren"));
        warehouseWorkers.add(new WarehouseWorker("4", "hadar"));
        warehouseWorkers.add(new WarehouseWorker("5", "yael"));
        warehouseWorkers.add(new WarehouseWorker("6", "ido"));
        warehouseWorkers.add(new WarehouseWorker("7", "ben"));
        warehouseWorkers.add(new WarehouseWorker("8", "shalom"));
        warehouseWorkers.add(new WarehouseWorker("9", "jacob"));
        warehouseWorkers.add(new WarehouseWorker("10", "john"));
    }
}