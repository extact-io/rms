package io.extact.rms.external.webapi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.stopbugs.SuppressFBWarnings;

/**
 * Resources that accept server management commands.
 */
@Path("/mng")
@ApplicationScoped
@Slf4j
public class ManagementResource {

    @GET
    @Path("stop")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressFBWarnings("DM_EXIT")
    public String stopApplication(@Context HttpHeaders headers) {

        var host = headers.getHeaderString("Host");
        if (!host.toLowerCase().startsWith("localhost")) {
            log.warn("Ignore because it is a request from other than localhost.[host={}]", host);
            return "failed";
        }

        new Thread( () -> {
            log.info("Receive end event: Ends after 3 seconds");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            log.info("Execute: System.exit(0)");
            System.exit(0);
        } ).start();
        return "success";
    }
}
