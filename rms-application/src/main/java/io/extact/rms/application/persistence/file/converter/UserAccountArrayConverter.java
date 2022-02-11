package io.extact.rms.application.persistence.file.converter;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;

public class UserAccountArrayConverter implements EntityArrayConverter<UserAccount> {

    public static final UserAccountArrayConverter INSTANCE = new UserAccountArrayConverter();

    public UserAccount toEntity(String[] attributes) {

        var id = Integer.parseInt(attributes[0]);
        var loginId = attributes[1];
        var password = attributes[2];
        var userName = attributes[3];
        var phoneNumber = attributes[4];
        var contact = attributes[5];
        var userType = UserType.valueOf(attributes[6]);

        return UserAccount.of(id, loginId, password, userName, phoneNumber, contact, userType);
    }

    public String[] toArray(UserAccount userAccount) {

        var userAccountAttributes = new String[7];

        userAccountAttributes[0] = String.valueOf(userAccount.getId());
        userAccountAttributes[1] = userAccount.getLoginId();
        userAccountAttributes[2] = userAccount.getPassword();
        userAccountAttributes[3] = userAccount.getUserName();
        userAccountAttributes[4] = userAccount.getPhoneNumber();
        userAccountAttributes[5] = userAccount.getContact();
        userAccountAttributes[6] = userAccount.getUserType().name();

        return userAccountAttributes;
    }
}
