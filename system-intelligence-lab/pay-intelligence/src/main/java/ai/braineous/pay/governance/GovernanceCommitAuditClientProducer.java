package ai.braineous.pay.governance;

import io.braineous.dd.llm.audit.client.CommitAuditClient;
import io.braineous.dd.llm.audit.client.CommitAuditClientImpl;
import io.braineous.dd.llm.cr.services.CommitAuditService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class GovernanceCommitAuditClientProducer {

    @Inject
    CommitAuditService svc;

    @Produces
    @ApplicationScoped
    public CommitAuditClient commitAuditClient() {
        return new CommitAuditClientImpl(svc);
    }
}