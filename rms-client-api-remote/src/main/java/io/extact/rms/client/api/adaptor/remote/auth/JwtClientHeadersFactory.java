package io.extact.rms.client.api.adaptor.remote.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.extact.rms.client.api.login.JsonWebTokenConsumeEvent;

/**
 * サーバから発行されたJsonWebTokenをリクエストヘッダに付加するクラス
 */
@ApplicationScoped
public class JwtClientHeadersFactory implements ClientHeadersFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JwtClientHeadersFactory.class);
    private Pair<String, String> authHeader;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {

        if (authHeader == null) {
            return clientOutgoingHeaders;
        }

        var newHeadersMap = new MultivaluedHashMap<String, String>(clientOutgoingHeaders);
        newHeadersMap.add(authHeader.getKey(), authHeader.getValue());
        return newHeadersMap;
    }

    void onEvent(@Observes JsonWebTokenConsumeEvent event) {
        LOG.info("ヘッダに追加するJWTを受信しました");
        this.authHeader = event.getToken();
    }
}
