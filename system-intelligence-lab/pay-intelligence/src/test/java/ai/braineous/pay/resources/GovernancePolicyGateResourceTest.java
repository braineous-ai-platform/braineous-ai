package ai.braineous.pay.resources;

import ai.braineous.rag.prompt.observe.Console;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class GovernancePolicyGateResourceTest {

    @Test
    public void test_1() {
        String resp =
                given()
                        .when()
                        .get("/pay/governance/policygate/executions/payment_decision")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.executions.valid.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
        assertTrue(resp.contains("payment_decision"), resp);
    }

    @Test
    public void test_2() {
        String resp =
                given()
                        .when()
                        .get("/pay/governance/policygate/executions/%20%20%20")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.executions.encoded_spaces.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
        assertTrue(resp.contains("%20%20%20"), resp);
        assertTrue(resp.contains("executions"), resp);
    }

    @Test
    public void test_3() {
        String body =
                "{"
                        + "\"queryKind\":\"payment_decision\","
                        + "\"commitId\":\"COMMIT-DOES-NOT-EXIST\""
                        + "}";

        Console.log("test.pay.governance.approve.invalid_commit.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/governance/policygate/commit/approve")
                        .then()
                        .statusCode(422)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.approve.invalid_commit.out", resp);

        assertNotNull(resp);
    }

    @Test
    public void test_4() {
        String body =
                "{"
                        + "\"commitId\":\"COMMIT-1001\""
                        + "}";

        Console.log("test.pay.governance.approve.missing_query_kind.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/governance/policygate/commit/approve")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.approve.missing_query_kind.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("bad_request"), resp);
        assertTrue(resp.contains("queryKind required"), resp);
    }

    @Test
    public void test_5() {
        String body =
                "{"
                        + "\"queryKind\":\"payment_decision\""
                        + "}";

        Console.log("test.pay.governance.approve.missing_commit_id.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/governance/policygate/commit/approve")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.approve.missing_commit_id.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("bad_request"), resp);
        assertTrue(resp.contains("commitId required"), resp);
    }

    @Test
    public void test_6() {
        String body = "";

        Console.log("test.pay.governance.approve.empty_body.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/governance/policygate/commit/approve")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.governance.approve.empty_body.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
    }
}