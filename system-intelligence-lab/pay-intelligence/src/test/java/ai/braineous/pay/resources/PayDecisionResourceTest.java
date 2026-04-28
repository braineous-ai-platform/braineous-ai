package ai.braineous.pay.resources;

import ai.braineous.cgo.config.CGOSystemConfig;
import ai.braineous.rag.prompt.models.cgo.graph.GraphBuilder;
import ai.braineous.rag.prompt.observe.Console;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PayDecisionResourceTest {

    private MongoClient testClient;

    @BeforeEach
    public void setup() {

        GraphBuilder.getInstance().clear();

        String uri = CGOSystemConfig.resolveMongoDBUri();
        this.testClient = MongoClients.create(uri);

        MongoDatabase db = testClient.getDatabase("cgo");
        db.getCollection("cgo_nodes").deleteMany(new Document());
        db.getCollection("cgo_edges").deleteMany(new Document());
    }

    @AfterEach
    public void tearDown() {
        if (this.testClient != null) {
            this.testClient.close();
        }
    }

    @Test
    public void test_1() {
        String body =
                "{"
                        + "\"paymentRequestFactId\":\"payment_request:PAY-1001\","
                        + "\"relatedFactIdsCsv\":\"customer_account:CUST-2001,payment_method:PM-3001\""
                        + "}";

        Console.log("test.pay.decision.valid.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.decision.valid.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("bad_request"), resp);
        assertTrue(resp.contains("decision failed"), resp);
    }

    @Test
    public void test_2() {
        String body = "{ }";

        Console.log("test.pay.decision.missing.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.decision.missing.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("paymentRequestFactId required"), resp);
    }

    @Test
    public void test_3() {
        String body = "invalid-json";

        Console.log("test.pay.decision.invalid.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.decision.invalid.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("Invalid JSON"), resp);
    }

    @Test
    public void test_4() {
        String body =
                "{"
                        + "\"paymentRequestFactId\":\"payment_request:PAY-2001\""
                        + "}";

        String r1 =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        String r2 =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.decision.det.out1", r1);
        Console.log("test.pay.decision.det.out2", r2);

        assertNotNull(r1);
        assertNotNull(r2);

        assertTrue(r1.contains("bad_request"), r1);
        assertTrue(r1.contains("decision failed"), r1);

        assertTrue(r2.contains("bad_request"), r2);
        assertTrue(r2.contains("decision failed"), r2);

        assertEquals(r1, r2);
    }
}