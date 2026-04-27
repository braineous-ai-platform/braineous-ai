package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.GraphView;
import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PayIngestionAgentTest {

    @Test
    public void test_1() {
        PayIngestionAgent agent = new PayIngestionAgent();

        String json = "{"
                + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"},"
                + "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"},"
                + "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"},"
                + "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}"
                + "}";

        GraphView graphView = agent.ingestPayment(json);

        Console.log("test_1_graph_view", String.valueOf(graphView));

        Assertions.assertNotNull(graphView);
    }

    @Test
    public void test_2() {
        PayIngestionAgent agent = new PayIngestionAgent();

        GraphView graphView = agent.ingestPayment(null);

        Console.log("test_2_graph_view", String.valueOf(graphView));

        Assertions.assertNull(graphView);
    }

    @Test
    public void test_3() {
        PayIngestionAgent agent = new PayIngestionAgent();

        GraphView graphView = agent.ingestPayment(" ");

        Console.log("test_3_graph_view", String.valueOf(graphView));

        Assertions.assertNull(graphView);
    }
}