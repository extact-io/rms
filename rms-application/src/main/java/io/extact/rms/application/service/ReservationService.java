package io.extact.rms.application.service;

import static io.extact.rms.application.exception.BusinessFlowException.CauseType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.GenericRepository;
import io.extact.rms.application.persistence.ReservationRepository;

@ApplicationScoped
public class ReservationService implements GenericService<Reservation> {

    private ReservationRepository repository;

    @Inject
    public ReservationService(ReservationRepository reservationRepository) {
        this.repository = reservationRepository;
    }

    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return repository.findByRentalItemAndStartDate(rentalItemId, startDate);
    }

    public List<Reservation> findByReserverId(int reserverId) {
        return repository.findByReserverId(reserverId);
    }

    public List<Reservation> findByRentalItemId(int rentalItemId) {
        return repository.findByRentalItemId(rentalItemId);
    }

    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime from, LocalDateTime to) {
        return repository.findOverlappedReservation(rentalItemId, from, to);
    }

    public List<Reservation> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        return repository.findOverlappedReservations(from, to);
    }

    public boolean hasRefereToRentalItem(int rentalItemId) {
        return !findByRentalItemId(rentalItemId).isEmpty();
    }

    public boolean hasRefereToUserAccount(int userAccountId) {
        return !findByReserverId(userAccountId).isEmpty();
    }

    public void cancel(int reservationId, int cancelUserId) throws BusinessFlowException {
        var reservation = repository.get(reservationId);
        if (reservation == null) {
            throw new BusinessFlowException("Reservation does not exist for reservationId", NOT_FOUND);
        }
        // キャンセルはレンタル品を予約した人しか取り消せないことのチェック
        if (reservation.getUserAccountId() != cancelUserId) {
            throw new BusinessFlowException(
                    String.format("Others' reservations cannot be deleted. reserverId=%s, cancelUserId=%s",
                            reservation.getUserAccountId(),
                            cancelUserId),
                    CauseType.FORBIDDEN);
        }
        repository.delete(reservation);
    }

    @Override
    public Consumer<Reservation> getDuplicateChecker() {
        return (targetReservation) -> {
            var foundReservations = repository.findOverlappedReservations(
                    targetReservation.getRentalItemId(),
                    targetReservation.getStartDateTime(),
                    targetReservation.getEndDateTime());
            if (!foundReservations.isEmpty() &&
                    (targetReservation.getId() == null
                            || foundReservations.stream().anyMatch(r -> !r.isSameId(targetReservation)))) {
                throw new BusinessFlowException("Already reserved.", DUPRICATE);
            }
        };
    }

    @Override
    public GenericRepository<Reservation> getRepository() {
        return repository;
    }
}
