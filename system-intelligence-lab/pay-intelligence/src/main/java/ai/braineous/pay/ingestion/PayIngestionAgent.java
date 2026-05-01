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
            String payload = paymentJson;

            if (payload != null) {
                payload = payload.trim();
            }

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

            return this.llmBridge.submit(context);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}