# Movie Ticket Booking System
CSC 1102 Structured and Object-Oriented Programming practical exam.

## Development ground rules
- it Uses Java and Swing,
- Store movies, showtimes, seats, and bookings in arrays while the program runs;
- Keep each class focused on one kind of information or responsibility.
- Keep fields private. Add setters only when a value should change; use a focused method for state changes such as booking a seat.

## Environment

The project uses standard Java and Swing; no third-party libraries or build tool are needed. A JDK is required because it includes both `java` and `javac`. The development computer currently reports Java 26.0.2.1.

From the project root in PowerShell, compile all source files and run the entry point:

```powershell
javac -d out src/com/movieticket/*.java
java -cp out com.movieticket.Main
```


Seat.java
Achieves: Represents one seat in a screening.
How: Stores a seat number and whether it is booked. Its book() method changes an available seat to booked, and isBooked() lets other classes check its status.

Showtime.java
Achieves: Represents one movie screening.
How: Stores the screening date/time and ticket price. When created, it makes a fixed array of Seat objects. It also calculates the price for a number of seats and counts available seats.

Movie.java
Achieves: Represents a movie and its schedule.
How: Stores the title, genre, duration, and a fixed-capacity array of Showtime objects. addShowTime() adds screenings; getter methods provide the movie details and scheduled showtimes.

Booking.java
Achieves: Records the details of one successful booking.
How: Stores a booking reference, the selected movie and showtime, the selected seats, and the total price. It calculates the total from the showtime’s ticket price and the number of seats.

TicketingSystem.java
Achieves: Applies the booking rules and keeps the in-memory booking list.
How: Stores the movie catalogue and a fixed-capacity array of bookings. When asked to book seats, it checks that the movie and showtime belong to the catalogue, the seats belong to that showtime and are available, and no seat was selected twice. If valid, it creates a Booking, marks the seats booked, and stores the booking.

BookingFrame.java
Achieves: Provides the desktop interface for customers.
How: Builds the Swing window with movie and showtime dropdowns, a seat grid, a price/status display, and a booking button. It asks TicketingSystem to confirm bookings and shows either an error or a booking summary.

Main.java
Achieves: Starts the complete application.
How: Creates sample movies and showtimes, creates the TicketingSystem, then opens BookingFrame on Swing’s event-dispatch thread. Create this last so the classes it connects are already in place.



Seat.java — one seat
A Seat object represents a single seat in a particular screening.

seatNumber stores its label, such as S1.
booked stores whether someone has reserved it. It starts as false.
The constructor rejects a blank seat number.
getSeatNumber() and isBooked() let other classes read the seat’s details.
book() changes the status to booked and returns true. If it was already booked, it returns false without changing it.
toString() produces a readable label, such as S1 (Available).
This file provides the basic seat state that Showtime, BookingFrame, and TicketingSystem use.


2. Showtime.java — one screening
A Showtime represents a scheduled showing of a movie.

dateTime stores when it starts.
ticketPrice stores the cost of one ticket.
seats stores the seats available for this screening.
The constructor checks that the date/time is not blank, the price is not negative, and there is at least one seat. It then creates the requested number of Seat objects, numbered S1, S2, and so on.
getTotalPrice(seatCount) multiplies the ticket price by the number of seats. It rejects a seat count below one.
getSeatCount() and getSeatAt(index) provide access to the seats.
getAllSeats() returns a copy of the seat array, rather than exposing the original array.
getAvailableSeatCount() loops through the seats and counts those not yet booked.
toString() formats the screening time and price for display.
Each Showtime owns its own seat objects, so booking a seat for one screening does not book the same-numbered seat at another screening.


3. Movie.java — a movie and its schedule
A Movie stores movie details and the screenings scheduled for it.

title, genre, and durationMinutes store the movie’s information.
showTimes is a fixed-capacity array of screenings, and showTimeCount tracks how many have been added.
The constructor rejects a blank title or genre, a duration below one minute, and a showtime capacity below one.
addShowTime(showTime) adds a screening if it is not null and the array has space. It returns true when added and false otherwise.
getShowTimeAt(index) returns a screening at a valid index; it throws an exception for an invalid index.
getAvailableShowTimes() returns a copy containing only the screenings added so far.
toString() creates a readable movie label for the interface.
The movie contains showtimes, and each showtime contains seats.


4. Booking.java — one confirmed reservation
A Booking records a successful reservation.

It stores a reference number, the Movie, the Showtime, and the selected Seat objects.
Its constructor checks that the reference is not blank, the movie and showtime exist, and at least one non-null seat was selected.
It copies the selected seats into its own array so the caller cannot replace entries in the booking by changing its original array.
It calculates and stores totalPrice using the showtime’s ticket price and the number of seats.
Getter methods let the interface display the booking reference, movie, screening, seats, and total. getSeatAt(index) checks that the requested seat index is valid.
This class describes the booking record; it does not itself check whether a seat is available or store the booking in the system. TicketingSystem handles those responsibilities.

5. TicketingSystem.java — booking rules and storage
This is the central coordinator for the catalogue and bookings.

movies holds the movie catalogue.
bookings is a fixed-capacity array for confirmed bookings.
bookingCount tracks how many bookings have been stored.
The constructor requires a non-empty movie catalogue, a booking capacity of at least one, and no null movies. It copies the movie array before storing it.
getMovieCount() and getMovieAt(index) let the interface show the catalogue.
getBookingCount() and getBookingAt(index) provide access to stored bookings, with bounds checks.
The important method is bookSeats(movie, showTime, selectedSeats):

It checks that the movie is in the catalogue and that the showtime belongs to that movie.
It checks that at least one seat was selected and that the booking list has space.
It checks each selected seat: it must exist, belong to the selected showtime, and not already be booked.
It checks that the same seat object was not selected twice.
If all checks pass, it creates a booking reference and a Booking object.
It marks each selected seat as booked, saves the booking in the array, increments the count, and returns the booking.
The private containsMovie, containsShowTime, and containsSeat methods search the relevant arrays. They use object identity (==) to verify that the selected objects are the actual movie, showtime, and seats managed by this system—not just objects with matching names.

Invalid selections cause an exception, which the interface can show to the user



6. BookingFrame.java — the Swing window
BookingFrame displays the application and responds to user actions.

Its fields hold the TicketingSystem, movie and showtime dropdowns, seat grid, booking button, status label, and currently selected seats.
The constructor initializes these components, calls setupUI(), and configures the window’s size, close behavior, and position.
setupUI() lays out the controls, adds the movies from the ticketing system to the dropdown, and connects selection changes and the booking button to handler methods.
updateShowtimes() gets the chosen movie and fills the showtime dropdown with that movie’s screenings.
renderSeats() clears the previous seat display and selection, then creates a toggle button for each seat. Booked seats are disabled; available seats can be selected.
updateStatus() updates the message and total price, and enables the booking button only when seats are selected.
bookSelectedSeats() collects the selected items and calls ticketingSystem.bookSeats(...). On success it shows a summary and refreshes the seat grid. If validation fails, it displays an error dialog.
showBookingSummary() formats the reference, movie, showtime, seat numbers, and total into a confirmation dialog.
The frame handles display and interaction. It asks TicketingSystem to enforce booking rules rather than duplicating those rules in the UI.


7. Main.java — startup
Main assembles the starting data and launches the interface.

BOOKING_CAPACITY sets the maximum number of bookings kept during this run.
createCatalogue() creates sample Movie objects, adds Showtime objects to them, and returns the movie array. Each showtime creates its own seats.
main() schedules startup with SwingUtilities.invokeLater(...), which runs Swing UI setup on its event-dispatch thread. Inside that task it creates the TicketingSystem and displays a BookingFrame.



How the files connect when the app runs
Main creates Movie objects
→ each Movie gets Showtime objects
→ each Showtime creates Seat objects

Main passes the catalogue to TicketingSystem
→ TicketingSystem is passed to BookingFrame

User chooses a movie, showtime, and seats in BookingFrame
→ BookingFrame asks TicketingSystem to book them
→ TicketingSystem validates the selection
→ creates and stores a Booking
→ marks those seats as booked

BookingFrame displays the confirmation

In short, the data classes (Seat, Showtime, Movie, Booking) describe the booking information; TicketingSystem enforces the rules; BookingFrame handles user interaction; and Main assembles and launches everything.
Main
  → creates movies, showtimes, and seats
  → creates TicketingSystem
  → opens BookingFrame
      → user selects a movie
      → available showtimes appear
      → user selects a showtime
      → seats appear
      → user selects available seats
      → TicketingSystem validates and records the booking
      → BookingFrame displays the confirmation