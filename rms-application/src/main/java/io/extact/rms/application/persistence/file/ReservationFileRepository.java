package io.extact.rms.application.persistence.file;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.persistence.GenericRepository.ApiType;
import io.extact.rms.application.persistence.ReservationRepository;
import io.extact.rms.application.persistence.file.converter.EntityArrayConverter;
import io.extact.rms.application.persistence.file.io.FileAccessor;
import io.extact.rms.platform.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class ReservationFileRepository extends AbstractFileRepository<Reservation> implements ReservationRepository {

    @Inject
    public ReservationFileRepository(FileAccessor fileAccessor, EntityArrayConverter<Reservation> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getStartDateTime().toLocalDate().equals(startDate))
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReserverId(int reserverId) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getUserAccountId() == reserverId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByRentalItemId(int rentalItemId) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .collect(Collectors.toList());
    }

    //@Override
    public List<Reservation> findOverlappedReservations(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var conditionOfPeriod = new Reservation.DateTimePeriod(startDateTime, endDateTime);
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(conditionOfPeriod))
                .collect(Collectors.toList());
    }

    @Override
    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return this.findOverlappedReservations(rentalItemId, startDateTime, endDateTime).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Reservation> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        var conditionOfPeriod = new Reservation.DateTimePeriod(from, to);
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(conditionOfPeriod))
                .collect(Collectors.toList());
    }
}
