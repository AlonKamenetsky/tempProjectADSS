//package SuppliersModule.PresentationLayer;
//
//import java.util.Scanner;
//
//public class SupplierMain {
//    public static void main(String[] args) {
//        Database.initializeSchema();
//        Scanner scanner = new Scanner(System.in);
//        SupplierCLI cli = new SupplierCLI();
//        while (true) {
//            System.out.println("do you want to load suppliers menu?");
//            System.out.println("1. Load suppliers menu");
//            System.out.println("2. Exit");
//            int x = scanner.nextInt();
//            switch (x) {
//                case 1:
//                    cli.mainCliMenu();
//                    break;
//                case 2:
//                    System.exit(0);
//            }
//        }
//
//
//    }
//}
