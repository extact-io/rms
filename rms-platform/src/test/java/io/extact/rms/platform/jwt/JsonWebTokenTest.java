package io.extact.rms.platform.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.InvalidJwtSignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.extact.rms.platform.jwt.consumer.JsonWebTokenValidator;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaims;

public class JsonWebTokenTest {

    private MapConfig mapConfig;

    @BeforeEach
    void setup() {
        // デフォルト値の設定
        MapConfig config = new MapConfig();
        config.setSecretPhrase("privateSecretkey");
        config.setIssuer("testApplication");
        config.setIssuedAt(-1L);
        config.setExpirationTime(60.0F);
        config.setAllowedClockSeconds(30);
        this.mapConfig = config;
    }

    @Test
    void testGenerateTokenAndValidate() throws JwtValidateException {

        // テストし易いように発行日時と有効期限を固定
        long now = System.currentTimeMillis() / 1000L; // 秒で表した現在日時
        mapConfig.setIssuedAt(now); // 発行日時を固定で設定
        mapConfig.setExpirationTime(00.0F);

        // Tokenの生成
        JsonWebTokenGenerator generator = new Jose4jJwtGenerator(mapConfig);
        UserClaims userClaims = new SimpleUserClaims();
        String token = generator.generateToken(userClaims);

        // 生成したTokenを検査
        JsonWebTokenValidator validator = new Jose4PrivateSecretedTokenValidator(mapConfig);
        JsonWebToken jwt = validator.validate(token);

        // 復元したJSONが元通りか確認
        JwtConfig jwtConfig = JwtConfig.of(mapConfig);
        assertThat(jwt.getName()).isEqualTo(userClaims.getUserPrincipalName());
        assertThat(jwt.getRawToken()).isEqualTo(token);
        assertThat(jwt.getIssuer()).isEqualTo(jwtConfig.getIssuer());
        assertThat(jwt.getAudience()).isNull();
        assertThat(jwt.getSubject()).isEqualTo(userClaims.getUserId());
        assertThat(jwt.getTokenID()).isNotNull();
        assertThat(jwt.getIssuedAtTime()).isEqualTo(now);
        assertThat(jwt.getExpirationTime()).isBetween(now, now + 5L); // JwtClaims内部でnowをするため+5msecまでは誤差として許容
        assertThat(jwt.getGroups()).hasSize(1);
        assertThat(jwt.getGroups()).containsAll(userClaims.getGroups());


    }

    @Test
    void testTokenExpired() {

        // 有効期限と誤差許容を0にして直ぐ有効期間切れになるように設定
        mapConfig.setExpirationTime(0.0F);
        mapConfig.setAllowedClockSeconds(0);

        JsonWebTokenGenerator generator = new Jose4jJwtGenerator(mapConfig);
        UserClaims userClaims = new SimpleUserClaims();
        String token = generator.generateToken(userClaims);

        JsonWebTokenValidator validator = new Jose4PrivateSecretedTokenValidator(mapConfig);
        JwtValidateException actual = catchThrowableOfType(() ->
            validator.validate(token),
            JwtValidateException.class
        );
        assertThat(actual.getCause().getClass()).isEqualTo(InvalidJwtException.class);
    }

    @Test
    void testTokenFalsified() {
        // Tokenの生成
        JsonWebTokenGenerator generator = new Jose4jJwtGenerator(mapConfig);
        UserClaims userClaims = new SimpleUserClaims();
        String token = generator.generateToken(userClaims);

        String invalidToken = token + "a"; // Tokenを改ざん

        // 生成したTokenを検査
        JsonWebTokenValidator validator = new Jose4PrivateSecretedTokenValidator(mapConfig);
        JwtValidateException actual = catchThrowableOfType(() ->
            validator.validate(invalidToken),
            JwtValidateException.class
        );
        assertThat(actual.getCause().getClass()).isEqualTo(InvalidJwtSignatureException.class);
    }

    static class SimpleUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "soramame";
        }
        @Override
        public String getUserPrincipalName() {
            return "soramame@rms.com";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("1");
        }
    }
}
