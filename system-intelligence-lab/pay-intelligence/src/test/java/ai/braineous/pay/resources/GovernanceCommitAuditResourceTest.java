package ai.braineous.pay.resources;

import ai.braineous.rag.prompt.observe.Console;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class GovernanceCommitAuditResourceTest {

    @Test
    public void test_1() {
        String resp =
                given()
                        .when()
                        .get("/pay/governance/commitaudit/COMMIT-1001")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.audit.valid_like.out", resp);

        assertNotNull(resp);
    }

    @Test
    public void test_2() {
        String resp =
                given()
                        .when()
                        .get("/pay/governance/commitaudit/%20%20%20")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.audit.encoded_spaces.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
        assertTrue(resp.contains("bad_request"), resp);
        assertTrue(resp.contains("audit not found"), resp);
    }

    @Test
    public void test_3() {
        String resp =
                String.valueOf(given()
                        .when()
                        .get("/pay/governance/commitaudit/")
                        .then()
                        .statusCode(404)); // path not matched

        // no extract needed
    }

    @Test
    public void test_4() {
        String resp =
                given()
                        .when()
                        .get("/pay/governance/commitaudit/UNKNOWN-COMMIT")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.audit.unknown.out", resp);

        assertNotNull(resp);
    }
}
