Ticket Management System

Requirements:-
User should be able to see the events, book tickets

## Booking flow

The in-memory booking service uses numbered seats (`1..seatingCapacity`) on each
screen. Availability is tracked separately for each show through its bookings.
Configure screen capacity before accepting bookings, and keep screen IDs, capacity,
and show times stable once bookings exist.

```text
Select show and available seats
    -> createBooking: PENDING_PAYMENT (five-minute seat hold)
        -> verified payment: confirmBooking -> CONFIRMED
        -> payment failure/user cancellation: cancelBooking -> CANCELLED
        -> hold deadline reached: EXPIRED
```

Cancelled and expired holds release all selected seats. Expiry is evaluated on
booking-service calls, including availability reads. A hold never extends beyond
show start. Confirmed bookings retain their seats; refunds and cancellation of
paid bookings are not implemented.

```java
screen.setSeatingCapacity(100);
screenRepository.save(screen);
BookingService bookingService = new BookingService(new BookingRepository(), showRepository, screenRepository);
List<Seat> available = bookingService.getAvailableSeats(show.getShowId());
Booking held = bookingService.createBooking("user-123", show.getShowId(), List.of(1, 2));
// Only call after a trusted payment provider has verified payment:
Booking confirmed = bookingService.confirmBooking(held.getBookingId(), "provider-transaction-id");
// On failure instead: bookingService.cancelBooking(held.getBookingId());
```

Booking and Seat are Java classes with getters and setters. Repository reads return
defensive copies, so editing a returned booking does not change stored reservations; use the returned object or `getBooking`
to retrieve updated status. Repeating confirmation with the same payment reference
is idempotent, and a payment reference cannot confirm multiple bookings.

All booking services must share one `BookingRepository`. Seat checks and updates
are synchronized on that repository, so competing requests in this JVM cannot
reserve the same seat for the same show. Writes must go through `BookingService`.
Data is lost on restart; multiple application instances require database-backed
transactions/constraints instead of this in-memory lock.

The application main method demonstrates a hold and simulated confirmation.
There are no booking HTTP endpoints or real charges yet. Authentication, pricing,
provider verification, refunds (including payments received after hold expiry),
and a separate ticket artifact remain future integrations.

Run tests with `mvn test` (or `mvn -o test` with dependencies already cached).


## Model references

`Show` stores `screenId` and `movieId`; `Threater` stores `List<String> screenIds`.
Resolve related entities using `ScreenRepository.findById(show.getScreenId())`
and `MovieRepository.findById(show.getMovieId())`.
`Booking.showId`, `Booking.userId`, and `Seat.screenId` are also identifiers.
`Booking` keeps defensive copies of its `List<Seat>` value objects.

Each repository uses a typed `HashMap<String, Entity>` for direct ID lookups.
Saving the same ID replaces its existing entry; `findAll()` returns a list snapshot
with no guaranteed order. Missing IDs throw `IllegalArgumentException`.
Only the booking repository deep-copies entities; other repositories return mutable entities.

Share the same screen repository across `ThreaterService`, `ShowService`, and
`BookingService`. Create screens with `addScreen(threaterId, screenName)` and
shows with `addShow(screenId, movieId, startTime)`. Show creation validates both IDs.
