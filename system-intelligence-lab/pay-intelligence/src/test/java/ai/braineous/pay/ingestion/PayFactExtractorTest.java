package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PayFactExtractorTest {

    @Test
    public void test_1() {
        PayFactExtractor extractor = new PayFactExtractor();

        String json = "{"
                + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"},"
                + "\"payment_method\":{\"id\":\"PM-3001\",\"type\":\"CARD\"},"
                + "\"risk_profile\":{\"id\":\"RISK-4001\",\"level\":\"LOW\"},"
                + "\"merchant_policy\":{\"id\":\"POL-5001\",\"capture\":\"AUTO\"}"
                + "}";

        List<Fact> facts = extractor.extract(json);

        Console.log("test_1_fact_count", String.valueOf(facts.size()));
        Console.log("test_1_facts", facts.toString());

        Assertions.assertEquals(5, facts.size());

        Assertions.assertEquals("PaymentRequest:PAY-1001", facts.get(0).getId());
        Assertions.assertEquals("CustomerAccount:CUST-2001", facts.get(1).getId());
        Assertions.assertEquals("PaymentMethod:PM-3001", facts.get(2).getId());
        Assertions.assertEquals("RiskProfile:RISK-4001", facts.get(3).getId());
        Assertions.assertEquals("MerchantPolicy:POL-5001", facts.get(4).getId());

        Assertions.assertTrue(facts.get(0).getText().contains("\"kind\":\"PaymentRequest\""));
        Assertions.assertTrue(facts.get(0).getText().contains("\"mode\":\"atomic\""));
    }

    @Test
    public void test_2() {
        PayFactExtractor extractor = new PayFactExtractor();

        List<Fact> facts = extractor.extract(null);

        Console.log("test_2_fact_count", String.valueOf(facts.size()));

        Assertions.assertEquals(0, facts.size());
    }

    @Test
    public void test_3() {
        PayFactExtractor extractor = new PayFactExtractor();

        List<Fact> facts = extractor.extract(" ");

        Console.log("test_3_fact_count", String.valueOf(facts.size()));

        Assertions.assertEquals(0, facts.size());
    }

    @Test
    public void test_4() {
        PayFactExtractor extractor = new PayFactExtractor();

        List<Fact> facts = extractor.extract("{bad-json");

        Console.log("test_4_fact_count", String.valueOf(facts.size()));

        Assertions.assertEquals(0, facts.size());
    }

    @Test
    public void test_5() {
        PayFactExtractor extractor = new PayFactExtractor();

        String json = "{"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"}"
                + "}";

        List<Fact> facts = extractor.extract(json);

        Console.log("test_5_fact_count", String.valueOf(facts.size()));

        Assertions.assertEquals(0, facts.size());
    }

    @Test
    public void test_6() {
        PayFactExtractor extractor = new PayFactExtractor();

        String json = "{"
                + "\"payment_request\":{\"id\":\"PAY-1001\",\"amount\":\"125.00\",\"currency\":\"USD\"},"
                + "\"customer_account\":{\"id\":\"CUST-2001\",\"status\":\"ACTIVE\"}"
                + "}";

        List<Fact> facts = extractor.extract(json);

        Console.log("test_6_fact_count", String.valueOf(facts.size()));
        Console.log("test_6_facts", facts.toString());

        Assertions.assertEquals(2, facts.size());
        Assertions.assertEquals("PaymentRequest:PAY-1001", facts.get(0).getId());
        Assertions.assertEquals("CustomerAccount:CUST-2001", facts.get(1).getId());
    }
}