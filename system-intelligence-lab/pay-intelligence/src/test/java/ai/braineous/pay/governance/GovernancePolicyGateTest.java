package ai.braineous.pay.governance;

import io.braineous.dd.llm.pg.model.ExecutionView;
import io.braineous.dd.llm.pg.model.PolicyGateResult;
import io.braineous.dd.llm.policygate.client.PolicyGateClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GovernancePolicyGateTest {

    @Test
    public void test_1() {
        FakePolicyGateClient client = new FakePolicyGateClient();
        GovernancePolicyGate gate = new GovernancePolicyGate(client);

        ExecutionView view = gate.getExecutions(" payment_decision ");

        Assertions.assertNotNull(view);
        Assertions.assertEquals("payment_decision", client.queryKind);
    }

    @Test
    public void test_2() {
        FakePolicyGateClient client = new FakePolicyGateClient();
        GovernancePolicyGate gate = new GovernancePolicyGate(client);

        PolicyGateResult result = gate.approve(" payment_decision ", " COMMIT-1 ");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("payment_decision", client.queryKind);
        Assertions.assertEquals("COMMIT-1", client.commitId);
    }

    @Test
    public void test_3() {
        FakePolicyGateClient client = new FakePolicyGateClient();
        GovernancePolicyGate gate = new GovernancePolicyGate(client);

        ExecutionView view = gate.getExecutions("   ");

        Assertions.assertNull(view);
        Assertions.assertNull(client.queryKind);
    }

    @Test
    public void test_4() {
        FakePolicyGateClient client = new FakePolicyGateClient();
        GovernancePolicyGate gate = new GovernancePolicyGate(client);

        PolicyGateResult result = gate.approve("payment_decision", "   ");

        Assertions.assertNull(result);
        Assertions.assertNull(client.queryKind);
        Assertions.assertNull(client.commitId);
    }

    @Test
    public void test_5() {
        GovernancePolicyGate gate = new GovernancePolicyGate(null);

        ExecutionView view = gate.getExecutions("payment_decision");

        Assertions.assertNull(view);
    }

    private static class FakePolicyGateClient implements PolicyGateClient {

        private String queryKind;
        private String commitId;

        public ExecutionView getExecutions(String queryKind) {
            this.queryKind = queryKind;

            ExecutionView view = new ExecutionView();
            view.setQueryKind(queryKind);
            return view;
        }

        public PolicyGateResult approve(String queryKind, String commitId) {
            this.queryKind = queryKind;
            this.commitId = commitId;

            PolicyGateResult result = new PolicyGateResult();
            result.setOk(true);
            return result;
        }
    }
}