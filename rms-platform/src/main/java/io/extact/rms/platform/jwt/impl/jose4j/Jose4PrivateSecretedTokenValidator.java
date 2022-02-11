package io.extact.rms.platform.jwt.impl.jose4j;

import static io.extact.rms.platform.jwt.impl.jose4j.KeyCreators.*;

import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import io.extact.rms.platform.extension.ConfiguableScoped;
import io.extact.rms.platform.jwt.JwtConfig;
import io.extact.rms.platform.jwt.JwtValidateException;
import io.extact.rms.platform.jwt.consumer.JsonWebTokenValidator;

/**
 * 秘密鍵で署名されたTokenの検証クラス。
 */
@ConfiguableScoped
public class Jose4PrivateSecretedTokenValidator implements JsonWebTokenValidator {

    private JwtConfig jwtConfig;

    @Inject
    public Jose4PrivateSecretedTokenValidator(Config config) {
        this.jwtConfig = JwtConfig.of(config);
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {

        // 検証条件の設定
        JwtConsumer consumer = new JwtConsumerBuilder()
                // 有効期限をチェックする
                .setRequireExpirationTime()
                // 有効期限の時間ズレ許容秒数
                .setAllowedClockSkewInSeconds(jwtConfig.getAllowedClockSeconds())
                // サブジェクトは必須
                .setRequireSubject()
                // JwtIdは必須
                .setRequireJwtId()
                // 発行者は自分自身であること
                .setExpectedIssuer(jwtConfig.getIssuer())
                // 受信者のチェックはしない
                .setSkipDefaultAudienceValidation()
                // トークンの署名を検査するキー（＝署名に使ったキー）
                .setVerificationKey(jwtConfig.getSecretKey(PHRASE_TO_KEY_CONVERTER))
                // 復号キーの形式チェックはしない
                .setRelaxVerificationKeyValidation()
                .build();

        // tokenを検証しClaimsを取り出す
        JwtClaims claims;
        String userPrincipalName;
        try {
            claims = consumer.processToClaims(token);
            userPrincipalName = claims.getStringClaimValue(Claims.upn.name());
            Objects.requireNonNull(userPrincipalName); // 必須項目
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new JwtValidateException(e);
        }

        claims.setClaim(Claims.raw_token.name(), token);
        return new Jose4jCallerPrincipal(userPrincipalName, claims);
    }
}
