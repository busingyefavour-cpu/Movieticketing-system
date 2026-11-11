package com.movieticket;

import java.util.Arrays;
import java.util.UUID;

/** Coordinates movie bookings and keeps them for the current run. */
public class TicketingSystem {
    private final Movie[] movies;
    private final Booking[] bookings;
    private int bookingCount;

    public TicketingSystem(Movie[] movies, int maximumBookings) {
        if (movies == null || movies.length == 0) {
            throw new IllegalArgumentException("At least one movie is required.");
        }
        if (maximumBookings < 1) {
            throw new IllegalArgumentException("Booking capacity must be at least one.");
        }
        for (Movie movie : movies) {
            if (movie == null) {
                throw new IllegalArgumentException("Movie catalogue cannot contain null.");
            }
        }

        this.movies = Arrays.copyOf(movies, movies.length);
        this.bookings = new Booking[maximumBookings];
    }

    public int getMovieCount() {
        return movies.length;
    }

    public Movie getMovieAt(int index) {
        if (index < 0 || index >= movies.length) {
            throw new IndexOutOfBoundsException("Movie index is outside the catalogue.");
        }
        return movies[index];
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public Booking getBookingAt(int index) {
        if (index < 0 || index >= bookingCount) {
            throw new IndexOutOfBoundsException("Booking index is outside the booking list.");
        }
        return bookings[index];
    }

    public Booking bookSeats(Movie movie, Showtime showTime, Seat[] selectedSeats) {
        if (!containsMovie(movie) || showTime == null || !containsShowTime(movie, showTime)) {
            throw new IllegalArgumentException("Select a movie and one of its showtimes.");
        }
        if (selectedSeats == null || selectedSeats.length == 0) {
            throw new IllegalArgumentException("Select at least one seat.");
        }
        if (bookingCount == bookings.length) {
            throw new IllegalStateException("The booking list is full.");
        }

        for (int index = 0; index < selectedSeats.length; index++) {
            Seat seat = selectedSeats[index];
            if (seat == null || !containsSeat(showTime, seat) || seat.isBooked()) {
                throw new IllegalArgumentException("One or more selected seats are unavailable.");
            }
            for (int previous = 0; previous < index; previous++) {
                if (selectedSeats[previous] == seat) {
                    throw new IllegalArgumentException("A seat cannot be selected more than once.");
                }
            }
        }

        String bookingNumber = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking(bookingNumber, movie, showTime, selectedSeats);
        for (Seat seat : selectedSeats) {
            seat.book();
        }
        bookings[bookingCount] = booking;
        bookingCount++;
        return booking;
    }

    private boolean containsMovie(Movie movie) {
        for (Movie catalogueMovie : movies) {
            if (catalogueMovie == movie) {
                return true;
            }
        }
        return false;
    }

    private boolean containsShowTime(Movie movie, Showtime showTime) {
        for (Showtime movieShowTime : movie.getAvailableShowTimes()) {
            if (movieShowTime == showTime) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSeat(Showtime showTime, Seat seat) {
        for (int index = 0; index < showTime.getSeatCount(); index++) {
            if (showTime.getSeatAt(index) == seat) {
                return true;
            }
        }
        return false;
    }
}