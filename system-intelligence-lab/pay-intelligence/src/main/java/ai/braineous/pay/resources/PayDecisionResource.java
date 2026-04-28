package ai.braineous.pay.resources;

import ai.braineous.pay.decision.PayDecisionAgent;
import ai.braineous.rag.prompt.cgo.api.QueryExecution;
import ai.braineous.rag.prompt.cgo.api.ValidateTask;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.braineous.dd.llm.query.client.QueryResult;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

@Path("/pay/decision")
public class PayDecisionResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> decide(String requestJson) {
        Console.log("api.pay.decision.in", requestJson);

        JsonObject json;
        try {
            json = JsonParser.parseString(requestJson).getAsJsonObject();
        } catch (Exception e) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "Invalid JSON");
            err.addProperty("details", e.getMessage());
            return RestResponse.status(Status.BAD_REQUEST, err.toString());
        }

        String paymentRequestFactId = readString(json, "paymentRequestFactId");
        if (paymentRequestFactId == null) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "bad_request");
            err.addProperty("details", "paymentRequestFactId required");
            return RestResponse.status(Status.BAD_REQUEST, err.toString());
        }

        String relatedFactIdsCsv = readString(json, "relatedFactIdsCsv");

        QueryExecution<ValidateTask> execution;
        try {
            PayDecisionAgent agent = new PayDecisionAgent();
            QueryResult result = agent.decideCaptureIntent(paymentRequestFactId, relatedFactIdsCsv);

            if (result == null) {
                JsonObject err = new JsonObject();
                err.addProperty("error", "bad_request");
                err.addProperty("details", "decision failed");
                return RestResponse.status(Status.BAD_REQUEST, err.toString());
            }

            execution = (QueryExecution<ValidateTask>) QueryExecution.fromJson(result.getQueryExecutionJson());
        } catch (Exception e) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "Decision orchestration failed");
            err.addProperty("details", e.getMessage());
            return RestResponse.status(Status.INTERNAL_SERVER_ERROR, err.toString());
        }

        JsonObject out = new JsonObject();
        out.addProperty("rawResponse", execution.getRawResponse());
        out.addProperty("promptValidation", String.valueOf(execution.getPromptValidation()));
        out.addProperty("llmResponseValidation", String.valueOf(execution.getLlmResponseValidation()));
        out.addProperty("domainValidation", String.valueOf(execution.getDomainValidation()));

        String response = out.toString();
        Console.log("api.pay.decision.out", response);

        return RestResponse.status(Status.OK, response);
    }

    private String readString(JsonObject json, String name) {
        if (json == null) {
            return null;
        }

        if (!json.has(name)) {
            return null;
        }

        if (json.get(name) == null || json.get(name).isJsonNull()) {
            return null;
        }

        String value = json.get(name).getAsString();
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }
}
