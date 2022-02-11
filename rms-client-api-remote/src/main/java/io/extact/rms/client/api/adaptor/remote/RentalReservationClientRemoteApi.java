package io.extact.rms.client.api.adaptor.remote;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.adaptor.remote.dto.AddRentalItemRequestDto;
import io.extact.rms.client.api.adaptor.remote.dto.AddReservationRequestDto;
import io.extact.rms.client.api.adaptor.remote.dto.AddUserAccountRequestDto;
import io.extact.rms.client.api.adaptor.remote.rest.RentalReservationRestClient;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.platform.extension.ConfiguableScoped;

@ConfiguableScoped
public class RentalReservationClientRemoteApi implements RentalReservationClientApi {

    @Inject
    @RestClient
    private RentalReservationRestClient client;

    @Override
    public UserAccountClientDto authenticate(String loginId, String password) {
        var paramMap = new HashMap<String, String>();
        paramMap.put("loginId", loginId);
        paramMap.put("password", password);
        return client.authenticate(paramMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    // RestClient does not allow type definition of List<? extends ReservationDto>. So forcibly cast.
    public List<ReservationClientDto> findReservationByRentalItemAndStartDate(Integer targetRentalItemId, LocalDate targetDate) {
        return (List<ReservationClientDto>)(List<?>)client.findReservation(targetRentalItemId, targetDate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReservationClientDto> findReservationByReserverId(int reserverId) {
        return (List<ReservationClientDto>)(List<?>)client.findReservationByReserverId(reserverId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReservationClientDto> getOwnReservations() {
        return (List<ReservationClientDto>)(List<?>)client.getOwnReservations();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RentalItemClientDto> getAllRentalItems() {
        return (List<RentalItemClientDto>)(List<?>)client.getAllRentalItems();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAccountClientDto> getAllUserAccounts() {
        return (List<UserAccountClientDto>)(List<?>)client.getAllUserAccounts();
    }

    @Override
    public ReservationClientDto addReservation(ReservationClientDto addReservation) {
        var requestDto = new AddReservationRequestDto(addReservation);
        return client.addReservation(requestDto);
    }

    @Override
    public RentalItemClientDto addRentalItem(RentalItemClientDto addRentalItem) {
        var requestDto = new AddRentalItemRequestDto(addRentalItem);
        return client.addRentalItem(requestDto);
    }

    @Override
    public UserAccountClientDto addUserAccount(UserAccountClientDto addUserAccountDto) {
        var requestDto = new AddUserAccountRequestDto(addUserAccountDto);
        return client.addUserAccount(requestDto);
    }

    @Override
    public void cancelReservation(int reservationId) throws BusinessFlowClientException {
        client.cancelReservation(reservationId);
    }

    @Override
    public UserAccountClientDto updateUserAccount(UserAccountClientDto updateUserAccountDto) {
        return client.updateUserAccount(updateUserAccountDto);
    }
}
