package com.movieticket;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Represents one screening and the seats available for that screening. */
public class Showtime {
    private final String dateTime;
    private final LocalDate date;
    private final String roomName;
    private final int ticketPrice;
    private final Seat[] seats;

    public Showtime(String dateTime, int ticketPrice, int numberOfSeats) {
        this(dateTime, "Room 1", ticketPrice, numberOfSeats);
    }

    public Showtime(String dateTime, String roomName, int ticketPrice, int numberOfSeats) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException("Showtime cannot be blank.");
        }
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be blank.");
        }
        if (dateTime.length() < 10) {
            throw new IllegalArgumentException("Showtime must start with a date in yyyy-MM-dd format.");
        }
        try {
            this.date = LocalDate.parse(dateTime.substring(0, 10));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Showtime must start with a valid date in yyyy-MM-dd format.", exception);
        }
        if (ticketPrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        if (numberOfSeats < 1) {
            throw new IllegalArgumentException("A showtime must have at least one seat.");
        }

        this.dateTime = dateTime;
        this.roomName = roomName;
        this.ticketPrice = ticketPrice;
        this.seats = new Seat[numberOfSeats];

        for (int index = 0; index < seats.length; index++) {
            seats[index] = new Seat("S" + (index + 1));
        }
    }

    public String getDateTime() {
        return dateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public int getTotalPrice(int seatCount) {
        if (seatCount < 1) {
            throw new IllegalArgumentException("Seat count must be at least one.");
        }
        return ticketPrice * seatCount;
    }

    public int getSeatCount() {
        return seats.length;
    }

    public Seat getSeatAt(int index) {
        if (index < 0 || index >= seats.length) {
            throw new IndexOutOfBoundsException("Seat number is outside this showtime.");
        }
        return seats[index];
    }

    public Seat[] getAllSeats() {
        return seats;
    }

    public int getAvailableSeatCount() {
        int available = 0;
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                available++;
            }
        }
        return available;
    }

    @Override
    public String toString() {
        return dateTime + " - " + roomName + " - Ugx " + ticketPrice;
    }
}
