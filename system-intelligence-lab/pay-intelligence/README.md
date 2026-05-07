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