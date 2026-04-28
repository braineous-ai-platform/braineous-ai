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
public class PayIngestionResourceTest {

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
                        + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                        + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"},"
                        + "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"},"
                        + "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"},"
                        + "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}"
                        + "}";

        Console.log("test.pay.ingest.valid.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.ingest.valid.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
        assertTrue(resp.contains("GraphSnapshot"), resp);
    }

    @Test
    public void test_2() {
        String body =
                "["
                        + "{\"payment_request\":{\"id\":\"PAY-2001\",\"amount\":\"50.00\"}},"
                        + "{\"payment_request\":{\"id\":\"PAY-2002\",\"amount\":\"75.00\"}}"
                        + "]";

        Console.log("test.pay.ingest.array.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.ingest.array.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());
        assertTrue(resp.contains("GraphSnapshot"), resp);
    }

    @Test
    public void test_3() {
        String body = "invalid-json";

        Console.log("test.pay.ingest.invalid.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(400)
                        .extract()
                        .asString();

        Console.log("test.pay.ingest.invalid.out", resp);

        assertNotNull(resp);
        assertTrue(resp.contains("parse failed") || resp.contains("invalid payload"), resp);
    }

    @Test
    public void test_4() {
        String body = "[]";

        Console.log("test.pay.ingest.empty.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.ingest.empty.out", resp);

        assertNotNull(resp);
        assertFalse(resp.isBlank());

        assertTrue(resp.contains("GraphSnapshot"), resp);
        assertTrue(resp.contains("nodes={}"), resp);
        assertTrue(resp.contains("edges={}"), resp);
    }

    @Test
    public void test_5() {
        String body =
                "{"
                        + "\"payment_request\":{\"id\":\"PAY-3001\",\"amount\":\"100.00\"}"
                        + "}";

        String r1 =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        String r2 =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/ingest")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("test.pay.ingest.det.out1", r1);
        Console.log("test.pay.ingest.det.out2", r2);

        assertNotNull(r1);
        assertNotNull(r2);
        assertEquals(r1, r2);
    }
}