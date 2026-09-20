# BookingService practice

Implement the TODOs in `src/main/java/com/kumarmangalam/ticketManagementSystem/service/BookingService.java`.
Constructors and synchronization blocks are supplied. Replace each placeholder exception
with your implementation. The app cannot complete its booking demo until these methods work.

Suggested order:
1. `capacity` and `upcomingShow`
2. `occupiedSeats` and `expireHolds`
3. `getAvailableSeats` and `getBooking`
4. `createBooking`
5. `confirmBooking` and `cancelBooking`

Run `mvn -o -Dtest=BookingServiceTests,ModelReferenceTests test` to check your work
(use Maven without `-o` if dependencies are not cached). Tests are intentionally
expected to fail while methods are unimplemented; do not disable them.

The previous working implementation is saved in `reference/BookingService.java.txt`.
It is outside the source tree and is not compiled.
