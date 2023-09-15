package io.quarkus.workshop.superheroes.fight;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;

@Path("/api/fights")
@Produces(APPLICATION_JSON)
public class FightResource {

    @Inject Logger logger;

    @Inject
    FightService service;

    @GET
    @Path("/randomfighters")
    public Response getRandomFighters() {
        Fighters fighters = service.findRandomFighters();
        logger.debug("Get random fighters " + fighters);
        return Response.ok(fighters).build();
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
