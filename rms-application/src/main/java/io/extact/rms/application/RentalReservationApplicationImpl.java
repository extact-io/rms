package io.extact.rms.application;

import static io.extact.rms.application.exception.BusinessFlowException.CauseType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.RmsSystemException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.service.RentalItemService;
import io.extact.rms.application.service.ReservationService;
import io.extact.rms.application.service.UserAccountService;
import io.extact.rms.platform.debug.DebugSleepInterceptor.DebugSleep;

@Transactional(TxType.REQUIRED)
@ApplicationScoped
@DebugSleep
public class RentalReservationApplicationImpl implements RentalReservationApplication {

    private RentalItemService rentalItemService;
    private ReservationService reservationService;
    private UserAccountService userService;

    private Map<Class<?>, Function<Integer, ?>> entityGetterMap;

    // ----------------------------------------------------- constructor methods

    @Inject
    public RentalReservationApplicationImpl(RentalItemService rentalItemService,
                ReservationService reservationService,
                UserAccountService userAccountService) {
        this.rentalItemService = rentalItemService;
        this.reservationService = reservationService;
        this.userService = userAccountService;

        this.entityGetterMap = Map.of(
                RentalItem.class, rentalItemService::get,
                Reservation.class, reservationService::get,
                UserAccount.class, userAccountService::get
            );
    }


    // ----------------------------------------------------- public methods

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> entityClass, int id) {
        return (T) entityGetterMap.get(entityClass).apply(id);
    }

    @Override
    public UserAccount authenticate(String loginId, String password) throws BusinessFlowException {
        var user = userService.findByLoginIdAndPasswod(loginId, password);
        if (user == null) {
            throw new BusinessFlowException("The loginId or password is different", NOT_FOUND);
        }
        return user;
    }

    @Override
    public List<Reservation> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate startDate)
            throws BusinessFlowException {
        var reservations = reservationService.findByRentalItemAndStartDate(rentalItemId, startDate);
        if (reservations.isEmpty()) {
            throw new BusinessFlowException("Reservation does not exist for rentalItemId and startDate", NOT_FOUND);
        }
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationByReserverId(int reserverId) {
        var reservations = reservationService.findByReserverId(reserverId);
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationByRentalItemId(int rentalItemId) {
        var reservations = reservationService.findByRentalItemId(rentalItemId);
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalItem> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to) {
        var reservedItemIds = reservationService.findOverlappedReservation(from, to).stream()
                .map(Reservation::getRentalItemId)
                .collect(Collectors.toList());
        return rentalItemService.findAll().stream()
                .filter(item -> !reservedItemIds.contains(item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canRentedItemAtTerm(int rentalItemId, LocalDateTime from, LocalDateTime to) {
        return reservationService.findOverlappedReservation(rentalItemId, from, to) == null;
    }

    @Override
    public List<Reservation> getAllReservations() {
        var reservations = reservationService.findAll();
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalItem> getAllRentalItems() {
        return rentalItemService.findAll();
    }

    @Override
    public List<UserAccount> getAllUserAccounts() {
        return userService.findAll();
    }

    @Override
    public Reservation addReservation(Reservation addReservation) throws BusinessFlowException {

        var rentalItem = rentalItemService.get(addReservation.getRentalItemId());
        if (rentalItem == null) {
            throw new BusinessFlowException("RentalItem does not exist for rentalItemId.", NOT_FOUND);
        }

        // 予約の登録
        var newReservation = reservationService.add(addReservation);

        // 予約オブジェクトの再構成
        var reserver = getUserAccount(addReservation.getUserAccountId());
        newReservation.setRentalItem(rentalItem);
        newReservation.setUserAccount(reserver);

        return newReservation;
    }

    @Override
    public RentalItem addRentalItem(RentalItem addRentalItem) throws BusinessFlowException, RmsSystemException {
        return rentalItemService.add(addRentalItem);
    }

    @Override
    public UserAccount addUserAccount(UserAccount addUserAccount) throws BusinessFlowException {
        return userService.add(addUserAccount);
    }

    @Override
    public RentalItem updateRentalItem(RentalItem updateRentalItem) {
        return rentalItemService.update(updateRentalItem);
    }

    @Override
    public Reservation updateReservation(Reservation updateReservation) {
        var reservation = reservationService.update(updateReservation);
        return toTraversedReservation(reservation);
    }

    @Override
    public UserAccount updateUserAccount(UserAccount updateUserAccount) {
        return userService.update(updateUserAccount);
    }

    @Override
    public void deleteRentalItem(int rentalItemId) throws BusinessFlowException {
        var isRefered = reservationService.hasRefereToRentalItem(rentalItemId);
        if (isRefered) {
            throw new BusinessFlowException("Cannot be deleted because it is referenced in the reservation.", CauseType.REFERED);
        }
        rentalItemService.delete(rentalItemId);
    }

    @Override
    public void deleteReservation(int reservationId) throws BusinessFlowException {
        reservationService.delete(reservationId);
    }

    @Override
    public void deleteUserAccount(int userAccountId) throws BusinessFlowException {
        var isRefered = reservationService.hasRefereToUserAccount(userAccountId);
        if (isRefered) {
            throw new BusinessFlowException("Cannot be deleted because it is referenced in the reservation.", CauseType.REFERED);
        }
        userService.delete(userAccountId);
    }

    @Override
    public void cancelReservation(int reservationId) throws BusinessFlowException {
        reservationService.cancel(reservationId, LoginUserUtils.get().getUserId());
    }

    @Override
    public UserAccount getOwnUserProfile() throws BusinessFlowException {
        var targetId = LoginUserUtils.get().getUserId();
        var userAccount = userService.get(targetId);
        if (userAccount == null) {
            throw new BusinessFlowException("UserAccount does not exist for LoginId.", NOT_FOUND);
        }
        return userAccount;
    }

    @Override
    public UserAccount updateUserProfile(UserAccount updateUserAccount) {
        if (LoginUserUtils.get().getUserId() != updateUserAccount.getId()) {
            throw new BusinessFlowException("other's profile cannot be updated.", CauseType.FORBIDDEN);
        }
        return userService.update(updateUserAccount);
    }

    // ----------------------------------------------------- private methods

    private Reservation toTraversedReservation(Reservation resavation) {
        var rentalItems = getRentalItem(resavation.getRentalItemId());
        resavation.setRentalItem(rentalItems);
        var reservers = getUserAccount(resavation.getUserAccountId());
        resavation.setUserAccount(reservers);
        return resavation;
    }

    private RentalItem getRentalItem(int rentalItemId) {
        return rentalItemService.get(rentalItemId);
    }

    private UserAccount getUserAccount(int userAccountId) {
        return userService.get(userAccountId);
    }
}
