package io.quarkus.workshop.superheroes.hero;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/heroes")
@Tag(name = "heroes")
public class HeroResource {

    Logger logger;

    public HeroResource(Logger logger) {
        this.logger = logger;
    }

    @Operation(summary = "Returns a random hero")
    @GET
    @Path("/random")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, required = true)))
    public Uni<RestResponse<Hero>> getRandomHero() {
        return Hero.findRandom()
                .onItem().ifNotNull().transform(h -> {
                    this.logger.debugf("Found random hero: %s", h);
                    return RestResponse.ok(h);
                })
                .onItem().ifNull().continueWith(() -> {
                    this.logger.debug("No random villain found");
                    return RestResponse.notFound();
                });
    }

    @Operation(summary = "Returns all the heroes from the database")
    @GET
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, type = SchemaType.ARRAY)))
    public Uni<List<Hero>> getAllHeroes() {
        return Hero.listAll();
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
