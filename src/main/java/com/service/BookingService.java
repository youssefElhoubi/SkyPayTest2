package com.service;

import com.entities.Booking;
import com.entities.Room;
import com.entities.User;
import com.exceptions.InvalidInputException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BookingService {


    public void createBooking(List<Booking> bookings, User user, Room room, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || user == null || room == null) {
            throw new InvalidInputException("Invalid input: User, Room, and Dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidInputException("Check-out date must be after check-in date.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);


        int pricePerNight = room.getPrice().intValue();
        int totalCost = (int) nights * pricePerNight;

        if (user.getBalance() < totalCost) {
            throw new InvalidInputException("Insufficient balance. Cost: " + totalCost + ", Balance: " + user.getBalance());
        }

        boolean isOccupied = bookings.stream()
                .filter(b -> b.getRoomNumber().getRoomNumber() == room.getRoomNumber()) // Check same room
                .anyMatch(b ->
                        checkIn.isBefore(b.getCheckOut()) && checkOut.isAfter(b.getCheckIn())
                );

        if (isOccupied) {
            throw new InvalidInputException("Room " + room.getRoomNumber() + " is already booked for these dates.");
        }

        user.setBalance(user.getBalance() - totalCost);

        int newId = bookings.stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;

        Booking newBooking = new Booking(
                newId,
                user,
                room,
                checkIn,
                checkOut,
                (double) pricePerNight, // Storing snapshot of price
                totalCost
        );

        bookings.add(newBooking);
        System.out.println("✅ Booking successful! Cost: " + totalCost + ". New User Balance: " + user.getBalance());
    }
    public Optional<Booking> findBooking(List<Booking> bookings, int bookingId) {
        if (bookings == null || bookings.isEmpty()) return Optional.empty();
        return bookings.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst();
    }

    public void updateBookingDates(List<Booking> bookings, int bookingId, LocalDate newCheckIn, LocalDate newCheckOut) {
        Booking booking = findBooking(bookings, bookingId).orElseThrow(() ->
                new InvalidInputException("Booking ID " + bookingId + " not found."));

        bookings.remove(booking);

        try {
            boolean isOccupied = bookings.stream()
                    .filter(b -> b.getRoomNumber().getRoomNumber() == booking.getRoomNumber().getRoomNumber())
                    .anyMatch(b -> newCheckIn.isBefore(b.getCheckOut()) && newCheckOut.isAfter(b.getCheckIn()));

            if (isOccupied) {
                throw new InvalidInputException("Cannot update: Room is occupied on new dates.");
            }

            booking.setCheckIn(newCheckIn);
            booking.setCheckOut(newCheckOut);

            bookings.add(booking);
            System.out.println("✅ Booking " + bookingId + " updated.");

        } catch (Exception e) {
            bookings.add(booking);
            throw e;
        }
    }

    public void listAllBookings(List<Booking> bookings) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           BOOKING HISTORY                              ║");
        System.out.println("╠══════╤════════════════╤══════╤════════════╤════════════╤═══════════════╣");
        System.out.println("║  ID  │    USER ID     │ ROOM │  CHECK-IN  │ CHECK-OUT  │   COST ($)    ║");
        System.out.println("╠══════╪════════════════╪══════╪════════════╪════════════╪═══════════════╣");

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("║                     No bookings records found.                         ║");
        } else {
            // Sort by ID descending (Latest created first)
            bookings.stream()
                    .sorted(Comparator.comparingInt(Booking::getBookingId).reversed())
                    .forEach(b -> {
                        System.out.printf("║ %-4d │ %-14s │ %-4d │ %-10s │ %-10s │ %13d ║%n",
                                b.getBookingId(),
                                // Truncate User ID for display if it's a long UUID
                                (b.getUser().getId().length() > 14) ? b.getUser().getId().substring(0, 14) : b.getUser().getId(),
                                b.getRoomNumber().getRoomNumber(),
                                b.getCheckIn(),
                                b.getCheckOut(),
                                b.getTotalCost());
                    });
        }
        System.out.println("╚══════╧════════════════╧══════╧════════════╧════════════╧═══════════════╝");
    }
}