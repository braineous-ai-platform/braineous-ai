package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Edge;
import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.Relationship;
import ai.braineous.rag.prompt.cgo.api.RelationshipProvider;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class PayRelationshipProvider implements RelationshipProvider {

    @Override
    public List<Relationship> provideRelationships(List<Fact> facts) {
        List<Relationship> relationships = new ArrayList<Relationship>();

        if (facts == null) {
            return relationships;
        }

        if (facts.isEmpty()) {
            return relationships;
        }

        Fact hub = findFactByKind(facts, "PaymentRequest");
        if (hub == null) {
            return relationships;
        }

        addRelationship(relationships, hub, findFactByKind(facts, "CustomerAccount"));
        addRelationship(relationships, hub, findFactByKind(facts, "PaymentMethod"));
        addRelationship(relationships, hub, findFactByKind(facts, "RiskProfile"));
        addRelationship(relationships, hub, findFactByKind(facts, "MerchantPolicy"));

        Console.log("pay_relationship_count", String.valueOf(relationships.size()));
        return relationships;
    }

    private void addRelationship(List<Relationship> relationships, Fact hub, Fact spoke) {
        if (relationships == null) {
            return;
        }

        if (hub == null) {
            return;
        }

        if (spoke == null) {
            return;
        }

        if (hub.getId() == null) {
            return;
        }

        if (spoke.getId() == null) {
            return;
        }

        if (hub.getId().equals(spoke.getId())) {
            return;
        }

        String edgeId = "Edge:" + hub.getId() + "->" + spoke.getId();

        Edge edge = new Edge();
        edge.setId(edgeId);
        edge.setFromFactId(hub.getId());
        edge.setToFactId(spoke.getId());

        Fact fromRef = new Fact(hub.getId(), hub.getText());
        Fact toRef = new Fact(spoke.getId(), spoke.getText());

        Relationship relationship = new Relationship(fromRef, toRef, edge);
        relationships.add(relationship);

        Console.log("pay_relationship", edgeId);
    }

    private Fact findFactByKind(List<Fact> facts, String kind) {
        if (facts == null) {
            return null;
        }

        if (kind == null) {
            return null;
        }

        for (int i = 0; i < facts.size(); i++) {
            Fact fact = facts.get(i);
            if (fact == null) {
                continue;
            }

            String factKind = readKind(fact);
            if (kind.equals(factKind)) {
                return fact;
            }
        }

        return null;
    }

    private String readKind(Fact fact) {
        if (fact == null) {
            return null;
        }

        if (fact.getText() == null) {
            return null;
        }

        try {
            JsonObject json = JsonParser.parseString(fact.getText()).getAsJsonObject();
            if (!json.has("kind")) {
                return null;
            }

            return json.get("kind").getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}
