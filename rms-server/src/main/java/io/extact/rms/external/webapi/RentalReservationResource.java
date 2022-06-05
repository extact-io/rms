package io.extact.rms.external.webapi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import io.extact.rms.application.RentalReservationApplication;
import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.domain.constraint.ValidationGroups.Add;
import io.extact.rms.external.webapi.dto.AddRentalItemDto;
import io.extact.rms.external.webapi.dto.AddReservationDto;
import io.extact.rms.external.webapi.dto.AddUserAccountDto;
import io.extact.rms.external.webapi.dto.LoginDto;
import io.extact.rms.external.webapi.dto.RentalItemResourceDto;
import io.extact.rms.external.webapi.dto.ReservationResourceDto;
import io.extact.rms.external.webapi.dto.UserAccountResourceDto;
import io.extact.rms.platform.jwt.consumer.Authenticated;
import io.extact.rms.platform.jwt.provider.GenerateToken;
import io.extact.rms.platform.validate.ValidateGroup;
import io.extact.rms.platform.validate.ValidateParam;

@Path("/rms")
@ApplicationScoped
@ValidateParam
public class RentalReservationResource implements WebApiSpec {

    private RentalReservationApplication application;

    @Inject
    public RentalReservationResource(RentalReservationApplication application) {
        this.application = application;
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(String loginId, String password) {
        return authenticate(LoginDto.of(loginId, password)); // this method is for debug so convert.
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(LoginDto loginDto) {
        return application.authenticate(loginDto.getLoginId(), loginDto.getPassword())
                .transform(UserAccountResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate date) {
        return application.findReservationByRentalItemAndStartDate(rentalItemId, date)
                .stream()
                .map(ReservationResourceDto::toDto)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByReserverId(Integer reserverId) {
        return application.findReservationByReserverId(reserverId)
                .stream()
                .map(ReservationResourceDto::toDto)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> getOwnReservations() {
        return findReservationByReserverId(LoginUserUtils.get().getUserId());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE }) // add admin role for react-ui
    @Override
    public List<RentalItemResourceDto> getAllRentalItems() {
        return application.getAllRentalItems()
                .stream()
                .map(RentalItemResourceDto::toDto)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @ValidateGroup(groups = Add.class) // for @ReserveStartDateTimeFuture
    @Override
    public ReservationResourceDto addReservation(AddReservationDto addDto) {
        return application.addReservation(addDto.toEntity())
                .transform(ReservationResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public void cancelReservation(Integer reservationId) {
        application.cancelReservation(reservationId);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByRentalItemId(Integer rentalItemId) {
        return application.findReservationByRentalItemId(rentalItemId)
                .stream()
                .map(ReservationResourceDto::toDto)
                .toList();
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<RentalItemResourceDto> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to) {
        return application.findCanRentedItemAtTerm(from, to)
                .stream()
                .map(RentalItemResourceDto::toDto)
                .toList();
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public boolean canRentedItemAtTerm(Integer rentalItemId, LocalDateTime from, LocalDateTime to) {
        return application.canRentedItemAtTerm(rentalItemId, from, to);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public RentalItemResourceDto addRentalItem(AddRentalItemDto addDto) {
        return application.addRentalItem(addDto.toEntity())
                .transform(RentalItemResourceDto::toDto);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public RentalItemResourceDto updateRentalItem(RentalItemResourceDto updateDto) {
        return application.updateRentalItem(updateDto.toEntity())
                .transform(RentalItemResourceDto::toDto);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteRentalItem(Integer rentalItemId) {
        application.deleteRentalItem(rentalItemId);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public List<ReservationResourceDto> getAllReservations() {
        return application.getAllReservations()
                .stream()
                .map(ReservationResourceDto::toDto)
                .toList();
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public ReservationResourceDto updateReservation(ReservationResourceDto updateDto) {
        return application.updateReservation(updateDto.toEntity())
                .transform(ReservationResourceDto::toDto);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteReservation(Integer reservationId) {
        application.deleteReservation(reservationId);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public List<UserAccountResourceDto> getAllUserAccounts() {
        return application.getAllUserAccounts()
                .stream()
                .map(UserAccountResourceDto::toDto)
                .toList();
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto addUserAccount(AddUserAccountDto addDto) {
        return application.addUserAccount(addDto.toEntity())
                .transform(UserAccountResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto updateUserAccount(UserAccountResourceDto updateDto) {
        return application.updateUserAccount(updateDto.toEntity())
                .transform(UserAccountResourceDto::toDto);
    }

    // for react-ui
    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteUserAccount(Integer userAccountId) {
        application.deleteUserAccount(userAccountId);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    @Override
    public UserAccountResourceDto getOwnUserProfile() {
        return application.getOwnUserProfile().transform(UserAccountResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    @Override
    public UserAccountResourceDto updateUserProfile(UserAccountResourceDto updateDto) {
        return application.updateUserProfile(updateDto.toEntity())
                .transform(UserAccountResourceDto::toDto);
    }
}
