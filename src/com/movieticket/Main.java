package com.movieticket;

import javax.swing.SwingUtilities;

/** Creates the sample catalogue and starts the application. */
public class Main {
    private static final int BOOKING_CAPACITY = 100;

    private Main() {
    }

    private static Movie[] createCatalogue() {
        Movie firstMovie = new Movie("Black Panther: Wakanda Forever", "Action", 161, 2);
        firstMovie.addShowTime(new ShowTime("2026-11-10 18:00", 20000, 20));
        firstMovie.addShowTime(new ShowTime("2026-11-10 21:00", 25000, 20));

        Movie secondMovie = new Movie("Spider-Man: Beyond the Spider-Verse", "Animation", 140, 1);
        secondMovie.addShowTime(new ShowTime("2026-11-11 15:00", 15000, 16));

        return new Movie[]{firstMovie, secondMovie};
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicketingSystem ticketingSystem = new TicketingSystem(createCatalogue(), BOOKING_CAPACITY);
            new BookingFrame(ticketingSystem).setVisible(true);
        });
    }
}