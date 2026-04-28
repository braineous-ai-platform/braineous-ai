package ai.braineous.pay.decision;

import ai.braineous.cgo.llm.OpenAILlmAdapter;
import ai.braineous.rag.prompt.cgo.api.LlmAdapter;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import io.braineous.dd.llm.query.client.QueryClient;
import io.braineous.dd.llm.query.client.QueryResult;
import io.braineous.dd.llm.query.client.RESTClient;

public class PayDecisionAgent {

    private QueryClient queryClient = new RESTClient();
    private LlmAdapter adapter = new OpenAILlmAdapter(new JsonObject());

    PayDecisionAgent(QueryClient queryClient, LlmAdapter adapter) {
        this.queryClient = queryClient;
        this.adapter = adapter;
    }

    public QueryResult decideCaptureIntent(String paymentRequestFactId, String relatedFactIdsCsv) {
        if (paymentRequestFactId == null) {
            Console.log("pay_decision_fact_id", "null");
            return null;
        }

        if (paymentRequestFactId.trim().length() == 0) {
            Console.log("pay_decision_fact_id", "blank");
            return null;
        }

        String safeRelatedFactIds = "";
        if (relatedFactIdsCsv != null) {
            safeRelatedFactIds = relatedFactIdsCsv.trim();
        }

        String sql = buildCaptureIntentSql(paymentRequestFactId.trim(), safeRelatedFactIds);

        Console.log("pay_decision_sql", sql);

        //JsonObject config = new JsonObject();
        //LlmAdapter adapter = new OpenAILlmAdapter(config);

        QueryResult result = this.queryClient.query(this.adapter, sql);

        Console.log("pay_decision_result", String.valueOf(result));

        return result;
    }

    private String buildCaptureIntentSql(String paymentRequestFactId, String relatedFactIdsCsv) {
        StringBuilder sql = new StringBuilder();

        sql.append("select decision, reason, code ");
        sql.append("from llm ");
        sql.append("where factId = '");
        sql.append(paymentRequestFactId);
        sql.append("'");

        if (relatedFactIdsCsv != null && relatedFactIdsCsv.trim().length() > 0) {
            sql.append(" and relatedFactIds = '");
            sql.append(relatedFactIdsCsv.trim());
            sql.append("'");
        }

        sql.append(" control ");
        sql.append("decision_mode = 'capture_intent'");

        return sql.toString();
    }
}
