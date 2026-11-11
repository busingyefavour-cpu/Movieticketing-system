package com.movieticket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Displays the movie selection, seat map, and booking summary. */
public class BookingFrame extends JFrame {
    private final TicketingSystem ticketingSystem;
    private final JComboBox<Movie> movieDropdown;
    private final JComboBox<Showtime> showtimeDropdown;
    private final JPanel seatGridPanel;
    private final JButton bookButton;
    private final JLabel statusLabel;
    private final List<Seat> selectedSeats;

    public BookingFrame(TicketingSystem ticketingSystem) {
        super("Movie Ticket Booking System");
        this.ticketingSystem = ticketingSystem;
        this.movieDropdown = new JComboBox<>();
        this.showtimeDropdown = new JComboBox<>();
        this.seatGridPanel = new JPanel();
        this.bookButton = new JButton("Book Selected Seats");
        this.statusLabel = new JLabel("Please select a movie and showtime to begin.");
        this.selectedSeats = new ArrayList<>();

        setupUI();
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Movie:"));
        topPanel.add(movieDropdown);
        topPanel.add(new JLabel("Select Showtime:"));
        topPanel.add(showtimeDropdown);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(seatGridPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bookButton.setEnabled(false);
        bookButton.addActionListener(event -> bookSelectedSeats());
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(bookButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        for (int index = 0; index < ticketingSystem.getMovieCount(); index++) {
            movieDropdown.addItem(ticketingSystem.getMovieAt(index));
        }
        movieDropdown.addActionListener(event -> updateShowtimes());
        showtimeDropdown.addActionListener(event -> renderSeats());
        updateShowtimes();
    }

    private void updateShowtimes() {
        Movie selectedMovie = (Movie) movieDropdown.getSelectedItem();
        showtimeDropdown.removeAllItems();
        if (selectedMovie != null) {
            for (Showtime showTime : selectedMovie.getAvailableShowTimes()) {
                showtimeDropdown.addItem(showTime);
            }
        }
        renderSeats();
    }

    private void renderSeats() {
        seatGridPanel.removeAll();
        selectedSeats.clear();
        bookButton.setEnabled(false);

        Showtime selectedShowtime = (Showtime) showtimeDropdown.getSelectedItem();
        if (selectedShowtime != null) {
            int columns = 4;
            int rows = (int) Math.ceil((double) selectedShowtime.getSeatCount() / columns);
            seatGridPanel.setLayout(new GridLayout(rows, columns, 5, 5));
            seatGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (int index = 0; index < selectedShowtime.getSeatCount(); index++) {
                Seat seat = selectedShowtime.getSeatAt(index);
                JToggleButton seatButton = new JToggleButton(seat.getSeatNumber());
                if (seat.isBooked()) {
                    seatButton.setEnabled(false);
                    seatButton.setText(seat.getSeatNumber() + " (X)");
                    seatButton.setBackground(Color.RED);
                } else {
                    seatButton.setBackground(Color.GREEN);
                    seatButton.addActionListener(event -> {
                        if (seatButton.isSelected()) {
                            selectedSeats.add(seat);
                        } else {
                            selectedSeats.remove(seat);
                        }
                        updateStatus();
                    });
                }
                seatGridPanel.add(seatButton);
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
        updateStatus();
    }

    private void updateStatus() {
        Showtime selectedShowtime = (Showtime) showtimeDropdown.getSelectedItem();
        if (selectedShowtime == null) {
            statusLabel.setText("Please select a movie and showtime to begin.");
            bookButton.setEnabled(false);
        } else if (selectedSeats.isEmpty()) {
            statusLabel.setText("0 seats selected.");
            bookButton.setEnabled(false);
        } else {
            int total = selectedShowtime.getTotalPrice(selectedSeats.size());
            statusLabel.setText(selectedSeats.size() + " seat(s) selected. Total: Ugx " + total);
            bookButton.setEnabled(true);
        }
    }

    private void bookSelectedSeats() {
        Movie movie = (Movie) movieDropdown.getSelectedItem();
        Showtime showTime = (Showtime) showtimeDropdown.getSelectedItem();
        Seat[] seats = selectedSeats.toArray(new Seat[0]);

        try {
            Booking booking = ticketingSystem.bookSeats(movie, showTime, seats);
            showBookingSummary(booking);
            renderSeats();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookingSummary(Booking booking) {
        StringBuilder seatNumbers = new StringBuilder();
        for (int index = 0; index < booking.getSeatCount(); index++) {
            if (index > 0) {
                seatNumbers.append(", ");
            }
            seatNumbers.append(booking.getSeatAt(index).getSeatNumber());
        }

        JOptionPane.showMessageDialog(this,
                "Booking Successful!\n"
                        + "Reference: " + booking.getBookingNumber() + "\n"
                        + "Movie: " + booking.getMovie().getTitle() + "\n"
                        + "Showtime: " + booking.getShowTime().getDateTime() + "\n"
                        + "Seats: " + seatNumbers + "\n"
                        + "Total Paid: Ugx " + booking.getTotalPrice(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}