package ai.braineous.pay.governance;

import io.braineous.dd.llm.pg.model.ExecutionView;
import io.braineous.dd.llm.pg.model.PolicyGateResult;
import io.braineous.dd.llm.policygate.client.PolicyGateClient;
import io.braineous.dd.llm.policygate.client.PolicyGateClientImpl;

public class GovernancePolicyGate {

    private PolicyGateClient client;

    public GovernancePolicyGate() {
        this.client = new PolicyGateClientImpl();
    }

    public GovernancePolicyGate(PolicyGateClient client) {
        this.client = client;
    }

    public ExecutionView getExecutions(String queryKind) {
        if (queryKind == null) {
            return null;
        }

        String qk = queryKind.trim();
        if (qk.isEmpty()) {
            return null;
        }

        if (this.client == null) {
            return null;
        }

        return this.client.getExecutions(qk);
    }

    public PolicyGateResult approve(String queryKind, String commitId) {
        if (queryKind == null) {
            return null;
        }

        String qk = queryKind.trim();
        if (qk.isEmpty()) {
            return null;
        }

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

        return this.client.approve(qk, cid);
    }
}