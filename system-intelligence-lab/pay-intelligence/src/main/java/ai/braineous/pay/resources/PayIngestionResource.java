package ai.braineous.pay.resources;

import ai.braineous.pay.ingestion.PayIngestionAgent;
import ai.braineous.rag.prompt.cgo.api.GraphView;
import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/pay/ingest")
public class PayIngestionResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> ingest(String body) {
        Console.log("api.pay.ingest.in", body);

        JsonElement root;
        try {
            root = JsonParser.parseString(body);
        } catch (Exception e) {
            Console.log("api.pay.ingest.error", String.valueOf(e));
            return RestResponse.status(Response.Status.BAD_REQUEST, "{\"error\":\"parse failed\"}");
        }

        if (root == null) {
            return RestResponse.status(Response.Status.BAD_REQUEST, "{\"error\":\"invalid payload\"}");
        }

        if (!root.isJsonObject() && !root.isJsonArray()) {
            return RestResponse.status(Response.Status.BAD_REQUEST, "{\"error\":\"invalid payload\"}");
        }

        PayIngestionAgent agent = new PayIngestionAgent();
        GraphView graph = agent.ingestPayment(root.toString());

        String out;
        if (graph == null) {
            out = "{}";
        } else {
            out = String.valueOf(graph);
        }

        Console.log("api.pay.ingest.out", out);

        return RestResponse.ok(out);
    }
}