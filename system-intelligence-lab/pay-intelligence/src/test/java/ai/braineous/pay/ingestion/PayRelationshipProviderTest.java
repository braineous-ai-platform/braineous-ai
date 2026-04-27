package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Edge;
import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.Relationship;
import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PayRelationshipProviderTest {

    @Test
    public void test_1() {
        PayFactExtractor extractor = new PayFactExtractor();
        PayRelationshipProvider provider = new PayRelationshipProvider();

        String json = "{"
                + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"},"
                + "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"},"
                + "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"},"
                + "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}"
                + "}";

        List<Fact> facts = extractor.extract(json);
        List<Relationship> relationships = provider.provideRelationships(facts);

        Console.log("test_1_relationship_count", String.valueOf(relationships.size()));
        Console.log("test_1_relationships", relationships.toString());

        Assertions.assertEquals(4, relationships.size());

        assertEdge(relationships.get(0), "PaymentRequest:PAY-1001", "CustomerAccount:CUST-2001");
        assertEdge(relationships.get(1), "PaymentRequest:PAY-1001", "PaymentMethod:PM-3001");
        assertEdge(relationships.get(2), "PaymentRequest:PAY-1001", "RiskProfile:RISK-4001");
        assertEdge(relationships.get(3), "PaymentRequest:PAY-1001", "MerchantPolicy:POL-5001");
    }

    @Test
    public void test_2() {
        PayRelationshipProvider provider = new PayRelationshipProvider();

        List<Relationship> relationships = provider.provideRelationships(null);

        Console.log("test_2_relationship_count", String.valueOf(relationships.size()));

        Assertions.assertEquals(0, relationships.size());
    }

    @Test
    public void test_3() {
        PayRelationshipProvider provider = new PayRelationshipProvider();

        List<Relationship> relationships = provider.provideRelationships(new java.util.ArrayList<Fact>());

        Console.log("test_3_relationship_count", String.valueOf(relationships.size()));

        Assertions.assertEquals(0, relationships.size());
    }

    @Test
    public void test_4() {
        PayRelationshipProvider provider = new PayRelationshipProvider();

        java.util.ArrayList<Fact> facts = new java.util.ArrayList<Fact>();
        facts.add(new Fact("CustomerAccount:CUST-2001",
                "{\"id\":\"CustomerAccount:CUST-2001\",\"kind\":\"CustomerAccount\",\"mode\":\"atomic\"}"));

        List<Relationship> relationships = provider.provideRelationships(facts);

        Console.log("test_4_relationship_count", String.valueOf(relationships.size()));

        Assertions.assertEquals(0, relationships.size());
    }

    @Test
    public void test_5() {
        PayFactExtractor extractor = new PayFactExtractor();
        PayRelationshipProvider provider = new PayRelationshipProvider();

        String json = "{"
                + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"}"
                + "}";

        List<Fact> facts = extractor.extract(json);
        List<Relationship> relationships = provider.provideRelationships(facts);

        Console.log("test_5_relationship_count", String.valueOf(relationships.size()));
        Console.log("test_5_relationships", relationships.toString());

        Assertions.assertEquals(1, relationships.size());
        assertEdge(relationships.get(0), "PaymentRequest:PAY-1001", "CustomerAccount:CUST-2001");
    }

    @Test
    public void test_6() {
        PayRelationshipProvider provider = new PayRelationshipProvider();

        java.util.ArrayList<Fact> facts = new java.util.ArrayList<Fact>();
        facts.add(new Fact("PaymentRequest:PAY-1001",
                "{\"id\":\"PaymentRequest:PAY-1001\",\"kind\":\"PaymentRequest\",\"mode\":\"atomic\"}"));
        facts.add(new Fact("Broken:BROKEN-1",
                "{\"id\":\"Broken:BROKEN-1\",\"mode\":\"atomic\"}"));

        List<Relationship> relationships = provider.provideRelationships(facts);

        Console.log("test_6_relationship_count", String.valueOf(relationships.size()));

        Assertions.assertEquals(0, relationships.size());
    }

    private void assertEdge(Relationship relationship, String expectedFrom, String expectedTo) {
        Assertions.assertNotNull(relationship);

        Edge edge = (Edge) relationship.getEdge();

        Console.log("assert_edge_id", edge.getId());
        Console.log("assert_edge_from", edge.getFromFactId());
        Console.log("assert_edge_to", edge.getToFactId());

        Assertions.assertEquals(expectedFrom, edge.getFromFactId());
        Assertions.assertEquals(expectedTo, edge.getToFactId());

        String expectedEdgeId = "Edge:" + expectedFrom + "->" + expectedTo;
        Assertions.assertEquals(expectedEdgeId, edge.getId());
    }
}