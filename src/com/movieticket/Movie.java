

import java.util.Arrays;

/** Represents a movie and its scheduled showtimes. */
public class Movie {
    private final String title;
    private final String genre;
    private final int durationMinutes;
    private final ShowTime[] showTimes;
    private int showTimeCount;
    public Movie(String title, String genre, int durationMinutes, int maximumShowTimes) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be blank.");
        }
        if (genre == null || genre.isBlank()) {
            throw new IllegalArgumentException("Movie genre cannot be blank.");
        }
        if (durationMinutes < 1) {
            throw new IllegalArgumentException("Duration must be at least one minute.");
        }
        if (maximumShowTimes < 1) {
            throw new IllegalArgumentException("A movie must allow at least one showtime.");
        }

        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.showTimes = new ShowTime[maximumShowTimes];
        this.showTimeCount = 0;
    }

}