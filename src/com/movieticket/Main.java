package com.movieticket;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

/** Creates the sample catalogue and starts the application. */
public class Main {
    private static final int BOOKING_CAPACITY = 100;

    private Main() {
    }

    private static Movie[] createCatalogue() {
        LocalDate firstDay = LocalDate.now().plusDays(1);
        LocalDate secondDay = firstDay.plusDays(1);

        Movie firstMovie = new Movie("Black Panther: Wakanda Forever", "Action", 161, 3);
        firstMovie.addShowTime(new Showtime(firstDay + " 18:00", "Room 1", 20000, 20));
        firstMovie.addShowTime(new Showtime(firstDay + " 21:00", "Room 1", 25000, 20));
        firstMovie.addShowTime(new Showtime(secondDay + " 18:00", "Room 1", 20000, 20));

        Movie secondMovie = new Movie("Spider-Man: Beyond the Spider-Verse", "Animation", 140, 1);
        secondMovie.addShowTime(new Showtime(firstDay + " 18:00", "Room 2", 15000, 16));

        return new Movie[]{firstMovie, secondMovie};
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicketingSystem ticketingSystem = new TicketingSystem(createCatalogue(), BOOKING_CAPACITY);
            new BookingFrame(ticketingSystem).setVisible(true);
        });
    }
}