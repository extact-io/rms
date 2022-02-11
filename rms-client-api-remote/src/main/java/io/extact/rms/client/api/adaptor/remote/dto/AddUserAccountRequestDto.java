package io.extact.rms.client.api.adaptor.remote.dto;

import lombok.Getter;

import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto.ClientUserType;

@Getter
public class AddUserAccountRequestDto {

    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private ClientUserType userType;

    // ----------------------------------------------------- constructor methods

    public AddUserAccountRequestDto(UserAccountClientDto clientDto) {
        loginId = clientDto.getLoginId();
        password = clientDto.getPassword();
        userName = clientDto.getUserName();
        phoneNumber = clientDto.getPhoneNumber();
        contact = clientDto.getContact();
        userType = clientDto.getUserType();
    }

}
