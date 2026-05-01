package ai.braineous.pay.resources;

import ai.braineous.cgo.config.CGOSystemConfig;
import ai.braineous.pay.ingestion.PayIngestionAgent;
import ai.braineous.rag.prompt.cgo.api.GraphView;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class PayDecisionResourceIT {

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
        String paymentJson =
                "{"
                        + "\"payment_request\":{"
                        + "\"id\":\"PAY-1001\","
                        + "\"amount\":\"125.00\","
                        + "\"currency\":\"USD\""
                        + "},"
                        + "\"customer_account\":{"
                        + "\"id\":\"CUST-2001\","
                        + "\"status\":\"ACTIVE\""
                        + "},"
                        + "\"payment_method\":{"
                        + "\"id\":\"PM-3001\","
                        + "\"type\":\"CARD\""
                        + "},"
                        + "\"risk_profile\":{"
                        + "\"id\":\"RISK-4001\","
                        + "\"level\":\"LOW\""
                        + "},"
                        + "\"merchant_policy\":{"
                        + "\"id\":\"POL-5001\","
                        + "\"capture\":\"AUTO\""
                        + "}"
                        + "}";

        Console.log("it.pay.decision.approved.ingest.in", paymentJson);

        PayIngestionAgent ingestionAgent = new PayIngestionAgent();
        GraphView graph = ingestionAgent.ingestPayment(paymentJson);

        Console.log("it.pay.decision.approved.ingest.out", String.valueOf(graph));

        assertNotNull(graph);

        String body =
                "{"
                        + "\"paymentRequestFactId\":\"PaymentRequest:PAY-1001\","
                        + "\"relatedFactIdsCsv\":\"CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001\""
                        + "}";

        Console.log("it.pay.decision.approved.in", body);

        String resp =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("/pay/decision")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        Console.log("it.pay.decision.approved.out", resp);

        assertNotNull(resp);

        assertTrue(resp.contains("rawResponse"), resp);
        assertTrue(resp.contains("promptValidation"), resp);
        assertTrue(resp.contains("llmResponseValidation"), resp);
        assertTrue(resp.contains("domainValidation"), resp);
    }
}