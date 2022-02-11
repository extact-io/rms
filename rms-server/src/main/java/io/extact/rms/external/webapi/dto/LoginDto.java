package io.extact.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.extact.rms.application.domain.constraint.LoginId;
import io.extact.rms.application.domain.constraint.Passowrd;

@Schema(description = "ログインDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LoginDto {

    @LoginId
    @Schema(required = true, minLength = 5, maxLength = 10)
    private String loginId;

    @Passowrd
    @Schema(required = true, minLength = 5, maxLength = 10)
    private String password;
}
