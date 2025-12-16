package com.service;

import com.entities.Room;
import com.enums.RoomType;
import com.exceptions.InvalidInputException;

import java.util.List;
import java.util.Optional;

public class RoomService {

    public Optional<Room> findRoom(List<Room> rooms, int roomNumber) {
        if (rooms == null || rooms.isEmpty()) {
            return Optional.empty();
        }
        return rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst();
    }

    public void createRoom(List<Room> rooms, int roomNumber, RoomType roomType, Double price) {
        // Validation
        if (roomNumber <= 0 || price <= 0 || roomType == null) {
            throw new InvalidInputException("Invalid input: ID and Price must be positive, Type cannot be null.");
        }

        // Check if room already exists
        if (findRoom(rooms, roomNumber).isPresent()) {
            throw new InvalidInputException("Room " + roomNumber + " already exists. Use update instead.");
        }

        // Create and add
        Room newRoom = new Room(roomNumber, roomType, price);
        rooms.add(newRoom);
        System.out.println("✅ Room " + roomNumber + " created.");
    }

    public void updateRoom(List<Room> rooms, int roomNumber, RoomType newType, Double newPrice) {
        // Validation
        if (roomNumber <= 0 || newPrice <= 0 || newType == null) {
            throw new InvalidInputException("Invalid input: ID and Price must be positive, Type cannot be null.");
        }

        Optional<Room> existingRoom = findRoom(rooms, roomNumber);

        if (existingRoom.isPresent()) {
            Room room = existingRoom.get();
            room.setRoomType(newType);
            room.setPrice(newPrice);
            System.out.println("✅ Room " + roomNumber + " updated.");
        } else {
            throw new InvalidInputException("Cannot update: Room " + roomNumber + " does not exist.");
        }
    }
}