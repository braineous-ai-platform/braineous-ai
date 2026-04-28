package ai.braineous.pay.governance;

import io.braineous.dd.llm.audit.client.CommitAuditClient;
import io.braineous.dd.llm.cr.model.CommitAuditView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GovernanceCommitAudit {

    @Inject
    CommitAuditClient client;

    public CommitAuditView getAudit(String commitId) {

        if (commitId == null) {
            return null;
        }

        String cid = commitId.trim();
        if (cid.isEmpty()) {
            return null;
        }

        if (this.client == null) {
            return null;
        }

        return this.client.getAudit(cid);
    }
}