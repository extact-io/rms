package io.extact.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.domain.constraint.ItemName;
import io.extact.rms.application.domain.constraint.LoginId;
import io.extact.rms.application.domain.constraint.Passowrd;
import io.extact.rms.application.domain.constraint.UserName;
import io.extact.rms.application.domain.constraint.UserTypeConstraint;

@Schema(description = "ユーザ登録用DTO")
@Getter
@Setter
public class AddUserAccountDto {

    @Schema(required = true)
    @LoginId
    private String loginId;

    @Schema(required = true)
    @Passowrd
    private String password;

    @Schema(required = true)
    @UserName
    private String userName;

    @Schema(required = false)
    @ItemName
    private String phoneNumber;

    @Schema(required = false)
    private String contact;

    @Schema(required = true)
    @UserTypeConstraint
    private UserType userType;

    public UserAccount toEntity() {
        return UserAccount.ofTransient(loginId, password, userName, phoneNumber, contact, userType);
    }
}
