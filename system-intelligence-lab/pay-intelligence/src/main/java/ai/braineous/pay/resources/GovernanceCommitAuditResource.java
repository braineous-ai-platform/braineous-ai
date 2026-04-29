package ai.braineous.pay.resources;

import ai.braineous.pay.governance.GovernanceCommitAudit;
import com.google.gson.JsonObject;
import io.braineous.dd.llm.cr.model.CommitAuditView;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/pay")
public class GovernanceCommitAuditResource {

    @Inject
    GovernanceCommitAudit audit;

    @GET
    @Path("/governance/commitaudit/{commitId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> getAudit(@PathParam("commitId") String commitId) {

        String cid = safe(commitId);
        if (cid == null) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "bad_request");
            err.addProperty("details", "commitId required");
            return RestResponse.status(Response.Status.BAD_REQUEST, err.toString());
        }

        try {
            CommitAuditView v = audit.getAudit(cid);

            if (v == null) {
                JsonObject err = new JsonObject();
                err.addProperty("error", "bad_request");
                err.addProperty("details", "audit not found");
                return RestResponse.status(Response.Status.BAD_REQUEST, err.toString());
            }

            return RestResponse.ok(v.toJsonString());

        } catch (RuntimeException re) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "system_error");
            err.addProperty("details", safeMsg(re.getMessage(), "system_error"));
            return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, err.toString());
        }
    }

    // -------------------------
    // helpers
    // -------------------------

    private String safe(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return null;
        }
        return t;
    }

    private String safeMsg(String s, String fallback) {
        String t = safe(s);
        if (t == null) {
            return fallback;
        }
        return t;
    }
}