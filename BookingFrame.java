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
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Displays the movie selection, seat map, and booking summary. */
public class BookingFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PRIMARY_COLOR = new Color(35, 76, 120);
    private static final Color AVAILABLE_COLOR = new Color(220, 240, 228);
    private static final Color SELECTED_COLOR = new Color(190, 218, 247);
    private static final Color BOOKED_COLOR = new Color(232, 234, 237);

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
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(12, 12));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));

        JLabel heading = new JLabel("MOVIE TICKET BOOKING", JLabel.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setForeground(PRIMARY_COLOR);
        topPanel.add(heading, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        selectionPanel.setBackground(BACKGROUND_COLOR);
        selectionPanel.add(new JLabel("Movie"));
        selectionPanel.add(movieDropdown);
        selectionPanel.add(new JLabel("Showtime"));
        selectionPanel.add(showtimeDropdown);
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        movieDropdown.setPreferredSize(new java.awt.Dimension(250, 30));
        showtimeDropdown.setPreferredSize(new java.awt.Dimension(200, 30));
        add(topPanel, BorderLayout.NORTH);

        seatGridPanel.setBackground(Color.WHITE);
        seatGridPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Choose your seats"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JScrollPane seatScrollPane = new JScrollPane(seatGridPanel);
        seatScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        seatScrollPane.getViewport().setBackground(Color.WHITE);
        add(seatScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4, 16, 16, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));
        bookButton.setEnabled(false);
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
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
            seatGridPanel.setLayout(new GridLayout(rows, columns, 8, 8));

            for (int index = 0; index < selectedShowtime.getSeatCount(); index++) {
                Seat seat = selectedShowtime.getSeatAt(index);
                JToggleButton seatButton = new JToggleButton(seat.getSeatNumber());
                seatButton.setOpaque(true);
                seatButton.setFocusPainted(false);
                if (seat.isBooked()) {
                    seatButton.setEnabled(false);
                    seatButton.setText(seat.getSeatNumber() + " (Booked)");
                    seatButton.setBackground(BOOKED_COLOR);
                } else {
                    seatButton.setBackground(AVAILABLE_COLOR);
                    seatButton.addActionListener(event -> {
                        if (seatButton.isSelected()) {
                            selectedSeats.add(seat);
                            seatButton.setBackground(SELECTED_COLOR);
                        } else {
                            selectedSeats.remove(seat);
                            seatButton.setBackground(AVAILABLE_COLOR);
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
                        + "Room: " + booking.getShowTime().getRoomName() + "\n"
                        + "Seats: " + seatNumbers + "\n"
                        + "Total Paid: Ugx " + booking.getTotalPrice(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}