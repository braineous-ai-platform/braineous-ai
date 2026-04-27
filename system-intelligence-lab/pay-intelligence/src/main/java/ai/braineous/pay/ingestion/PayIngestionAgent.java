package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.FactExtractor;
import ai.braineous.rag.prompt.cgo.api.GraphView;
import ai.braineous.rag.prompt.cgo.api.LLMBridge;
import ai.braineous.rag.prompt.cgo.api.LLMContext;
import ai.braineous.rag.prompt.cgo.api.RelationshipProvider;
import ai.braineous.rag.prompt.observe.Console;
import ai.braineous.rag.prompt.services.cgo.causal.CausalLLMBridge;

public class PayIngestionAgent {

    private LLMBridge llmBridge = new CausalLLMBridge();

    public GraphView ingestPayment(String paymentJson) {
        try {
            if (paymentJson == null) {
                Console.log("pay_ingest_input", "null");
                return null;
            }

            if (paymentJson.trim().length() == 0) {
                Console.log("pay_ingest_input", "blank");
                return null;
            }

            Console.log("pay_ingest_input", paymentJson);

            String payload = wrapAsArray(paymentJson);
            Console.log("pay_ingest_payload", payload);

            LLMContext context = new LLMContext();

            FactExtractor factExtractor = new PayFactExtractor();
            RelationshipProvider relationshipProvider = new PayRelationshipProvider();

            context.build(
                    "payment_request",
                    payload,
                    factExtractor,
                    relationshipProvider,
                    null
            );

            GraphView graphView = this.llmBridge.submit(context);

            Console.log("pay_ingest_status", "submitted");

            return graphView;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String wrapAsArray(String json) {
        String trimmed = json.trim();

        if (trimmed.startsWith("[")) {
            return json;
        }

        return "[" + json + "]";
    }
}