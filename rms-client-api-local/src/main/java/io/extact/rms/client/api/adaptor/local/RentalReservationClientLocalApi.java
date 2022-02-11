package io.extact.rms.client.api.adaptor.local;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.extact.rms.application.RentalReservationApplication;
import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.adaptor.local.BindLoginUser.LoginAction;
import io.extact.rms.client.api.adaptor.local.dto.RentalItemDtoConverter;
import io.extact.rms.client.api.adaptor.local.dto.ReservationDtoConverter;
import io.extact.rms.client.api.adaptor.local.dto.UserAccountDtoConverter;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.platform.extension.ConfiguableScoped;

@ConfiguableScoped
@HandleExceptions
@BindLoginUser
public class RentalReservationClientLocalApi implements RentalReservationClientApi {

    @Inject
    private RentalReservationApplication application;

    @LoginAction
    @Override
    public UserAccountClientDto authenticate(String loginId, String password)
            throws BusinessFlowClientException {
        return application.authenticate(loginId, password).transform(UserAccountDtoConverter::toDto);
    }

    @Override
    public List<ReservationClientDto> findReservationByRentalItemAndStartDate(Integer itemId, LocalDate startDate)
            throws BusinessFlowClientException {
        return application.findReservationByRentalItemAndStartDate(itemId, startDate).stream()
                    .map(ReservationDtoConverter::toDto)
                    .collect(Collectors.toList());
    }

    @Override
    public List<ReservationClientDto> findReservationByReserverId(int reserverId) {
        return application.findReservationByReserverId(reserverId).stream()
                    .map(ReservationDtoConverter::toDto)
                    .collect(Collectors.toList());
    }

    @Override
    public List<ReservationClientDto> getOwnReservations() {
        return findReservationByReserverId(LoginUserUtils.get().getUserId());
    }

    @Override
    public List<RentalItemClientDto> getAllRentalItems() {
        return application.getAllRentalItems().stream()
                    .map(RentalItemDtoConverter::toDto)
                    .collect(Collectors.toList());
    }

    @Override
    public List<UserAccountClientDto> getAllUserAccounts() {
        return application.getAllUserAccounts().stream()
                    .map(UserAccountDtoConverter::toDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ReservationClientDto addReservation(ReservationClientDto addReservationDto) throws BusinessFlowClientException {
        var newReservation = application.addReservation(addReservationDto.to(ReservationDtoConverter::toEntity));
        return newReservation.transform(ReservationDtoConverter::toDto);
    }

    @Override
    public RentalItemClientDto addRentalItem(RentalItemClientDto addRentalItemDto) throws BusinessFlowClientException {
        var newRentalItem = application.addRentalItem(addRentalItemDto.to(RentalItemDtoConverter::toEntity));
        return newRentalItem.transform(RentalItemDtoConverter::toDto);
    }

    @Override
    public UserAccountClientDto addUserAccount(UserAccountClientDto addUserDto) throws BusinessFlowClientException {
        var newUserAccount = application.addUserAccount(addUserDto.to(UserAccountDtoConverter::toEntity));
        return newUserAccount.transform(UserAccountDtoConverter::toDto);
    }

    @Override
    public void cancelReservation(int reservationId) throws BusinessFlowClientException {
        application.cancelReservation(reservationId);
    }

    @Override
    public UserAccountClientDto updateUserAccount(UserAccountClientDto updateUserAccountDto) {
        var updatedUser = application.updateUserAccount(updateUserAccountDto.to(UserAccountDtoConverter::toEntity));
        return updatedUser.transform(UserAccountDtoConverter::toDto);
    }
}
