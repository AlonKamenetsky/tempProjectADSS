package TransportationSuppliers.Integration;

import Transportation.Service.SiteService;
import Transportation.Service.TaskService;

import javax.management.InstanceAlreadyExistsException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class TransportationProvider implements TransportationInterface {
    private final SiteService siteService;
    private final TaskService taskService;

    TransportationProvider() {
        siteService = new SiteService();
        taskService = new TaskService();
    }

    @Override
    public void addTransportationAssignment(String departureAddress, String destinationAddress, String assignmentDate, HashMap<String, Integer> itemsNeeded) throws ParseException {
        String assignmentTime = "09:00";
        taskService.addTask(assignmentDate, assignmentTime, departureAddress); // 10:00 time of departure every day for transportations

        //checks if destination site already mapped to this assignment
        try {
            if (taskService.hasDestination(assignmentDate, assignmentTime, departureAddress, destinationAddress)) {
                System.out.println("Task already has this destination.");
                return;
            }
        } catch (NullPointerException e) {
            System.out.println("Not a valid site.");
        } catch (NoSuchElementException e) {
            System.out.println("Site doesn't exist.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        // add transportation document to this assignment
        taskService.addDocToTask(assignmentDate, assignmentTime, departureAddress, destinationAddress, itemsNeeded);
        taskService.updateWeightForTask(assignmentDate, assignmentTime, departureAddress);
        try {
            if (!taskService.assignDriverAndTruckToTask(assignmentDate, assignmentTime, departureAddress)) {
                System.out.println("""
                        Adding task not successful, no drivers or trucks available for this task right now.
                        Task is deleted for now. Thank you and sorry!""");

                taskService.deleteTask(assignmentDate, assignmentTime, departureAddress);
                return;
            }
            System.out.println("Added task successfully.");
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            try {
                taskService.deleteTask(assignmentDate, assignmentTime, departureAddress);
            } catch (Exception ex) {
                System.out.println("Failed to delete task: " + ex.getMessage());
            }

        } catch (NoSuchElementException n) {
            System.out.println("""
                    Adding task not successful:
                    """ + n.getMessage() + """
                    Task is deleted for now. Thank you and sorry!""");
            try {
                taskService.deleteTask(assignmentDate, assignmentTime, departureAddress);
            } catch (Exception ex) {
                System.out.println("Failed to delete task: " + ex.getMessage());
            }
        }
    }

    @Override
    public void addSupplierSite(String supplierAddress, String contactName, String phoneNumber) throws InstanceAlreadyExistsException {
        try {
            siteService.addSite(supplierAddress, contactName, phoneNumber);
        }
        catch (Exception e) {
            throw new InstanceAlreadyExistsException("Supplier site already exists");
        }
    }
}