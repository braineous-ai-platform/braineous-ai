package ai.braineous.pay.ingestion;

import ai.braineous.cgo.config.CGOSystemConfig;
import ai.braineous.rag.prompt.models.cgo.graph.GraphBuilder;
import ai.braineous.rag.prompt.models.cgo.graph.GraphSnapshot;
import ai.braineous.rag.prompt.observe.Console;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PayIngestionAgentTest {

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
                "[" +
                        "{" +
                        "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"}," +
                        "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"}," +
                        "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"}," +
                        "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"}," +
                        "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}" +
                        "}" +
                        "]";

        Console.log("test.pay.ingest.in", body);

        PayIngestionAgent agent = new PayIngestionAgent();
        GraphSnapshot view = (GraphSnapshot) agent.ingestPayment(body);

        assertNotNull(view);

        Console.log("test.pay.ingest.out.nodes", "" + view.nodes().size());
        Console.log("test.pay.ingest.out.edges", "" + view.edges().size());
        Console.log("test.pay.ingest.out.nodeKeys", "" + view.nodes().keySet());
        Console.log("test.pay.ingest.out.edgeKeys", "" + view.edges().keySet());

        assertTrue(view.nodes().size() > 0);
    }

    @Test
    public void test_2() {
        String body = "[]";

        Console.log("test.pay.ingest.empty.in", body);

        PayIngestionAgent agent = new PayIngestionAgent();
        GraphSnapshot view = (GraphSnapshot) agent.ingestPayment(body);

        assertNotNull(view);

        Console.log("test.pay.ingest.empty.out.nodes", "" + view.nodes().size());
        Console.log("test.pay.ingest.empty.out.edges", "" + view.edges().size());

        assertEquals(0, view.nodes().size());
        assertEquals(0, view.edges().size());
    }

    @Test
    public void test_3() {
        String body =
                "[" +
                        "{" +
                        "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"}" +
                        "}" +
                        "]";

        Console.log("test.pay.ingest.single.in", body);

        PayIngestionAgent agent = new PayIngestionAgent();
        GraphSnapshot view = (GraphSnapshot) agent.ingestPayment(body);

        assertNotNull(view);

        Console.log("test.pay.ingest.single.out.nodes", "" + view.nodes().size());
        Console.log("test.pay.ingest.single.out.edges", "" + view.edges().size());
        Console.log("test.pay.ingest.single.out.nodeKeys", "" + view.nodes().keySet());
        Console.log("test.pay.ingest.single.out.edgeKeys", "" + view.edges().keySet());

        assertTrue(view.nodes().size() > 0);
    }
}