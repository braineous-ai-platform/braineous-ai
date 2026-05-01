package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Edge;
import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.Relationship;
import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PayRelationshipProviderTest {

    @Test
    public void test_1() {
        Console.log("test.start", "PayRelationshipProvider");

        PayRelationshipProvider provider = new PayRelationshipProvider();
        List<Fact> facts = new ArrayList<>();

        Fact paymentRequest = new Fact(
                "PaymentRequest:PAY-1001",
                "{"
                        + "\"id\":\"PaymentRequest:PAY-1001\","
                        + "\"kind\":\"PaymentRequest\","
                        + "\"mode\":\"atomic\","
                        + "\"amount\":\"125.00\","
                        + "\"currency\":\"USD\""
                        + "}"
        );
        paymentRequest.setMode("atomic");
        facts.add(paymentRequest);

        Fact customerAccount = new Fact(
                "CustomerAccount:CUST-2001",
                "{"
                        + "\"id\":\"CustomerAccount:CUST-2001\","
                        + "\"kind\":\"CustomerAccount\","
                        + "\"mode\":\"atomic\","
                        + "\"status\":\"ACTIVE\""
                        + "}"
        );
        customerAccount.setMode("atomic");
        facts.add(customerAccount);

        Fact paymentMethod = new Fact(
                "PaymentMethod:PM-3001",
                "{"
                        + "\"id\":\"PaymentMethod:PM-3001\","
                        + "\"kind\":\"PaymentMethod\","
                        + "\"mode\":\"atomic\","
                        + "\"type\":\"CARD\""
                        + "}"
        );
        paymentMethod.setMode("atomic");
        facts.add(paymentMethod);

        Fact riskProfile = new Fact(
                "RiskProfile:RISK-4001",
                "{"
                        + "\"id\":\"RiskProfile:RISK-4001\","
                        + "\"kind\":\"RiskProfile\","
                        + "\"mode\":\"atomic\","
                        + "\"level\":\"LOW\""
                        + "}"
        );
        riskProfile.setMode("atomic");
        facts.add(riskProfile);

        Fact merchantPolicy = new Fact(
                "MerchantPolicy:POL-5001",
                "{"
                        + "\"id\":\"MerchantPolicy:POL-5001\","
                        + "\"kind\":\"MerchantPolicy\","
                        + "\"mode\":\"atomic\","
                        + "\"capture\":\"AUTO\""
                        + "}"
        );
        merchantPolicy.setMode("atomic");
        facts.add(merchantPolicy);

        Console.log("test.facts.count", "" + facts.size());

        List<Relationship> rels = provider.provideRelationships(facts);

        Console.log("test.relationships.count", "" + rels.size());

        for (Relationship r : rels) {
            Edge e = (Edge) r.getEdge();
            Console.log("rel", e.getFromFactId() + " -> " + e.getToFactId());
        }

        assertFalse(rels.isEmpty(), "relationships should be generated");
        assertEquals(4, rels.size(), "expected 4 payment_request outbound relationships");

        java.util.Set<String> keys = new java.util.HashSet<>();

        for (Relationship r : rels) {
            Edge e = (Edge) r.getEdge();
            keys.add(e.getFromFactId() + "->" + e.getToFactId());
        }

        assertTrue(keys.contains("PaymentRequest:PAY-1001->CustomerAccount:CUST-2001"));
        assertTrue(keys.contains("PaymentRequest:PAY-1001->PaymentMethod:PM-3001"));
        assertTrue(keys.contains("PaymentRequest:PAY-1001->RiskProfile:RISK-4001"));
        assertTrue(keys.contains("PaymentRequest:PAY-1001->MerchantPolicy:POL-5001"));
    }

    @Test
    public void test_2() {
        Console.log("test.start", "PayRelationshipProvider.noise_and_self");

        PayRelationshipProvider provider = new PayRelationshipProvider();
        List<Fact> facts = new ArrayList<>();

        Fact paymentRequest = new Fact(
                "PaymentRequest:PAY-1001",
                "{"
                        + "\"id\":\"PaymentRequest:PAY-1001\","
                        + "\"kind\":\"PaymentRequest\","
                        + "\"mode\":\"atomic\""
                        + "}"
        );
        paymentRequest.setMode("atomic");
        facts.add(paymentRequest);

        Fact customerAccount = new Fact(
                "CustomerAccount:CUST-2001",
                "{"
                        + "\"id\":\"CustomerAccount:CUST-2001\","
                        + "\"kind\":\"CustomerAccount\","
                        + "\"mode\":\"atomic\","
                        + "\"status\":\"ACTIVE\""
                        + "}"
        );
        customerAccount.setMode("atomic");
        facts.add(customerAccount);

        Fact noise = new Fact(
                "Noise:X1",
                "{"
                        + "\"id\":\"Noise:X1\","
                        + "\"kind\":\"SomethingElse\","
                        + "\"foo\":\"bar\""
                        + "}"
        );
        noise.setMode("atomic");
        facts.add(noise);

        Console.log("test.facts.count", "" + facts.size());

        List<Relationship> rels = provider.provideRelationships(facts);

        Console.log("test.relationships.count", "" + rels.size());

        boolean found = false;

        for (Relationship r : rels) {
            Edge edge = (Edge) r.getEdge();

            Console.log("rel", edge.getFromFactId() + " -> " + edge.getToFactId());

            assertNotEquals(edge.getFromFactId(), edge.getToFactId(), "no self-loop");

            if ("PaymentRequest:PAY-1001".equals(edge.getFromFactId())
                    && "CustomerAccount:CUST-2001".equals(edge.getToFactId())) {
                found = true;
            }
        }

        assertEquals(1, rels.size());
        assertTrue(found, "should contain PaymentRequest:PAY-1001 -> CustomerAccount:CUST-2001");
    }

    @Test
    public void test_3() {
        Console.log("test.start", "PayRelationshipProvider.real_payment_json");

        String body =
                "["
                        + "{"
                        + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                        + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"},"
                        + "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"},"
                        + "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"},"
                        + "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}"
                        + "}"
                        + "]";

        Console.log("test.json.length", "" + body.length());

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.facts.count", "" + facts.size());

        PayRelationshipProvider provider = new PayRelationshipProvider();
        List<Relationship> rels = provider.provideRelationships(facts);

        Console.log("test.relationships.count", "" + rels.size());

        for (Relationship r : rels) {
            Edge edge = (Edge) r.getEdge();
            Console.log("rel", edge.getFromFactId() + " -> " + edge.getToFactId());
        }

        assertEquals(5, facts.size());
        assertEquals(4, rels.size());
    }
}