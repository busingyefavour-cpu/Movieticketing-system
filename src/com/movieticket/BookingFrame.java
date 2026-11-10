
package com.movieticket;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class BookingFrame extends JFrame {

    private Movie[] movies;
    private Booking[] bookings = new Booking[100];
    private int bookingCount = 0;

    private JComboBox<Movie> movieBox;
    private JComboBox<ShowTime> showTimeBox;
    private JPanel seatPanel;
    private JLabel priceLabel;

    private JCheckBox[] seatChecks;

    public BookingFrame(Movie[] movies) {

        this.movies = movies;

        setTitle("Movie Ticket Booking System");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
            "MOVIE TICKET BOOKING SYSTEM",
            JLabel.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10));

        movieBox = new JComboBox<>(movies);

        showTimeBox = new JComboBox<>();

        seatPanel = new JPanel();

        priceLabel = new JLabel("Select a showtime and seats.");

        JButton bookButton = new JButton("CONFIRM BOOKING");

        mainPanel.add(new JLabel("Select Movie:"));
        mainPanel.add(movieBox);

        mainPanel.add(new JLabel("Select Showtime:"));
        mainPanel.add(showTimeBox);

        mainPanel.add(new JLabel("Select Seats:"));

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        seatPanel.setLayout(new FlowLayout());

        JScrollPane scrollPane = new JScrollPane(seatPanel);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(priceLabel, BorderLayout.NORTH);
        bottomPanel.add(bookButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        movieBox.addActionListener(e -> loadShowTimes());

        showTimeBox.addActionListener(e -> loadSeats());

        bookButton.addActionListener(e -> confirmBooking());

        loadShowTimes();

        setVisible(true);
    }

    private void loadShowTimes() {

        showTimeBox.removeAllItems();

        Movie movie = (Movie) movieBox.getSelectedItem();

        if (movie == null) {
            return;
        }

        ShowTime[] times = movie.getAvailableShowTimes();

        for (ShowTime time : times) {
            showTimeBox.addItem(time);
        }

        loadSeats();
    }

    private void loadSeats() {

        seatPanel.removeAll();

        ShowTime showTime =
            (ShowTime) showTimeBox.getSelectedItem();

        if (showTime == null) {
            return;
        }

        Seat[] seats = showTime.getAllSeats();

        seatChecks = new JCheckBox[seats.length];

        for (int i = 0; i < seats.length; i++) {

            Seat seat = seats[i];

            seatChecks[i] = new JCheckBox(
                seat.getSeatNumber()
            );

            seatChecks[i].setEnabled(!seat.isBooked());

            seatPanel.add(seatChecks[i]);
        }

        updatePrice();

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void updatePrice() {

        ShowTime showTime =
            (ShowTime) showTimeBox.getSelectedItem();

        if (showTime == null || seatChecks == null) {
            return;
        }

        int selected = 0;

        for (JCheckBox check : seatChecks) {
            if (check.isSelected()) {
                selected++;
            }
        }

        int total = selected * showTime.getTicketPrice();

        priceLabel.setText(
            "Selected Seats: " + selected +
            " | Total Price: UGX " + total
        );
    }

    private void confirmBooking() {

        Movie movie = (Movie) movieBox.getSelectedItem();

        ShowTime showTime =
            (ShowTime) showTimeBox.getSelectedItem();

        if (movie == null || showTime == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a movie and showtime."
            );
            return;
        }

        int count = 0;

        for (JCheckBox check : seatChecks) {
            if (check.isSelected()) {
                count++;
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select at least one seat."
            );
            return;
        }

        if (bookingCount == bookings.length) {
            JOptionPane.showMessageDialog(
                this,
                "Booking list is full."
            );
            return;
        }

        Seat[] selectedSeats = new Seat[count];

        Seat[] allSeats = showTime.getAllSeats();

        int index = 0;

        for (int i = 0; i < seatChecks.length; i++) {

            if (seatChecks[i].isSelected()) {

                if (allSeats[i].isBooked()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "A selected seat is no longer available."
                    );
                    return;
                }

                selectedSeats[index] = allSeats[i];
                index++;
            }
        }

        String bookingNumber = UUID.randomUUID()
            .toString()
            .substring(0, 8)
            .toUpperCase();

        Booking booking = new Booking(
            bookingNumber,
            movie,
            showTime,
            selectedSeats
        );

        for (Seat seat : selectedSeats) {
            seat.book();
        }

        bookings[bookingCount] = booking;
        bookingCount++;

        int total = count * showTime.getTicketPrice();

        JOptionPane.showMessageDialog(
            this,
            "BOOKING SUCCESSFUL!\n\n" +
            "Booking Number: " + bookingNumber +
            "\nMovie: " + movie.getTitle() +
            "\nShowtime: " + showTime.getDateTime() +
            "\nSeats Booked: " + count +
            "\nTotal: UGX " + total
        );

        loadSeats();
    }

    public Booking[] getBookings() {

        Booking[] result = new Booking[bookingCount];

        for (int i = 0; i < bookingCount; i++) {
            result[i] = bookings[i];
        }

        return result;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JOptionPane.showMessageDialog(
                null,
                "Pass your existing Movie array when opening BookingFrame."
            );

        });
    }
}
