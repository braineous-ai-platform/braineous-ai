![LLMDD Architecture](parallax-image.jpg)

BraineousAI is a runtime for building deterministic AI applications that are safe for production.

It provides a unified stack for context shaping, controlled LLM execution, and governed outcomes — without the unpredictability of prompt-driven systems.

Run the system, execute a declarative query, and observe deterministic results end-to-end.

No AI frameworks. No prompt engineering gymnastics. Just a clean application runtime for building with LLMs.

Get started in minutes and experience a predictable, production-ready AI workflow.

## The Problem with AI Applications Today

Building AI applications for production today often feels unstable.

LLMs are inherently non-deterministic — the same input can produce different outputs across runs. Because of this, behavior in staging rarely matches production, making reliability difficult to achieve.

Most current approaches rely on prompts as the primary logic layer. This introduces implicit behavior that is hard to test, validate, and debug. As systems grow, pipelines become fragile and difficult to reason about.

To compensate, developers resort to prompt tuning, retries, and post-hoc validation. These techniques attempt to manage unpredictability after the fact, rather than controlling it at the system level.

The result is a fundamental gap: powerful models, but no consistent way to build reliable, production-grade applications around them.

## A Different Approach: Deterministic AI Runtime

LLMs are inherently non-deterministic. This is not a limitation — it is what gives them their power. However, it also means that drift affects not just production systems, but staging environments, test suites, and CI/CD pipelines.

Instead of trying to eliminate this non-determinism, BraineousAI takes a different approach.

LLMs are treated as bounded components within a deterministic runtime.

The runtime controls how context is constructed, how queries are executed, and how outputs are validated before they are returned. This ensures that the system behaves predictably, even when the underlying model does not.

In this model, output structure is deterministic, while values are allowed to vary within controlled, safe bounds.

The result is a system where non-determinism is contained, and application behavior remains stable and reliable.

## Developer Experience

BraineousAI is designed to give developers a complete local experience across three core phases: ingestion, query, and governance.

The full runtime can be brought up in a containerized environment, allowing developers to work with the system end-to-end without first assembling a complex AI stack.

Instead of iterating on prompts, tuning probabilistic context through retrieval pipelines, or managing vector search behavior by hand, developers interact with the system through a declarative query interface.

Each part of the query contributes to a controlled execution path inside the runtime, making application behavior easier to understand and refine.

The first success moment is simple and concrete: ingest a fact and relationship graph, execute a T-shirt SQL query, inspect the structured result, and decide whether the use case is handled or the query should be refined.

This makes AI application development feel less like prompt experimentation and more like building against a real system.

## What Makes BraineousAI Different

Most current approaches to building AI applications focus on composing prompts, chaining model calls, and tuning retrieval pipelines. While these techniques can improve results, they still rely on managing non-deterministic behavior after the fact.

BraineousAI takes a different approach.

Instead of treating prompts as the primary interface, it introduces a runtime that controls how AI is used within an application.

In BraineousAI:

- context is shaped before execution
- queries express intent declaratively
- execution is bounded and structured
- outputs are validated before they are returned

This shifts the model from:

> prompt → hope → parse

to:

> context → query → controlled execution → governed output

The result is not just better prompts, but a fundamentally different way to build AI applications — one where behavior is predictable, testable, and suitable for production systems.

## Production Readiness

BraineousAI is designed for environments where reliability matters.

While LLMs remain non-deterministic, the runtime ensures that application behavior stays controlled and predictable.

Outputs follow a deterministic structure, allowing downstream systems to rely on consistent contracts. At the same time, value-level variation is managed within safe and expected bounds.

All results are validated as part of execution, ensuring that only acceptable outputs are returned.

This approach makes it possible to build AI applications that are not only powerful, but also stable, testable, and suitable for real-world production use.

## Try It in 15 Minutes

BraineousAI is designed to give you a complete, end-to-end experience in minutes.

The goal is simple: bring up the runtime, execute a query, and observe deterministic behavior across the system.

### What You’ll Do

- Start the BraineousAI runtime locally
- Load a sample application context
- Execute a declarative query
- Inspect the structured result
- Refine the query and observe controlled changes in output

### What You’ll See

- A running system where all components are already wired together
- Deterministic output structure, even as values vary
- Clear, inspectable results that reflect your query intent
- A development loop that feels predictable and easy to reason about


## Try It Locally

The `pay-intelligence` extension is designed to provide a complete local runtime experience across the full intelligence lifecycle.

In a few minutes, you can:

- ingest operational payment context
- execute runtime payment decisions
- inspect governance behavior
- observe bounded runtime drift across repeated executions

### Clone the Repository

```bash
git clone https://github.com/braineous-ai-platform/system-intelligence-lab.git
```

```bash
cd system-intelligence-lab/pay-intelligence
```

### Start the Runtime

Run the Quarkus application locally:

```bash
mvn quarkus:dev
```

Once the runtime starts, the payment intelligence endpoints will be available locally.

### Ingest Operational Context

The ingestion phase transforms operational entities and relationships into deterministic runtime context.

Execute the following request:

```bash
curl -X POST http://localhost:8080/pay/ingest \
  -H "Content-Type: application/json" \
  -d '[
    {
      "payment_request":{
        "id":"PAY-1001",
        "amount":"125.00",
        "currency":"USD"
      },
      "customer_account":{
        "id":"CUST-2001",
        "status":"ACTIVE"
      },
      "payment_method":{
        "id":"PM-3001",
        "type":"CARD"
      },
      "risk_profile":{
        "id":"RISK-4001",
        "level":"LOW"
      },
      "merchant_policy":{
        "id":"POL-5001",
        "capture":"AUTO"
      }
    }
  ]'
```

This creates the runtime graph context used during payment decision execution.

### Execute a Runtime Decision

Execute a payment decision against the previously ingested context:

```bash
curl -X POST http://localhost:8080/pay/decision \
  -H "Content-Type: application/json" \
  -d '{
    "paymentRequestFactId":"PaymentRequest:PAY-1001",
    "relatedFactIdsCsv":"CustomerAccount:CUST-2001,PaymentMethod:PM-3001,RiskProfile:RISK-4001,MerchantPolicy:POL-5001"
  }'
```

The runtime returns a governed decision response that includes:

- structured runtime output
- prompt validation state
- LLM response validation state
- domain validation state

### Inspect Governance State

Inspect policy-gate executions for the payment decision flow:

```bash
curl http://localhost:8080/pay/governance/policygate/executions/validate_payment_capture
```

Approve a governed execution:

```bash
curl -X POST http://localhost:8080/pay/governance/policygate/commit/approve \
  -H "Content-Type: application/json" \
  -d '{
    "queryKind":"validate_payment_capture",
    "commitId":"<commit-id>"
  }'
```

Inspect commit audit history:

```bash
curl http://localhost:8080/pay/governance/commitaudit/<commit-id>
```

### Run Functional Integration Tests

Execute the functional runtime lifecycle tests:

```bash
mvn test -Dtest=PayDecisionResourceIT
```

These tests validate:

- ingestion lifecycle behavior
- runtime decision execution
- governance validation flow
- structured operational outcomes

### Run Drift Integration Tests

Execute the drift integration tests:

```bash
mvn test -Dtest=PayDecisionResourceDriftIT
```

These tests repeatedly execute the same runtime decisions and log:

- decision drift
- reason drift
- code drift

across multiple executions.

The goal is not to force identical outputs across runs.

The goal is to observe and bound runtime variability while preserving deterministic operational contracts and semantically stable behavior.

## Current Runtime Status

The `pay-intelligence` extension currently provides a runnable end-to-end intelligence lifecycle across ingestion, query, governance, and drift observation workflows.

The runtime already supports:

- operational context ingestion
- graph-based runtime context modeling
- SQL-like declarative domain query execution
- governed runtime decision flows
- policy-gate execution workflows
- commit approval workflows
- commit audit inspection
- Functional Integration Testing
- Drift Integration Testing
- local Quarkus-based runtime execution

The current implementation is focused on establishing deterministic runtime structure around bounded AI execution behavior.

This includes:

- observable lifecycle execution
- governed operational outcomes
- semantically stable runtime behavior
- repeatable integration testing workflows
- controlled runtime variability through drift evaluation

Current development focus areas include:

- additional intelligence extensions within `system-intelligence-lab`
- expanded governance automation workflows
- broader runtime lifecycle examples
- continued developer experience refinement
- packaging and onboarding improvements

The goal is to evolve intelligence extensions as practical enterprise runtime systems rather than isolated prompt-driven AI demos.