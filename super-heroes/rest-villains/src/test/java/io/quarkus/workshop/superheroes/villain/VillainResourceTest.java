package io.quarkus.workshop.superheroes.villain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Random;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class VillainResourceTest {

    private static final String JSON = "application/json;charset=UTF-8";

    private static final String DEFAULT_NAME = "Super Chocolatine";
    private static final String UPDATED_NAME = "Super Chocolatine (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
    private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
    private static final String DEFAULT_PICTURE = "super_chocolatine.png";
    private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
    private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
    private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;

    private static final int NB_VILLAINS = 570;
    private static String villainId;
    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/villains/hello")
          .then()
             .statusCode(200)
             .body(is("Hello Villain Resource"));
    }

    @Test
    void shouldNotGetUnknownVillain() {
        Long randomId = new Random().nextLong();
        given()
                .pathParam("id", randomId)
                .when().get("/api/villains/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldGetRandomVillain() {
        given()
                .when().get("/api/villains/random")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Villain villain = new Villain();
        villain.name = null;
        villain.otherName = DEFAULT_OTHER_NAME;
        villain.picture = DEFAULT_PICTURE;
        villain.powers = DEFAULT_POWERS;
        villain.level = 0;

        given()
                .body(villain)
                .header(CONTENT_TYPE, JSON)
                .header(ACCEPT, JSON)
                .when()
                .post("/api/villains")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }


}