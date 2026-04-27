package ai.braineous.pay.ingestion;

import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.cgo.api.FactExtractor;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class PayFactExtractor implements FactExtractor {

    @Override
    public List<Fact> extract(String jsonStr) {
        List<Fact> facts = new ArrayList<Fact>();

        if (jsonStr == null) {
            return facts;
        }

        if (jsonStr.trim().length() == 0) {
            return facts;
        }

        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseString(jsonStr);
            if (!parsed.isJsonObject()) {
                return facts;
            }
            root = parsed.getAsJsonObject();
        } catch (Exception e) {
            return facts;
        }

        if (!root.has("payment_request")) {
            return facts;
        }

        JsonObject paymentRequest = readObject(root, "payment_request");
        if (paymentRequest == null) {
            return facts;
        }

        String paymentRequestId = readString(paymentRequest, "id");
        if (paymentRequestId == null) {
            return facts;
        }

        facts.add(buildFact("PaymentRequest", paymentRequestId, paymentRequest));

        addSpokeFact(facts, root, "customer_account", "CustomerAccount");
        addSpokeFact(facts, root, "payment_method", "PaymentMethod");
        addSpokeFact(facts, root, "risk_profile", "RiskProfile");
        addSpokeFact(facts, root, "merchant_policy", "MerchantPolicy");

        Console.log("pay_fact_count", String.valueOf(facts.size()));
        return facts;
    }

    private void addSpokeFact(List<Fact> facts, JsonObject root, String jsonKey, String kind) {
        JsonObject spoke = readObject(root, jsonKey);
        if (spoke == null) {
            return;
        }

        String id = readString(spoke, "id");
        if (id == null) {
            return;
        }

        facts.add(buildFact(kind, id, spoke));
    }

    private Fact buildFact(String kind, String rawId, JsonObject source) {
        String factId = kind + ":" + rawId;

        JsonObject factJson = new JsonObject();
        factJson.addProperty("id", factId);
        factJson.addProperty("kind", kind);
        factJson.addProperty("mode", "atomic");

        copyFields(source, factJson);

        Console.log("pay_fact", factJson.toString());

        Fact fact = new Fact(factId, factJson.toString());
        fact.setMode("atomic");
        return fact;
    }

    private void copyFields(JsonObject source, JsonObject target) {
        java.util.Set<java.util.Map.Entry<String, JsonElement>> entries = source.entrySet();

        for (java.util.Map.Entry<String, JsonElement> entry : entries) {
            if ("id".equals(entry.getKey())) {
                continue;
            }

            target.add(entry.getKey(), entry.getValue());
        }
    }

    private JsonObject readObject(JsonObject root, String key) {
        if (root == null) {
            return null;
        }

        if (key == null) {
            return null;
        }

        if (!root.has(key)) {
            return null;
        }

        try {
            JsonElement value = root.get(key);
            if (!value.isJsonObject()) {
                return null;
            }

            return value.getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    private String readString(JsonObject object, String key) {
        if (object == null) {
            return null;
        }

        if (key == null) {
            return null;
        }

        if (!object.has(key)) {
            return null;
        }

        try {
            JsonElement value = object.get(key);
            if (value == null) {
                return null;
            }

            if (value.isJsonNull()) {
                return null;
            }

            String text = value.getAsString();
            if (text == null) {
                return null;
            }

            if (text.trim().length() == 0) {
                return null;
            }

            return text;
        } catch (Exception e) {
            return null;
        }
    }
}