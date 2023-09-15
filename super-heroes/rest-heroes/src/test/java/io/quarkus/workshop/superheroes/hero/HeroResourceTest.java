package io.quarkus.workshop.superheroes.hero;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HeroResourceTest {

    private static final String JSON = "application/json;charset=UTF-8";

    private static final String DEFAULT_NAME = "Super Baguette";
    private static final String UPDATED_NAME = "Super Baguette (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Baguette Tradition";
    private static final String UPDATED_OTHER_NAME = "Super Baguette Tradition (updated)";
    private static final String DEFAULT_PICTURE = "super_baguette.png";
    private static final String UPDATED_PICTURE = "super_baguette_updated.png";
    private static final String DEFAULT_POWERS = "eats baguette really quickly";
    private static final String UPDATED_POWERS = "eats baguette really quickly (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;

    private static final int NB_HEROES = 941;
    private static String heroId;

    @Test
    void shouldPingOpenAPI() {
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .when().get("/q/openapi")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/api/heroes/hello")
                .then()
                .statusCode(200)
                .body(is("Hello Hero Resource"));
    }
    @Test
    void shouldNotGetUnknownHero() {
        Long randomId = new Random().nextLong();
        given()
                .pathParam("id", randomId)
                .when().get("/api/heroes/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldGetRandomHero() {
        given()
                .when().get("/api/heroes/random")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Hero hero = new Hero();
        hero.name = null;
        hero.otherName = DEFAULT_OTHER_NAME;
        hero.picture = DEFAULT_PICTURE;
        hero.powers = DEFAULT_POWERS;
        hero.level = 0;

        given()
                .body(hero)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post("/api/heroes")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Hero> heroes = get("/api/heroes").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getHeroTypeRef());
        assertEquals(NB_HEROES, heroes.size());
    }
}