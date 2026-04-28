package ai.braineous.pay.decision;

import ai.braineous.rag.prompt.cgo.api.LlmAdapter;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import io.braineous.dd.llm.query.client.QueryClient;
import io.braineous.dd.llm.query.client.QueryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PayDecisionAgentTest {

    @Test
    public void test_1() {
        CapturingQueryClient queryClient = new CapturingQueryClient();
        LlmAdapter adapter = new FakeLlmAdapter();

        PayDecisionAgent agent = new PayDecisionAgent(queryClient, adapter);

        QueryResult result = agent.decideCaptureIntent(
                "PaymentRequest:PAY-1001",
                "CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001"
        );

        Console.log("test_1_sql", queryClient.sql);
        Console.log("test_1_result", String.valueOf(result));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(adapter, queryClient.adapter);

        Assertions.assertTrue(queryClient.sql.contains("select decision, reason, code"));
        Assertions.assertTrue(queryClient.sql.contains("from llm"));
        Assertions.assertTrue(queryClient.sql.contains("factId = 'PaymentRequest:PAY-1001'"));
        Assertions.assertTrue(queryClient.sql.contains("relatedFactIds = 'CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001'"));
        Assertions.assertTrue(queryClient.sql.contains("decision_mode = 'capture_intent'"));
    }

    @Test
    public void test_2() {
        CapturingQueryClient queryClient = new CapturingQueryClient();
        LlmAdapter adapter = new FakeLlmAdapter();

        PayDecisionAgent agent = new PayDecisionAgent(queryClient, adapter);

        QueryResult result = agent.decideCaptureIntent(null, "CustomerAccount:CUST-2001");

        Console.log("test_2_result", String.valueOf(result));

        Assertions.assertNull(result);
        Assertions.assertNull(queryClient.sql);
    }

    @Test
    public void test_3() {
        CapturingQueryClient queryClient = new CapturingQueryClient();
        LlmAdapter adapter = new FakeLlmAdapter();

        PayDecisionAgent agent = new PayDecisionAgent(queryClient, adapter);

        QueryResult result = agent.decideCaptureIntent("   ", "CustomerAccount:CUST-2001");

        Console.log("test_3_result", String.valueOf(result));

        Assertions.assertNull(result);
        Assertions.assertNull(queryClient.sql);
    }

    @Test
    public void test_4() {
        CapturingQueryClient queryClient = new CapturingQueryClient();
        LlmAdapter adapter = new FakeLlmAdapter();

        PayDecisionAgent agent = new PayDecisionAgent(queryClient, adapter);

        QueryResult result = agent.decideCaptureIntent("PaymentRequest:PAY-1001", null);

        Console.log("test_4_sql", queryClient.sql);
        Console.log("test_4_result", String.valueOf(result));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(queryClient.sql.contains("relatedFactIds"));
        Assertions.assertTrue(queryClient.sql.contains("factId = 'PaymentRequest:PAY-1001'"));
        Assertions.assertTrue(queryClient.sql.contains("decision_mode = 'capture_intent'"));
    }

    private static class CapturingQueryClient implements QueryClient {

        private LlmAdapter adapter;
        private String sql;

        @Override
        public QueryResult query(LlmAdapter adapter, String sql) {
            this.adapter = adapter;
            this.sql = sql;

            JsonObject request = new JsonObject();
            request.addProperty("captured", "true");

            JsonObject execution = new JsonObject();
            execution.addProperty("status", "CAPTURED");

            return QueryResult.ok(request, execution);
        }

        @Override
        public QueryResult query(LlmAdapter adapter,
                                 String queryKind,
                                 String query,
                                 String fact,
                                 List<String> relatedFacts) {
            return null;
        }
    }

    private static class FakeLlmAdapter extends LlmAdapter {

        @Override
        public String invokeLlm(JsonObject prompt) {
            return "{}";
        }
    }
}