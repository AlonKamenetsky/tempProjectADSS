package TransportationSuppliers.Integration;

import Transportation.Service.SiteService;
import Transportation.Service.TaskService;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TransportationProvider implements TransportationInterface {
    private final SiteService siteService;
    private final TaskService taskService;
    private final Scanner input;

    public TransportationProvider() {
        siteService = new SiteService();
        taskService = new TaskService();
        input = new Scanner(System.in);
    }

    @Override
    public void addTransportationAssignment(String departureAddress, String choiceDestinationSite, String contactName, String phoneNumber, String assignmentDate, HashMap<String, Integer> itemsNeeded) throws ParseException {
        try {
            siteService.addSite(departureAddress, contactName, phoneNumber);
        }
        catch (InstanceAlreadyExistsException _) {
        }

        String assignmentTime = "09:00";
        taskService.addTask(assignmentDate, assignmentTime, choiceDestinationSite); // 10:00 time of departure every day for transportations

        //checks if destination site already mapped to this assignment
        try {
            if (taskService.hasDestination(assignmentDate, assignmentTime, departureAddress, choiceDestinationSite)) {
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
        taskService.addDocToTask(assignmentDate, assignmentTime, departureAddress, choiceDestinationSite, itemsNeeded);
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
    public void removesSupplierSite(String supplierAddress) throws InstanceNotFoundException {
        return;
    }

    @Override
    public void updateSupplierSite(String supplierAddress, String contactName, String phoneNumber) throws InstanceNotFoundException {
        return;
    }
}