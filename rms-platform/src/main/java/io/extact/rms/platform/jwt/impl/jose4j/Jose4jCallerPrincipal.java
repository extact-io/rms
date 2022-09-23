package io.extact.rms.platform.jwt.impl.jose4j;

import java.util.HashSet;
import java.util.Set;

import jakarta.security.enterprise.CallerPrincipal;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;

/**
 * jose4jの{@link JwtClaims Claims}を実体に持ったCellerPrincipal実装。
 * {@link JsonWebToken}に定義されているプロパティ以外の動作は考慮していない。
 */
public class Jose4jCallerPrincipal extends CallerPrincipal implements JsonWebToken {

    private JwtClaims claims;

    public Jose4jCallerPrincipal(String name, JwtClaims claims) {
        super(name);
        this.claims = claims;
    }

    @Override
    public Set<String> getGroups() {
        // JOSE4JはListでparseするがMP-JWTはSetを要求するため変換
        return new HashSet<>(getClaim(Claims.groups.name()));
    }

    @Override
    public Set<String> getClaimNames() {
        return new HashSet<>(claims.getClaimNames());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClaim(String claimName) {
        return (T) claims.getClaimValue(claimName);
    }

}
