package ai.braineous.pay.resources;

import ai.braineous.cgo.config.CGOSystemConfig;
import ai.braineous.pay.ingestion.PayIngestionAgent;
import ai.braineous.rag.prompt.cgo.api.GraphView;
import ai.braineous.rag.prompt.models.cgo.graph.GraphBuilder;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class PayDecisionResourceDriftIT {

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
    public void test_approve_drift() {

        String paymentJson =
                "["
                        + "{"
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
                        + "}"
                        + "]";

        Console.log("it.pay.decision.drift.ingest.in", paymentJson);

        PayIngestionAgent ingestionAgent = new PayIngestionAgent();
        GraphView graph = ingestionAgent.ingestPayment(paymentJson);

        Console.log("it.pay.decision.drift.ingest.out", String.valueOf(graph));

        assertNotNull(graph);

        String body =
                "{"
                        + "\"paymentRequestFactId\":\"PaymentRequest:PAY-1001\","
                        + "\"relatedFactIdsCsv\":\"CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001\""
                        + "}";

        Console.log("it.pay.decision.drift.in", body);

        String previousDecision = null;
        String previousReason = null;
        String previousCode = null;

        for (int i = 0; i < 5; i++) {

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

            Console.log("it.pay.decision.drift.response." + i, resp);

            JsonObject outer =
                    JsonParser.parseString(resp).getAsJsonObject();

            assertTrue(outer.has("rawResponse"), resp);

            String rawResponse =
                    outer.get("rawResponse").getAsString();

            Console.log("it.pay.decision.drift.raw." + i, rawResponse);

            JsonObject inner =
                    JsonParser.parseString(rawResponse).getAsJsonObject();

            assertTrue(inner.has("result"), rawResponse);

            JsonObject result =
                    inner.getAsJsonObject("result");

            assertNotNull(result);

            assertTrue(result.has("decision"), rawResponse);
            assertTrue(result.has("reason"), rawResponse);
            assertTrue(result.has("code"), rawResponse);

            String decision =
                    result.get("decision").getAsString();

            String reason =
                    result.get("reason").getAsString();

            String code =
                    result.get("code").getAsString();

            Console.log("it.pay.decision.drift.result.decision." + i, decision);
            Console.log("it.pay.decision.drift.result.reason." + i, reason);
            Console.log("it.pay.decision.drift.result.code." + i, code);

            assertTrue(decision.length() > 0, rawResponse);
            assertTrue(reason.length() > 0, rawResponse);
            assertTrue(code.length() > 0, rawResponse);

            if (previousDecision != null) {

                Console.log(
                        "it.pay.decision.drift.compare.decision." + i,
                        previousDecision + " -> " + decision);

                Console.log(
                        "it.pay.decision.drift.compare.reason." + i,
                        previousReason + " -> " + reason);

                Console.log(
                        "it.pay.decision.drift.compare.code." + i,
                        previousCode + " -> " + code);
            }

            previousDecision = decision;
            previousReason = reason;
            previousCode = code;
        }
    }

    @Test
    public void test_reject_drift() {

        String paymentJson =
                "["
                        + "{"
                        + "\"payment_request\":{"
                        + "\"id\":\"PAY-2001\","
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
                        + "\"level\":\"HIGH\""
                        + "},"
                        + "\"merchant_policy\":{"
                        + "\"id\":\"POL-5001\","
                        + "\"capture\":\"AUTO\""
                        + "}"
                        + "}"
                        + "]";

        Console.log("it.pay.decision.reject.drift.ingest.in", paymentJson);

        PayIngestionAgent ingestionAgent = new PayIngestionAgent();
        GraphView graph = ingestionAgent.ingestPayment(paymentJson);

        Console.log("it.pay.decision.reject.drift.ingest.out", String.valueOf(graph));

        assertNotNull(graph);

        String body =
                "{"
                        + "\"paymentRequestFactId\":\"PaymentRequest:PAY-2001\","
                        + "\"relatedFactIdsCsv\":\"CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001\""
                        + "}";

        Console.log("it.pay.decision.reject.drift.in", body);

        String previousDecision = null;
        String previousReason = null;
        String previousCode = null;

        for (int i = 0; i < 5; i++) {

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

            Console.log("it.pay.decision.reject.drift.response." + i, resp);

            JsonObject outer =
                    JsonParser.parseString(resp).getAsJsonObject();

            assertTrue(outer.has("rawResponse"), resp);

            String rawResponse =
                    outer.get("rawResponse").getAsString();

            Console.log("it.pay.decision.reject.drift.raw." + i, rawResponse);

            JsonObject inner =
                    JsonParser.parseString(rawResponse).getAsJsonObject();

            assertTrue(inner.has("result"), rawResponse);

            JsonObject result =
                    inner.getAsJsonObject("result");

            assertNotNull(result);

            assertTrue(result.has("decision"), rawResponse);
            assertTrue(result.has("reason"), rawResponse);
            assertTrue(result.has("code"), rawResponse);

            String decision =
                    result.get("decision").getAsString();

            String reason =
                    result.get("reason").getAsString();

            String code =
                    result.get("code").getAsString();

            Console.log("it.pay.decision.reject.drift.result.decision." + i, decision);
            Console.log("it.pay.decision.reject.drift.result.reason." + i, reason);
            Console.log("it.pay.decision.reject.drift.result.code." + i, code);

            assertTrue(decision.length() > 0, rawResponse);
            assertTrue(reason.length() > 0, rawResponse);
            assertTrue(code.length() > 0, rawResponse);

            if (previousDecision != null) {

                Console.log(
                        "it.pay.decision.reject.drift.compare.decision." + i,
                        previousDecision + " -> " + decision);

                Console.log(
                        "it.pay.decision.reject.drift.compare.reason." + i,
                        previousReason + " -> " + reason);

                Console.log(
                        "it.pay.decision.reject.drift.compare.code." + i,
                        previousCode + " -> " + code);
            }

            previousDecision = decision;
            previousReason = reason;
            previousCode = code;
        }
    }
}