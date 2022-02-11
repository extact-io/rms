package io.extact.rms.client.api.adaptor.remote.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.rms.client.api.adaptor.remote.auth.JwtClientHeadersFactory;
import io.extact.rms.client.api.adaptor.remote.auth.JwtConsumeResponseFilter;
import io.extact.rms.client.api.adaptor.remote.dto.AddRentalItemRequestDto;
import io.extact.rms.client.api.adaptor.remote.dto.AddReservationRequestDto;
import io.extact.rms.client.api.adaptor.remote.dto.AddUserAccountRequestDto;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.platform.jaxrs.converter.RmsTypeParameterFeature;

@RegisterRestClient(configKey = "web-api")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ClientExceptionMapper.class)
@RegisterProvider(JwtConsumeResponseFilter.class)
@RegisterClientHeaders(JwtClientHeadersFactory.class)
@Path("/rms")
public interface RentalReservationRestClient {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountClientDto authenticate(Map<String, String> paramMap);

    @GET
    @Path("/reservations/item/{itemId}/startdate/{startDate}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationClientDto> findReservation(@PathParam("itemId") int itemId, @PathParam("startDate") LocalDate targetDate);

    @GET
    @Path("/reservations/reserver/{reserverId}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationClientDto> findReservationByReserverId(@PathParam("reserverId") Integer reserverId);

    @GET
    @Path("/reservations/own")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationClientDto> getOwnReservations();

    @GET
    @Path("/items")
    List<RentalItemClientDto> getAllRentalItems();

    @GET
    @Path("/users")
    List<UserAccountClientDto> getAllUserAccounts();

    @POST
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReservationClientDto addReservation(AddReservationRequestDto requestDto);

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    RentalItemClientDto addRentalItem(AddRentalItemRequestDto requestDto);

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountClientDto addUserAccount(AddUserAccountRequestDto requestDto);

    @DELETE
    @Path("/reservations/own/{reservationId}")
    void cancelReservation(@PathParam("reservationId") Integer reservationId);

    @PUT
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountClientDto updateUserAccount(UserAccountClientDto updateUserAccountDto);
}
