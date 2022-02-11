package io.extact.rms.external.webapi.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.domain.constraint.Contact;
import io.extact.rms.application.domain.constraint.LoginId;
import io.extact.rms.application.domain.constraint.Passowrd;
import io.extact.rms.application.domain.constraint.PhoneNumber;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.UserName;
import io.extact.rms.application.domain.constraint.UserTypeConstraint;

@Schema(description = "ユーザDTO")
@Getter
@Setter
public class UserAccountResourceDto {

    @RmsId
    @Schema(required = true)
    private Integer id;

    @LoginId
    @Schema(required = true)
    private String loginId;

    @Passowrd
    @Schema(required = true)
    private String password;

    @UserName
    @Schema(required = false)
    private String userName;

    @PhoneNumber
    @Schema(required = false)
    private String phoneNumber;

    @Contact
    @Schema(required = false)
    private String contact;

    @UserTypeConstraint
    @Schema(required = true)
    private UserType userType;

    public static UserAccountResourceDto toDto(UserAccount entity) {
        if (entity == null) {
            return null;
        }
        var dto = new UserAccountResourceDto();
        dto.setId(entity.getId());
        dto.setLoginId(entity.getLoginId());
        dto.setPassword(entity.getPassword());
        dto.setUserName(entity.getUserName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setContact(entity.getContact());
        dto.setUserType(entity.getUserType().name());
        return dto;
    }

    public UserAccount toEntity() {
        return UserAccount.of(id, loginId, password, userName, phoneNumber, contact, userType);
    }

    // original getter
    public String getUserType() {
        return userType.name();
    }

    // original setter
    public void setUserType(String userType) {
        this.userType = UserType.valueOf(userType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
