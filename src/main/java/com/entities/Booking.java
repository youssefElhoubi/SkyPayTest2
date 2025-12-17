package com.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private  int bookingId;
    private  User user;
    private  Room roomNumber;
    private  LocalDate checkIn;
    private  LocalDate checkOut;
    private  Double bookedPricePerNight;
    private  int totalCost;
}
