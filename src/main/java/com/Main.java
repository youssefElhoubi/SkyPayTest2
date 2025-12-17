package com;

import com.entities.Booking;
import com.entities.Room;
import com.entities.User;
import com.enums.RoomType;
import com.exceptions.InvalidInputException;
import com.service.BookingService;
import com.service.RoomService;
import com.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // 1. Initialize Lists (The "Database")
    private static final List<Room> rooms = new ArrayList<>();
    private static final List<User> users = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    // 2. Initialize Services
    private static final RoomService roomService = new RoomService();
    private static final UserService userService = new UserService();
    private static final BookingService bookingService = new BookingService();

    // Date Formatter for dd/MM/yyyy inputs
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        // --- STEP 1: Run the Required Test Scenario ---
        System.out.println("🚀 STARTING AUTOMATED TEST SCENARIO...\n");
        runTestScenario();
        System.out.println("\n✅ TEST SCENARIO COMPLETE.\n");

        // --- STEP 2: Start Interactive Menu ---
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("👉 Choose an option: ");
            String input = scanner.nextLine();

            try {
                switch (input) {
                    case "1": // Add User
                        System.out.print("Enter Initial Balance: ");
                        int balance = Integer.parseInt(scanner.nextLine());
                        userService.createUser(users, balance);
                        break;

                    case "2": // Add/Update Room
                        System.out.print("Enter Room Number: ");
                        int rNum = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Type (STANDARD_SUITE, JUNIOR_SUITE, MASTER_SUITE): ");
                        RoomType type = RoomType.valueOf(scanner.nextLine().toUpperCase());
                        System.out.print("Enter Price: ");
                        Double price = Double.parseDouble(scanner.nextLine());

                        // Check if exists to decide between create or update (simulating setRoom)
                        if (roomService.findRoom(rooms, rNum).isPresent()) {
                            roomService.updateRoom(rooms, rNum, type, price);
                        } else {
                            roomService.createRoom(rooms, rNum, type, price);
                        }
                        break;

                    case "3": // Book Room
                        handleBookingProcess(scanner);
                        break;

                    case "4": // List Users
                        userService.listAllUsers(users);
                        break;

                    case "5": // List Rooms
                        roomService.listAllRooms(rooms);
                        break;

                    case "6": // List Bookings
                        bookingService.listAllBookings(bookings);
                        break;

                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("❌ Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Invalid number format.");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Error: Invalid enum type or input.");
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Unexpected Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // --- Helper Method: Run the Scenario defined in the prompt ---
    private static void runTestScenario() {
        try {
            // 1. Setup Data (Rooms & Users)
            // Note: Manually adding users to force IDs "1" and "2" as per test requirement
            // (Since userService.createUser generates random UUIDs usually)
            System.out.println("--- 1. Initializing Data ---");

            // Create Rooms (Based on PDF table context)
            roomService.createRoom(rooms, 1, RoomType.STANDARD, 1000.0);
            roomService.createRoom(rooms, 2, RoomType.JUNIOR, 2000.0);
            roomService.createRoom(rooms, 3, RoomType.MASTER, 3000.0);

            // Create Users (Manually to match "ID 1" and "ID 2" requirement)
            users.add(new User("1", 5000));
            users.add(new User("2", 10000));
            System.out.println("✅ Users 1 and 2 created.");

            // 2. Execute Transactions
            System.out.println("\n--- 2. Executing Transactions ---");

            // ● User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights)
            attemptBooking("1", 2, "30/06/2026", "07/07/2026");

            // ● User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (Invalid Date Order)
            attemptBooking("1", 2, "07/07/2026", "30/06/2026");

            // ● User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night)
            attemptBooking("1", 1, "07/07/2026", "08/07/2026");

            // ● User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights - Should fail due to overlap)
            attemptBooking("2", 1, "07/07/2026", "09/07/2026");

            // ● User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night)
            attemptBooking("2", 3, "07/07/2026", "08/07/2026");

            // ● setRoom(1, suite, 10000)
            System.out.println("\n--- 3. Updating Room 1 ---");
            try {
                // Assuming 'suite' refers to MASTER_SUITE or simply changing the type
                roomService.updateRoom(rooms, 1, RoomType.MASTER, 10000.0);
            } catch (Exception e) {
                System.out.println("❌ Update failed: " + e.getMessage());
            }

            // ● Print End Result
            System.out.println("\n--- 4. End Results ---");
            userService.listAllUsers(users);
            roomService.listAllRooms(rooms);
            bookingService.listAllBookings(bookings);

        } catch (Exception e) {
            System.out.println("CRITICAL ERROR IN SCENARIO: " + e.getMessage());
        }
    }

    // --- Helper: Interactive Booking Logic ---
    private static void handleBookingProcess(Scanner scanner) {
        System.out.print("Enter User ID: ");
        String uId = scanner.nextLine();

        System.out.print("Enter Room Number: ");
        int rNum = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Check-in (dd/MM/yyyy): ");
        LocalDate in = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

        System.out.print("Enter Check-out (dd/MM/yyyy): ");
        LocalDate out = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

        // Find entities
        User u = users.stream().filter(user -> user.getId().equals(uId)).findFirst()
                .orElseThrow(() -> new InvalidInputException("User not found"));
        Room r = roomService.findRoom(rooms, rNum)
                .orElseThrow(() -> new InvalidInputException("Room not found"));

        // Execute
        bookingService.createBooking(bookings, u, r, in, out);
    }

    // --- Helper: Shortcut for Scenario Bookings ---
    private static void attemptBooking(String userId, int roomNum, String inDate, String outDate) {
        System.out.print("👉 Attempt: User " + userId + " booking Room " + roomNum + " (" + inDate + " to " + outDate + ")... ");
        try {
            User u = users.stream().filter(user -> user.getId().equals(userId)).findFirst().orElseThrow();
            Room r = roomService.findRoom(rooms, roomNum).orElseThrow();
            LocalDate in = LocalDate.parse(inDate, DATE_FORMATTER);
            LocalDate out = LocalDate.parse(outDate, DATE_FORMATTER);

            bookingService.createBooking(bookings, u, r, in, out);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n════════ HOTEL SYSTEM MENU ════════");
        System.out.println("1. Create User");
        System.out.println("2. Set Room (Create/Update)");
        System.out.println("3. Book a Room");
        System.out.println("4. List All Users");
        System.out.println("5. List All Rooms");
        System.out.println("6. List All Bookings");
        System.out.println("0. Exit");
        System.out.println("═══════════════════════════════════");
    }
}