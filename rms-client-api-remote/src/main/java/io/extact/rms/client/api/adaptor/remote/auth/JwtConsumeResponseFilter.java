package io.extact.rms.client.api.adaptor.remote.auth;

import java.io.IOException;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.tuple.Pair;

import io.extact.rms.client.api.login.JsonWebTokenConsumeEvent;

/**
 * レスポンスヘッダからJsonWebTokenを取得し通知を行うクラス
 */
public class JwtConsumeResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        if (responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {

            var notificator = CDI.current().select(JwtConsumeEventNotifyDelegator.class).get();
            var value = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            notificator.push(new JsonWebTokenConsumeEvent(Pair.of(HttpHeaders.AUTHORIZATION, value)));
        }
    }
}
