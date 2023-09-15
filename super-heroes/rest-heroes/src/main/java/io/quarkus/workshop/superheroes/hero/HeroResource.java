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

    @Operation(summary = "Returns a hero for a given identifier")
    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class)))
    @APIResponse(responseCode = "204", description = "The hero is not found for a given identifier")
    public Uni<RestResponse<Hero>> getHero(@RestPath Long id) {
        return Hero.<Hero>findById(id)
                .map(hero -> {
                    if (hero != null) {
                        return RestResponse.ok(hero);
                    }
                    logger.debugf("No Hero found with id %d", id);
                    return RestResponse.noContent();
                });
    }

    @Operation(summary = "Creates a valid hero")
    @POST
    @APIResponse(responseCode = "201", description = "The URI of the created hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @WithTransaction
    public Uni<RestResponse<URI>> createHero(@Valid Hero hero, @Context UriInfo uriInfo) {
        return hero.<Hero>persist()
                .map(h -> {
                    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(h.id));
                    logger.debug("New Hero created with URI " + builder.build().toString());
                    return RestResponse.created(builder.build());
                });
    }

    @Operation(summary = "Updates an exiting hero")
    @PUT
    @APIResponse(responseCode = "200", description = "The updated hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class)))
    @WithTransaction
    public Uni<Hero> updateHero(@Valid Hero hero) {
        return Hero.<Hero>findById(hero.id)
                .map(retrieved -> {
                    retrieved.name = hero.name;
                    retrieved.otherName = hero.otherName;
                    retrieved.level = hero.level;
                    retrieved.picture = hero.picture;
                    retrieved.powers = hero.powers;
                    return retrieved;
                })
                .map(h -> {
                    logger.debugf("Hero updated with new valued %s", h);
                    return h;
                });

    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
