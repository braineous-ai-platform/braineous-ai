package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PayFactExtractorTest {

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

        Console.log("test.pay.extract.in", body);

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.pay.extract.out.count", "" + facts.size());

        for (Fact fact : facts) {
            Console.log("test.pay.extract.fact", fact.getId() + " :: " + fact.getText());
        }

        assertEquals(5, facts.size());

        assertContainsFact(facts, "PaymentRequest:PAY-1001");
        assertContainsFact(facts, "CustomerAccount:CUST-2001");
        assertContainsFact(facts, "PaymentMethod:PM-3001");
        assertContainsFact(facts, "RiskProfile:RISK-4001");
        assertContainsFact(facts, "MerchantPolicy:POL-5001");
    }

    @Test
    public void test_2() {
        String body = "[]";

        Console.log("test.pay.extract.empty.in", body);

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.pay.extract.empty.out.count", "" + facts.size());

        assertEquals(0, facts.size());
    }

    @Test
    public void test_3() {
        String body =
                "[" +
                        "{" +
                        "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"}" +
                        "}" +
                        "]";

        Console.log("test.pay.extract.single.in", body);

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.pay.extract.single.out.count", "" + facts.size());

        for (Fact fact : facts) {
            Console.log("test.pay.extract.single.fact", fact.getId() + " :: " + fact.getText());
        }

        assertEquals(1, facts.size());
        assertContainsFact(facts, "PaymentRequest:PAY-1001");
    }

    @Test
    public void test_4() {
        String body =
                "[" +
                        "{" +
                        "\"payment_request\":{\"amount\":\"125.00\",\"currency\":\"USD\"}," +
                        "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"}" +
                        "}" +
                        "]";

        Console.log("test.pay.extract.missing_id.in", body);

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.pay.extract.missing_id.out.count", "" + facts.size());

        for (Fact fact : facts) {
            Console.log("test.pay.extract.missing_id.fact", fact.getId() + " :: " + fact.getText());
        }

        assertEquals(1, facts.size());
        assertContainsFact(facts, "CustomerAccount:CUST-2001");
        assertNotContainsFact(facts, "PaymentRequest:PAY-1001");
    }

    @Test
    public void test_5() {
        String body =
                "[" +
                        "{" +
                        "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"}" +
                        "}," +
                        "\"NOT_JSON_OBJECT\"" +
                        "]";

        Console.log("test.pay.extract.malformed.in", body);

        PayFactExtractor extractor = new PayFactExtractor();
        List<Fact> facts = extractor.extract(body);

        Console.log("test.pay.extract.malformed.out.count", "" + facts.size());

        for (Fact fact : facts) {
            Console.log("test.pay.extract.malformed.fact", fact.getId() + " :: " + fact.getText());
        }

        assertEquals(1, facts.size());
        assertContainsFact(facts, "PaymentRequest:PAY-1001");
    }

    @Test
    public void test_6() {
        PayFactExtractor extractor = new PayFactExtractor();

        List<Fact> nullFacts = extractor.extract(null);
        List<Fact> blankFacts = extractor.extract("");

        Console.log("test.pay.extract.null.count", "" + nullFacts.size());
        Console.log("test.pay.extract.blank.count", "" + blankFacts.size());

        assertEquals(0, nullFacts.size());
        assertEquals(0, blankFacts.size());
    }

    private void assertContainsFact(List<Fact> facts, String factId) {
        boolean found = false;

        for (Fact fact : facts) {
            if (factId.equals(fact.getId())) {
                found = true;
            }
        }

        assertTrue(found, "missing fact " + factId);
    }

    private void assertNotContainsFact(List<Fact> facts, String factId) {
        boolean found = false;

        for (Fact fact : facts) {
            if (factId.equals(fact.getId())) {
                found = true;
            }
        }

        assertFalse(found, "unexpected fact " + factId);
    }
}