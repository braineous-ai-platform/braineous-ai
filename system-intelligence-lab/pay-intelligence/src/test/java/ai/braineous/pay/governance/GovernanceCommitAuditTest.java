package ai.braineous.pay.governance;

import io.braineous.dd.llm.audit.client.CommitAuditClient;
import io.braineous.dd.llm.cr.model.CommitAuditView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GovernanceCommitAuditTest {

    @Test
    public void test_1() {
        FakeCommitAuditClient client = new FakeCommitAuditClient();
        GovernanceCommitAudit audit = new GovernanceCommitAudit();
        audit.client = client;

        CommitAuditView view = audit.getAudit(" COMMIT-1 ");

        Assertions.assertNotNull(view);
        Assertions.assertEquals("COMMIT-1", client.commitId);
    }

    @Test
    public void test_2() {
        FakeCommitAuditClient client = new FakeCommitAuditClient();
        GovernanceCommitAudit audit = new GovernanceCommitAudit();
        audit.client = client;

        CommitAuditView view = audit.getAudit("   ");

        Assertions.assertNull(view);
        Assertions.assertNull(client.commitId);
    }

    @Test
    public void test_3() {
        GovernanceCommitAudit audit = new GovernanceCommitAudit();
        audit.client = null;

        CommitAuditView view = audit.getAudit("COMMIT-1");

        Assertions.assertNull(view);
    }

    private static class FakeCommitAuditClient implements CommitAuditClient {

        private String commitId;

        public CommitAuditView getAudit(String commitId) {
            this.commitId = commitId;

            CommitAuditView view = new CommitAuditView();
            view.setCommitId(commitId);
            return view;
        }
    }
}