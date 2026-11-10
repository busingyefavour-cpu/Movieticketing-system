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