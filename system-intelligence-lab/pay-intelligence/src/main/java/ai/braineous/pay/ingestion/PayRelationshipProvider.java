package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Edge;
import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.Relationship;
import ai.braineous.rag.prompt.cgo.api.RelationshipProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class PayRelationshipProvider implements RelationshipProvider {

    @Override
    public List<Relationship> provideRelationships(List<Fact> facts) {
        if (facts == null || facts.isEmpty()) {
            return List.of();
        }

        Fact paymentRequest = null;
        List<Fact> relatedFacts = new ArrayList<>();

        for (Fact f : facts) {
            if (f == null || f.getId() == null || f.getText() == null) {
                continue;
            }

            JsonObject j;
            try {
                j = JsonParser.parseString(f.getText()).getAsJsonObject();
            } catch (Exception e) {
                continue;
            }

            String kind = "";
            if (j.has("kind")) {
                kind = j.get("kind").getAsString();
            }

            if ("PaymentRequest".equals(kind) && f.getId().startsWith("PaymentRequest:")) {
                paymentRequest = f;
                continue;
            }

            if ("CustomerAccount".equals(kind) && f.getId().startsWith("CustomerAccount:")) {
                relatedFacts.add(f);
                continue;
            }

            if ("PaymentMethod".equals(kind) && f.getId().startsWith("PaymentMethod:")) {
                relatedFacts.add(f);
                continue;
            }

            if ("RiskProfile".equals(kind) && f.getId().startsWith("RiskProfile:")) {
                relatedFacts.add(f);
                continue;
            }

            if ("MerchantPolicy".equals(kind) && f.getId().startsWith("MerchantPolicy:")) {
                relatedFacts.add(f);
            }
        }

        if (paymentRequest == null) {
            return List.of();
        }

        if (relatedFacts.isEmpty()) {
            return List.of();
        }

        List<Relationship> rels = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();

        for (Fact relatedFact : relatedFacts) {
            if (relatedFact == null || relatedFact.getId() == null) {
                continue;
            }

            String fromId = paymentRequest.getId();
            String toId = relatedFact.getId();

            if (fromId.equals(toId)) {
                continue;
            }

            String key = fromId + "->" + toId;
            if (!seen.add(key)) {
                continue;
            }

            Edge edge = new Edge();
            edge.setId("Edge:" + key);
            edge.setFromFactId(fromId);
            edge.setToFactId(toId);

            Fact fromRef = new Fact(fromId, paymentRequest.getText());
            Fact toRef = new Fact(toId, relatedFact.getText());

            rels.add(new Relationship(fromRef, toRef, edge));
        }

        return List.copyOf(rels);
    }
}