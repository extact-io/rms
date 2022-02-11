package io.extact.rms.client.api.dto;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserAccountClientDto implements Convertable {

    private Integer id;
    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private ClientUserType userType;

    public enum ClientUserType {

        ADMIN(true),
        MEMBER(false);

        boolean admin;
        private ClientUserType(boolean admin) {
            this.admin = admin;
        }

        public boolean isAdmin() {
            return admin;
        }

        public static boolean isValidUserType(String userTypeName) {
            try {
                ClientUserType.valueOf(userTypeName);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    public static UserAccountClientDto ofTransient(String loginId, String password, String userName, String phoneNumber, String contact, ClientUserType userType) {
        return of(null, loginId, password, userName, phoneNumber, contact, userType);
    }

    public Set<String> getRoles() {
        return Set.of(userType.name());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
