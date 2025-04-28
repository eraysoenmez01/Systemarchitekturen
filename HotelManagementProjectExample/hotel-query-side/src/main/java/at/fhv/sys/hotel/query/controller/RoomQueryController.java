package at.fhv.sys.hotel.query.controller;

import at.fhv.sys.hotel.models.RoomQueryModel;
import at.fhv.sys.hotel.service.RoomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomQueryController {

    @Inject
    RoomService roomService;

    @GET
    @Path("/rooms/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RoomQueryModel getRoomById(@PathParam("roomId") String roomId) {
        return roomService.getRoomById(roomId);
    }

}
