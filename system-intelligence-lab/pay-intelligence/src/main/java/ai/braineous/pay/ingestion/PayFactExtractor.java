package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.FactExtractor;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class PayFactExtractor implements FactExtractor {

    @Override
    public List<Fact> extract(String jsonArrayStr) {
        List<Fact> facts = new ArrayList<>();

        if (jsonArrayStr == null || jsonArrayStr.isBlank()) {
            return facts;
        }

        JsonArray paymentArray;

        try {
            var root = JsonParser.parseString(jsonArrayStr);

            if (root.isJsonArray()) {
                paymentArray = root.getAsJsonArray();
            } else if (root.isJsonObject()
                    && root.getAsJsonObject().has("payments")
                    && root.getAsJsonObject().get("payments").isJsonArray()) {
                paymentArray = root.getAsJsonObject().getAsJsonArray("payments");
            } else {
                return facts;
            }
        } catch (Exception e) {
            return facts;
        }

        for (int i = 0; i < paymentArray.size(); i++) {
            if (!paymentArray.get(i).isJsonObject()) {
                continue;
            }

            JsonObject o = paymentArray.get(i).getAsJsonObject();

            addFact(facts, o, "payment_request", "PaymentRequest", "PaymentRequest");
            addFact(facts, o, "customer_account", "CustomerAccount", "CustomerAccount");
            addFact(facts, o, "payment_method", "PaymentMethod", "PaymentMethod");
            addFact(facts, o, "risk_profile", "RiskProfile", "RiskProfile");
            addFact(facts, o, "merchant_policy", "MerchantPolicy", "MerchantPolicy");
        }

        Console.log("pay.facts.count", "" + facts.size());

        return facts;
    }

    private void addFact(List<Fact> facts,
                         JsonObject root,
                         String fieldName,
                         String kind,
                         String idPrefix) {

        if (!root.has(fieldName)) {
            return;
        }

        if (!root.get(fieldName).isJsonObject()) {
            return;
        }

        JsonObject source = root.getAsJsonObject(fieldName);

        if (!source.has("id")) {
            return;
        }

        String rawId = source.get("id").getAsString();
        String factId = idPrefix + ":" + rawId;

        JsonObject factJson = new JsonObject();
        factJson.addProperty("id", factId);
        factJson.addProperty("kind", kind);
        factJson.addProperty("mode", "atomic");

        for (String key : source.keySet()) {
            if ("id".equals(key)) {
                continue;
            }

            if (source.get(key).isJsonPrimitive()) {
                factJson.add(key, source.get(key));
            }
        }

        Console.log("pay.fact." + fieldName, factJson.toString());

        Fact fact = new Fact(factId, factJson.toString());
        fact.setMode("atomic");
        facts.add(fact);
    }
}