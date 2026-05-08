![LLMDD Architecture](parallax-image.jpg)

## What is Pay-Intelligence?

Pay-Intelligence is an AI-native intelligence extension for payment systems.

It demonstrates how existing enterprise systems can be extended with contextual reasoning, controlled LLM execution, and governed outcomes — without replacing the operational system itself.

This repository is part of the `system-intelligence-lab`, a collection of reference implementations for building intelligence extensions on top of real enterprise workflows.

In this example, a traditional payment processor remains the source of operational truth. It still owns transaction execution, persistence, and payment lifecycle management.

The intelligence layer operates alongside the system.

It ingests operational facts and relationships, shapes deterministic execution context, executes bounded AI decisions, and governs the resulting outcomes before they are committed back into the workflow.

A payment decision, for example, is no longer based only on a single request payload.

Instead, the runtime can evaluate a broader operational context that includes:

- `payment_request`
- `customer_account`
- `payment_method`
- `risk_profile`
- `merchant_policy`

This context is then used to execute a controlled decision such as:

> should this payment capture be approved?

The runtime follows a structured lifecycle:

- Ingestion → operational facts and relationships are transformed into deterministic context
- Query → declarative intent is executed through controlled LLM reasoning
- Governance → outputs are validated, approved, and audited before downstream use

The goal is not to build a chatbot for payments.

The goal is to demonstrate how intelligence extensions can safely augment existing enterprise systems through a deterministic ingestion → query → governance runtime model.

## Extending Existing Payment Systems

Modern payment processors already solve critical operational problems.

They manage transactions, maintain state, execute captures and refunds, integrate with gateways, and act as the operational source of truth for payment workflows.

Pay-Intelligence does not replace these systems.

Instead, it operates as an intelligence extension layered alongside the existing payment infrastructure.

The operational system continues handling deterministic execution, while the intelligence layer introduces contextual reasoning, controlled decisioning, and governed outcomes.

This becomes especially useful when decisions require reasoning across multiple operational entities and policies at the same time.

For example, approving a payment capture may depend not only on the incoming request itself, but also on:

- customer history
- payment method characteristics
- risk posture
- merchant policies
- related operational context

Traditional systems often handle this through static rules, deeply coupled business logic, or fragmented decision pipelines that become difficult to evolve over time.

The intelligence extension introduces a different model.

Operational facts and relationships are ingested into a deterministic runtime, declarative intent is executed through controlled LLM reasoning, and the resulting outcomes are validated and governed before downstream use.

This creates a clean architectural separation:

- the payment processor remains responsible for operational execution
- the intelligence runtime remains responsible for contextual reasoning
- the governance layer remains responsible for controlled acceptance and auditability

The result is an incremental modernization path where intelligence can augment existing enterprise systems without requiring a full platform rewrite.

The following diagram illustrates the high-level runtime lifecycle used throughout the `pay-intelligence` extension.

## Extending Existing Payment Systems

Modern payment processors already solve critical operational problems.

They manage transactions, maintain state, execute captures and refunds, integrate with gateways, and act as the operational source of truth for payment workflows.

Pay-Intelligence does not replace these systems.

Instead, it operates as an intelligence extension layered alongside the existing payment infrastructure.

The operational system continues handling deterministic execution, while the intelligence layer introduces contextual reasoning, controlled decisioning, and governed outcomes.

This becomes especially useful when decisions require reasoning across multiple operational entities and policies at the same time.

For example, approving a payment capture may depend not only on the incoming request itself, but also on:

- customer history
- payment method characteristics
- risk posture
- merchant policies
- related operational context

Traditional systems often handle this through static rules, deeply coupled business logic, or fragmented decision pipelines that become difficult to evolve over time.

The intelligence extension introduces a different model.

Operational facts and relationships are ingested into a deterministic runtime, declarative intent is executed through controlled LLM reasoning, and the resulting outcomes are validated and governed before downstream use.

This creates a clean architectural separation:

- the payment processor remains responsible for operational execution
- the intelligence runtime remains responsible for contextual reasoning
- the governance layer remains responsible for controlled acceptance and auditability

The result is an incremental modernization path where intelligence can augment existing enterprise systems without requiring a full platform rewrite.

The following diagram illustrates the high-level runtime lifecycle used throughout the `pay-intelligence` extension.

```text
+-------------------------------------------------------------+
|                 Existing Payment Processor                  |
|-------------------------------------------------------------|
| transactions | captures | refunds | settlement | gateways  |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                Pay-Intelligence Extension                   |
|-------------------------------------------------------------|
|                                                             |
|  +----------------+                                         |
|  |   Ingestion    |                                         |
|  |----------------|                                         |
|  | facts          |                                         |
|  | relationships  |                                         |
|  | context graph  |                                         |
|  +----------------+                                         |
|           |                                                 |
|           v                                                 |
|  +----------------+                                         |
|  |     Query      |                                         |
|  |----------------|                                         |
|  | declarative    |                                         |
|  | intent         |                                         |
|  | controlled LLM |                                         |
|  +----------------+                                         |
|           |                                                 |
|           v                                                 |
|  +----------------+                                         |
|  |   Governance   |                                         |
|  |----------------|                                         |
|  | validation     |                                         |
|  | approvals      |                                         |
|  | auditability   |                                         |
|  +----------------+                                         |
|                                                             |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                Governed Payment Decision                    |
+-------------------------------------------------------------+

```

## Runtime Lifecycle

The `pay-intelligence` extension operates through a structured runtime lifecycle.

Each phase has a focused responsibility that keeps the system understandable, deterministic in structure, and safe to evolve over time.

The lifecycle is divided into three core phases:

- Ingestion
- Query
- Governance

Together, these phases allow operational systems to be extended with contextual reasoning while maintaining clear architectural boundaries and controlled execution behavior.

### Ingestion

The ingestion phase transforms operational data into deterministic execution context.

Instead of evaluating a payment request in isolation, the runtime ingests operational facts and relationships that together describe the broader system state surrounding a decision.

In the payment domain, this may include entities such as:

- `payment_request`
- `customer_account`
- `payment_method`
- `risk_profile`
- `merchant_policy`

These entities and relationships form the contextual foundation used during decision execution.

The goal of ingestion is not simply data collection.

The goal is to shape operational context into a form that can be reasoned about consistently during runtime execution.

### Query

The query phase executes declarative intent through controlled LLM reasoning.

Developers interact with the runtime through a SQL-like declarative domain query abstraction rather than directly engineering prompts.

This allows business intent to be expressed through structured runtime queries while the underlying execution details remain controlled by the system.

In the payment domain, a query may express intent such as:

> should this payment capture be approved?

The runtime then evaluates the previously ingested operational context and produces a structured decision outcome.

This creates a development model where AI execution behaves more like interacting with a system runtime than manually orchestrating prompts.

### Governance

The governance phase ensures that runtime outcomes are controlled before they are used by downstream systems.

Generated outputs are validated, reviewed through governance controls, and made auditable as part of the execution lifecycle.

This allows the intelligence extension to operate safely within enterprise workflows where reliability, traceability, and operational accountability are required.

Instead of treating governance as an external concern added after execution, governance becomes part of the runtime itself.

Together, the ingestion → query → governance lifecycle creates a deterministic operational model around inherently non-deterministic AI execution.

## Developer Architecture

The `pay-intelligence` extension is organized around the same lifecycle boundaries used throughout the runtime:

- ingestion
- query
- governance

Each layer has a focused responsibility that keeps the system modular, understandable, and easy to evolve over time.

Instead of centering the architecture around prompts or orchestration chains, the extension is structured around operational context, runtime decisioning, and governed outcomes.

### Domain Context Modeling

Traditional backend systems often begin by designing tables and relationships around operational data.

Intelligence extensions follow a similar process, but the focus shifts from storage modeling to runtime reasoning context.

The first step is identifying the central decision object and the surrounding operational entities required to reason about that decision safely.

In the `pay-intelligence` extension, the `payment_request` acts as the central runtime entity.

Supporting operational context is connected around it through related facts and relationships.

    +------------------+
    | customer_account |
    +------------------+
             |
             v
    +----------------+    +-----------------+    +----------------+
    | payment_method | -> | payment_request | <- |  risk_profile  |
    +----------------+    +-----------------+    +----------------+
             ^
             |
    +------------------+
    | merchant_policy  |
    +------------------+

This graph structure forms the runtime context used during decision execution.

Different domains may model their graphs differently depending on operational needs, but the overall runtime pattern remains the same:

- ingest operational facts and relationships
- execute declarative intent against contextual runtime state
- govern the resulting outcomes before downstream use

### Ingestion Layer

The ingestion layer is responsible for transforming operational entities and relationships into deterministic runtime context.

This layer shapes the information that will later be used during decision execution.

### Decision Layer

The decision layer executes runtime intent through a SQL-like declarative domain query abstraction.

Instead of directly engineering prompts, developers interact with the runtime through structured query semantics that express operational intent.

The runtime then evaluates the contextual graph and produces structured decision outcomes.

### Governance Layer

The governance layer ensures that generated outcomes remain controlled and auditable before they are consumed by downstream systems.

Validation, approvals, and execution traceability are treated as part of the runtime architecture itself rather than post-processing concerns.

This separation allows intelligence extensions to integrate safely into enterprise environments while preserving clear operational boundaries between reasoning, execution, and governance.

## Drift and Integration Testing

AI systems behave differently than traditional deterministic software.

The same runtime execution may produce different outputs across runs even when the operational context remains unchanged.

Instead of ignoring this behavior, the `pay-intelligence` extension treats drift as an observable runtime characteristic that must be measured, governed, and bounded operationally.

### Functional Integration Testing

Functional integration testing validates the runtime lifecycle end-to-end.

These tests ensure that:

- ingestion produces valid runtime context
- query execution returns structured outcomes
- governance controls operate correctly
- downstream operational behavior remains consistent

The goal of functional testing is to verify that the runtime behaves correctly as a complete operational system.

### Drift Integration Testing

Drift integration testing evaluates runtime stability across repeated executions.

Instead of assuming identical outputs on every run, the system evaluates whether runtime behavior remains semantically stable within acceptable operational boundaries.

For example, repeated executions may produce outcomes such as:

```json
{"decision":"APPROVE_PAYMENT_CAPTURE"}
```

and:

```json
{"decision":"APPROVE"}
```

While the exact values may differ, the semantic intent of the decision remains operationally consistent.

This allows runtime variability to be observed and measured without breaking deterministic system contracts.

The goal is not to force identical model outputs.

The goal is to ensure that runtime behavior remains bounded, explainable, and operationally safe over time.

### Observable Governance and HITL Workflows

The governance model is designed to keep runtime outcomes observable before downstream mutation or commit.

This allows human-in-the-loop (HITL) workflows to participate in operational decisioning when required by the domain.

Generated outcomes can be reviewed, validated, approved, and audited before they are consumed by downstream systems.

This creates a controlled execution model where governance becomes part of the runtime lifecycle itself rather than an external operational concern.

### Future Direction

Future runtime iterations will introduce additional automated governance capabilities through domain-defined semantic validation rules.

These rules will allow runtime outcomes to participate in bounded approval workflows while preserving observability, auditability, and operational control.

The goal is not to eliminate model variability.

The goal is to bound variability within deterministic operational contracts that remain observable, governable, and safe for enterprise workflows.

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