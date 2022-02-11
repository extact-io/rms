package io.extact.rms.application.domain;

import static javax.persistence.AccessType.*;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.extact.rms.application.domain.constraint.Contact;
import io.extact.rms.application.domain.constraint.LoginId;
import io.extact.rms.application.domain.constraint.Passowrd;
import io.extact.rms.application.domain.constraint.PhoneNumber;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.UserName;
import io.extact.rms.application.domain.constraint.UserTypeConstraint;
import io.extact.rms.application.domain.constraint.ValidationGroups.Update;

@Access(FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserAccount implements Transformable, IdAccessable {

    /** UserType */
    public enum UserType {

        ADMIN(true),
        MEMBER(false);

        boolean admin;
        private UserType(boolean admin) {
            this.admin = admin;
        }
        public boolean isAdmin() {
            return admin;
        }
    }

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;

    /** ログインID */
    @LoginId
    private String loginId;

    /** パスワード */
    @Passowrd
    private String password;

    /** ユーザ名 */
    @UserName
    private String userName;

    /** 電話番号 */
    @PhoneNumber
    private String phoneNumber;

    /** 連絡先 */
    @Contact
    private String contact;

    /** ユーザ区分 */
    @Enumerated(EnumType.STRING)
    @UserTypeConstraint
    private UserType userType;


    // ----------------------------------------------------- factory methods

    public static UserAccount ofTransient(String loginId, String password, String userName, String phoneNumber, String contact, UserType userType) {
        return of(null, loginId, password, userName, phoneNumber, contact, userType);
    }

    // ----------------------------------------------------- service methods

    @Transient
    public boolean isAdmin() {
        return this.userType == UserType.ADMIN;
    }

    public void setAdmin(boolean isAdmin) {
        this.userType = isAdmin ? UserType.ADMIN : UserType.MEMBER;
    }

    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
